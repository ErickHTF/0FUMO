package br.edu.ifsp.zerofumo.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Usuário não encontrado: " + id);
    }
}
