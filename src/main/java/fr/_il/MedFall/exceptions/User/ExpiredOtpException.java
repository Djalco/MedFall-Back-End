package fr._il.MedFall.exceptions.User;

public class ExpiredOtpException extends RuntimeException{
    public ExpiredOtpException(String message) {
        super(message);
    }
}
