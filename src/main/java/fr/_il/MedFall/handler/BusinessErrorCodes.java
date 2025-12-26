package fr._il.MedFall.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum BusinessErrorCodes {
    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    INCORRECT_MAIL(300, NOT_FOUND, "L’adresse e-mail saisie ne correspond à aucun compte."),
    INCORRECT_OTP(301, NOT_FOUND, "Le code de vérification saisi est incorrect. Veuillez vérifier et réessayer."),
    EXPIRED_OTP(302, FORBIDDEN, "Le code de vérification a expiré. Veuillez demander un nouveau et réessayer."),
    BAD_CREDENTIALS(303, FORBIDDEN, "Email et/ou mot de passe sont incorrects.");


    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, HttpStatus status, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = status;
    }
}
