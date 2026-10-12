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

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class SugerirRecursosIntegrationTest {

    /** Number of resources seeded by data.sql (3 per trigger: estresse, social, tedio, apos_refeicao, outro). */
    private static final int SEEDED_RESOURCES = 15;

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

    // ------------------------------------------------------------------ //
    //  Main flow                                                          //
    // ------------------------------------------------------------------ //

    @Test
    void listAll_shouldReturnSeededResourcesWithAllFields() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");
        completeAssessment(token, userId);

        mockMvc.perform(get("/api/relaxation-resources")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(SEEDED_RESOURCES))
                .andExpect(jsonPath("$[*].id", everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].title", everyItem(not(emptyOrNullString()))))
                .andExpect(jsonPath("$[*].description", everyItem(not(emptyOrNullString()))))
                .andExpect(jsonPath("$[*].category", everyItem(not(emptyOrNullString()))))
                .andExpect(jsonPath("$[*].trigger", everyItem(
                        is(oneOf("estresse", "social", "tedio", "apos_refeicao", "outro")))))
                .andExpect(jsonPath("$[*].title", hasItems(
                        "Respiração 4-7-8", "Conversar com alguém", "Jogo no celular",
                        "Lavar os dentes", "Meditação guiada")))
                .andExpect(jsonPath("$[?(@.title == 'Respiração 4-7-8')].category").value("respiração"))
                .andExpect(jsonPath("$[?(@.title == 'Respiração 4-7-8')].trigger").value("estresse"));
    }

    @Test
    void getByTrigger_shouldReturnOnlyResourcesForThatTrigger() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");
        completeAssessment(token, userId);

        mockMvc.perform(get("/api/relaxation-resources")
                        .param("trigger", "estresse")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].trigger", everyItem(is("estresse"))))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder(
                        "Respiração 4-7-8", "Caminhada de 5 minutos", "Beber água gelada")));
    }

    @Test
    void getByTrigger_shouldBeCaseInsensitive() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");
        completeAssessment(token, userId);

        mockMvc.perform(get("/api/relaxation-resources")
                        .param("trigger", "APOS_Refeicao")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].trigger", everyItem(is("apos_refeicao"))))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder(
                        "Beber água com limão", "Lavar os dentes", "Caminhar após comer")));
    }

    @Test
    void getByUnknownTrigger_shouldReturnEmptyList() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");
        completeAssessment(token, userId);

        mockMvc.perform(get("/api/relaxation-resources")
                        .param("trigger", "gatilho_inexistente")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ------------------------------------------------------------------ //
    //  Security                                                           //
    // ------------------------------------------------------------------ //

    @Test
    void listAllWithoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/relaxation-resources"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getByTriggerWithoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/relaxation-resources")
                        .param("trigger", "estresse"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listAllBeforeAssessment_shouldReturn403() throws Exception {
        var session = registerUser();
        String token = (String) session.get("token");

        mockMvc.perform(get("/api/relaxation-resources")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void getByTriggerBeforeAssessment_shouldReturn403() throws Exception {
        var session = registerUser();
        String token = (String) session.get("token");

        mockMvc.perform(get("/api/relaxation-resources")
                        .param("trigger", "estresse")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
}
