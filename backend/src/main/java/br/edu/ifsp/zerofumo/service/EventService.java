package br.edu.ifsp.zerofumo.service;

import br.edu.ifsp.zerofumo.dto.EventRequestDTO;
import br.edu.ifsp.zerofumo.dto.EventResponseDTO;
import br.edu.ifsp.zerofumo.entity.Event;
import br.edu.ifsp.zerofumo.entity.User;
import br.edu.ifsp.zerofumo.exception.AssessmentNotCompletedException;
import br.edu.ifsp.zerofumo.exception.UserNotFoundException;
import br.edu.ifsp.zerofumo.repository.EventRepository;
import br.edu.ifsp.zerofumo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventResponseDTO register(String email, EventRequestDTO dto) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(0L));

        if (!user.isAssessmentCompleted()) {
            throw new AssessmentNotCompletedException();
        }

        Event event = Event.builder()
                .user(user)
                .type(dto.getType())
                .intensity(dto.getIntensity())
                .trigger(dto.getTrigger())
                .notes(dto.getNotes())
                .occurredAt(dto.getOccurredAt())
                .build();

        return EventResponseDTO.from(eventRepository.save(event));
    }

    public List<EventResponseDTO> listByUser(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(0L));

        if (!user.isAssessmentCompleted()) {
            throw new AssessmentNotCompletedException();
        }

        return eventRepository.findByUserIdOrderByOccurredAtDesc(user.getId())
                .stream()
                .map(EventResponseDTO::from)
                .toList();
    }
}
