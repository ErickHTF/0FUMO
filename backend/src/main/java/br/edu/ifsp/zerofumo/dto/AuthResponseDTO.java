package br.edu.ifsp.zerofumo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private UserResponseDTO user;
}
