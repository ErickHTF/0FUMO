package br.edu.ifsp.zerofumo.exception;

public class AssessmentNotCompletedException extends RuntimeException {

    public AssessmentNotCompletedException() {
        super("Avaliação inicial não concluída. Conclua a avaliação para acessar as funcionalidades.");
    }
}
