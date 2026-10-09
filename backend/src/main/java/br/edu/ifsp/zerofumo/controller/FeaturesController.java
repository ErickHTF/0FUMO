package br.edu.ifsp.zerofumo.controller;

import br.edu.ifsp.zerofumo.exception.AssessmentNotCompletedException;
import br.edu.ifsp.zerofumo.exception.UserNotFoundException;
import br.edu.ifsp.zerofumo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/features")
@RequiredArgsConstructor
public class FeaturesController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getFeatures(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException(0L));

        if (!user.isAssessmentCompleted()) {
            throw new AssessmentNotCompletedException();
        }

        return ResponseEntity.ok(Map.of("features", "enabled", "message", "Funcionalidades disponíveis"));
    }
}
