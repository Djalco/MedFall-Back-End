package fr._il.MedFall.exceptions.User;

public class IncorrectOtpException extends RuntimeException {
    public IncorrectOtpException(String message) {
        super(message);
    }
}
