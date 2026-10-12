package br.edu.ifsp.zerofumo.controller;

import br.edu.ifsp.zerofumo.dto.AssessmentRequestDTO;
import br.edu.ifsp.zerofumo.dto.UpdateUserDTO;
import br.edu.ifsp.zerofumo.dto.UserResponseDTO;
import br.edu.ifsp.zerofumo.repository.UserRepository;
import br.edu.ifsp.zerofumo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal UserDetails userDetails) {
        return userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                .map(u -> ResponseEntity.ok(UserResponseDTO.from(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        requireOwner(id, principal);
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody UpdateUserDTO dto
    ) {
        requireOwner(id, principal);
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @PostMapping("/{id}/assessment")
    public ResponseEntity<UserResponseDTO> completeAssessment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody AssessmentRequestDTO dto
    ) {
        requireOwner(id, principal);
        return ResponseEntity.ok(userService.completeAssessment(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        requireOwner(id, principal);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void requireOwner(Long id, UserDetails principal) {
        if (principal == null || !userRepository.findByEmailIgnoreCase(principal.getUsername())
                .map(user -> user.getId().equals(id)).orElse(false)) {
            throw new AccessDeniedException("Acesso permitido apenas à própria conta");
        }
    }
}
