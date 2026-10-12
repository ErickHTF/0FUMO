package br.edu.ifsp.zerofumo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class MonitorarProgressoIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private String uniqueEmail() {
        return "teste+" + UUID.randomUUID() + "@zerofumo.com";
    }

    private Map<String, Object> registerUser() throws Exception {
        String payload = objectMapper.writeValueAsString(
                Map.of("name", "Teste User", "email", uniqueEmail(), "password", "senha1234"));

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return Map.of(
                "token", body.get("token").asText(),
                "userId", body.get("user").get("id").asLong()
        );
    }

    private String completeAssessment(String token, Long userId) throws Exception {
        String payload = objectMapper.writeValueAsString(
                Map.of("cigsPerDay", 10, "smokingYears", "1-5", "motivation", "health", "dependencyLevel", "medium"));

        mockMvc.perform(post("/api/users/{id}/assessment", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        return token;
    }

    private String eventPayload(String type, String intensity, String trigger, String occurredAt) throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (type != null)       map.put("type", type);
        if (intensity != null)  map.put("intensity", intensity);
        if (trigger != null)    map.put("trigger", trigger);
        if (occurredAt != null) map.put("occurredAt", occurredAt);
        return objectMapper.writeValueAsString(map);
    }

    private void registerEvent(String token, String type, String intensity, String trigger,
                               String occurredAt) throws Exception {
        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventPayload(type, intensity, trigger, occurredAt)))
                .andExpect(status().isCreated());
    }

    /** Returns an ISO-8601 local date-time {@code days} days before now, with fixed non-zero seconds. */
    private String daysAgo(long days) {
        return LocalDateTime.now()
                .minusDays(days)
                .minusHours(1)
                .withSecond(15)
                .withNano(0)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // ------------------------------------------------------------------ //
    //  Main flow                                                          //
    // ------------------------------------------------------------------ //

    @Test
    void getStats_shouldAggregateAllUserEvents() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");
        completeAssessment(token, userId);

        String firstEventAt = daysAgo(3);
        String lastEventAt  = daysAgo(0);

        registerEvent(token, "fissura",  "alta",  "estresse", firstEventAt);
        registerEvent(token, "resistiu", "alta",  "estresse", daysAgo(2));
        registerEvent(token, "resistiu", "media", "tedio",    daysAgo(1));
        registerEvent(token, "fumou",    "baixa", "social",   lastEventAt);

        mockMvc.perform(get("/api/progress")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").value(4))
                .andExpect(jsonPath("$.eventsByType.length()").value(3))
                .andExpect(jsonPath("$.eventsByType.fissura").value(1))
                .andExpect(jsonPath("$.eventsByType.resistiu").value(2))
                .andExpect(jsonPath("$.eventsByType.fumou").value(1))
                .andExpect(jsonPath("$.eventsByIntensity.length()").value(3))
                .andExpect(jsonPath("$.eventsByIntensity.alta").value(2))
                .andExpect(jsonPath("$.eventsByIntensity.media").value(1))
                .andExpect(jsonPath("$.eventsByIntensity.baixa").value(1))
                .andExpect(jsonPath("$.eventsByTrigger.length()").value(3))
                .andExpect(jsonPath("$.eventsByTrigger.estresse").value(2))
                .andExpect(jsonPath("$.eventsByTrigger.tedio").value(1))
                .andExpect(jsonPath("$.eventsByTrigger.social").value(1))
                .andExpect(jsonPath("$.lastEventAt").value(lastEventAt))
                .andExpect(jsonPath("$.daysTracking").value(3));
    }

    @Test
    void getStatsWithoutEvents_shouldReturnZerosAndEmptyMaps() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");
        completeAssessment(token, userId);

        mockMvc.perform(get("/api/progress")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").value(0))
                .andExpect(jsonPath("$.eventsByType").isMap())
                .andExpect(jsonPath("$.eventsByType").isEmpty())
                .andExpect(jsonPath("$.eventsByIntensity").isMap())
                .andExpect(jsonPath("$.eventsByIntensity").isEmpty())
                .andExpect(jsonPath("$.eventsByTrigger").isMap())
                .andExpect(jsonPath("$.eventsByTrigger").isEmpty())
                .andExpect(jsonPath("$.lastEventAt").value(nullValue()))
                .andExpect(jsonPath("$.daysTracking").value(0));
    }

    @Test
    void getStats_shouldOnlyCountAuthenticatedUserEvents() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");
        completeAssessment(token, userId);

        var otherSession = registerUser();
        String otherToken  = (String) otherSession.get("token");
        Long   otherUserId = (Long)   otherSession.get("userId");
        completeAssessment(otherToken, otherUserId);

        registerEvent(token, "resistiu", "media", "tedio", daysAgo(1));

        registerEvent(otherToken, "fumou",   "alta", "social",   daysAgo(5));
        registerEvent(otherToken, "fissura", "alta", "estresse", daysAgo(0));

        mockMvc.perform(get("/api/progress")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").value(1))
                .andExpect(jsonPath("$.eventsByType.length()").value(1))
                .andExpect(jsonPath("$.eventsByType.resistiu").value(1))
                .andExpect(jsonPath("$.eventsByType.fumou").doesNotExist())
                .andExpect(jsonPath("$.eventsByType.fissura").doesNotExist())
                .andExpect(jsonPath("$.eventsByIntensity.length()").value(1))
                .andExpect(jsonPath("$.eventsByIntensity.media").value(1))
                .andExpect(jsonPath("$.eventsByTrigger.length()").value(1))
                .andExpect(jsonPath("$.eventsByTrigger.tedio").value(1))
                .andExpect(jsonPath("$.daysTracking").value(1));

        mockMvc.perform(get("/api/progress")
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").value(2))
                .andExpect(jsonPath("$.eventsByType.resistiu").doesNotExist());
    }

    // ------------------------------------------------------------------ //
    //  Security                                                           //
    // ------------------------------------------------------------------ //

    @Test
    void getStatsWithoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/progress"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getStatsBeforeAssessment_shouldReturn403() throws Exception {
        var session = registerUser();
        String token = (String) session.get("token");

        mockMvc.perform(get("/api/progress")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
}
