package fr._il.MedFall.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VerifyPwdRequest {
    @Email(message = "L’adresse e-mail n’est pas bien formatée")
    @NotEmpty(message = "L’adresse e-mail est obligatoire")
    private String email;

    @NotEmpty(message = "Le code est obligatoire")
    private String reset_code;
}
