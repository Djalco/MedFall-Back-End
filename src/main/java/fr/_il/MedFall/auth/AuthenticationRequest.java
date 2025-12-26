package fr._il.MedFall.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AuthenticationRequest {
    @Email(message = "L’adresse e-mail n’est pas bien formatée")
    @NotEmpty(message = "L’adresse e-mail est obligatoire")
    private String email;

    @NotEmpty(message = "Le mot de passe est obligatoire")
    private String password;
}
