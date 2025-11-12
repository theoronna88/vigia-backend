package br.com.ronna.vigia.exceptions;

public class MaxStudentsException extends RuntimeException {
    public MaxStudentsException(String message) {
        super(message);
    }
}
