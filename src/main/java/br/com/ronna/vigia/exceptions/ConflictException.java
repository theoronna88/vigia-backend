package br.com.ronna.vigia.exceptions;

/**
 * Exceção lançada quando há conflito de estado no negócio
 * Por exemplo: tentar deletar uma turma que possui matrículas ativas
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
