package br.edu.ifsp.zerofumo;

import br.edu.ifsp.zerofumo.controller.UserController;
import br.edu.ifsp.zerofumo.dto.AssessmentRequestDTO;
import br.edu.ifsp.zerofumo.dto.UpdateUserDTO;
import br.edu.ifsp.zerofumo.entity.User;
import br.edu.ifsp.zerofumo.repository.UserRepository;
import br.edu.ifsp.zerofumo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserOwnershipTest {
    private UserService service;
    private UserRepository repository;
    private UserController controller;
    private UserDetails principal;

    @BeforeEach
    void setUp() {
        service = mock(UserService.class);
        repository = mock(UserRepository.class);
        controller = new UserController(service, repository);
        principal = org.springframework.security.core.userdetails.User
                .withUsername("owner@example.com").password("unused").roles("USER").build();
        User owner = new User();
        owner.setId(1L);
        when(repository.findByEmailIgnoreCase(principal.getUsername())).thenReturn(Optional.of(owner));
    }

    @Test
    void otherAccountCannotBeReadChangedAssessedOrDeleted() {
        assertThrows(AccessDeniedException.class, () -> controller.findById(2L, principal));
        assertThrows(AccessDeniedException.class, () -> controller.update(2L, principal, new UpdateUserDTO()));
        assertThrows(AccessDeniedException.class, () -> controller.completeAssessment(2L, principal, new AssessmentRequestDTO()));
        assertThrows(AccessDeniedException.class, () -> controller.delete(2L, principal));
        verifyNoInteractions(service);
    }

    @Test
    void ownerCanUseEveryEndpoint() {
        UpdateUserDTO update = new UpdateUserDTO();
        AssessmentRequestDTO assessment = new AssessmentRequestDTO();
        assertEquals(200, controller.findById(1L, principal).getStatusCode().value());
        assertEquals(200, controller.update(1L, principal, update).getStatusCode().value());
        assertEquals(200, controller.completeAssessment(1L, principal, assessment).getStatusCode().value());
        assertEquals(204, controller.delete(1L, principal).getStatusCode().value());
        verify(service).findById(1L);
        verify(service).update(1L, update);
        verify(service).completeAssessment(1L, assessment);
        verify(service).delete(1L);
    }

    @Test
    void missingPrincipalOrDeletedAccountIsDenied() {
        assertThrows(AccessDeniedException.class, () -> controller.findById(1L, null));
        when(repository.findByEmailIgnoreCase(principal.getUsername())).thenReturn(Optional.empty());
        assertThrows(AccessDeniedException.class, () -> controller.findById(1L, principal));
        verifyNoInteractions(service);
    }
}
