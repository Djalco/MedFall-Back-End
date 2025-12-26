package fr._il.MedFall.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPwdRequest {

    @Email(message = "Email is not well formatted")
    @NotEmpty(message = "Email is mandatory")
    @NotNull(message = "Email is mandatory")
    private String email;

    @NotEmpty(message = "Reset code is mandatory")
    @NotNull(message = "Reset code is mandatory")
    private String reset_code;

    @NotEmpty(message = "Password is mandatory")
    @NotNull(message = "Password is mandatory")
    private String password;
}
