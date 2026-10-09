package br.edu.ifsp.zerofumo.service;

import br.edu.ifsp.zerofumo.dto.ProgressStatsDTO;
import br.edu.ifsp.zerofumo.entity.Event;
import br.edu.ifsp.zerofumo.entity.User;
import br.edu.ifsp.zerofumo.exception.AssessmentNotCompletedException;
import br.edu.ifsp.zerofumo.exception.UserNotFoundException;
import br.edu.ifsp.zerofumo.repository.EventRepository;
import br.edu.ifsp.zerofumo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public ProgressStatsDTO getStats(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(0L));

        if (!user.isAssessmentCompleted()) {
            throw new AssessmentNotCompletedException();
        }

        List<Event> events = eventRepository.findByUserIdOrderByOccurredAtDesc(user.getId());

        Map<String, Long> eventsByType = events.stream()
                .collect(Collectors.groupingBy(Event::getType, Collectors.counting()));

        Map<String, Long> eventsByIntensity = events.stream()
                .collect(Collectors.groupingBy(Event::getIntensity, Collectors.counting()));

        Map<String, Long> eventsByTrigger = events.stream()
                .collect(Collectors.groupingBy(Event::getTrigger, Collectors.counting()));

        LocalDateTime lastEventAt = events.isEmpty() ? null : events.get(0).getOccurredAt();

        long daysTracking = 0;
        if (!events.isEmpty()) {
            LocalDateTime firstEvent = events.get(events.size() - 1).getOccurredAt();
            daysTracking = ChronoUnit.DAYS.between(firstEvent, LocalDateTime.now());
        }

        return ProgressStatsDTO.builder()
                .totalEvents(events.size())
                .eventsByType(eventsByType)
                .eventsByIntensity(eventsByIntensity)
                .eventsByTrigger(eventsByTrigger)
                .lastEventAt(lastEventAt)
                .daysTracking(daysTracking)
                .build();
    }
}
