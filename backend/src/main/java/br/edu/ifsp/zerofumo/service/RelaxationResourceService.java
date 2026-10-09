package br.edu.ifsp.zerofumo.service;

import br.edu.ifsp.zerofumo.dto.RelaxationResourceResponseDTO;
import br.edu.ifsp.zerofumo.entity.User;
import br.edu.ifsp.zerofumo.exception.AssessmentNotCompletedException;
import br.edu.ifsp.zerofumo.exception.UserNotFoundException;
import br.edu.ifsp.zerofumo.repository.RelaxationResourceRepository;
import br.edu.ifsp.zerofumo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelaxationResourceService {

    private final RelaxationResourceRepository relaxationResourceRepository;
    private final UserRepository userRepository;

    public List<RelaxationResourceResponseDTO> getByTrigger(String email, String trigger) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(0L));

        if (!user.isAssessmentCompleted()) {
            throw new AssessmentNotCompletedException();
        }

        return relaxationResourceRepository.findByTriggerIgnoreCase(trigger)
                .stream()
                .map(RelaxationResourceResponseDTO::from)
                .toList();
    }

    public List<RelaxationResourceResponseDTO> listAll(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(0L));

        if (!user.isAssessmentCompleted()) {
            throw new AssessmentNotCompletedException();
        }

        return relaxationResourceRepository.findAll()
                .stream()
                .map(RelaxationResourceResponseDTO::from)
                .toList();
    }
}
