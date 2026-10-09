package br.edu.ifsp.zerofumo.controller;

import br.edu.ifsp.zerofumo.dto.ProgressStatsDTO;
import br.edu.ifsp.zerofumo.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping
    public ResponseEntity<ProgressStatsDTO> getStats(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(progressService.getStats(userDetails.getUsername()));
    }
}
