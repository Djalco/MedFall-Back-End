package fr._il.MedFall.auth.services;

import fr._il.MedFall.DTO.User.UserPublicDto;
import fr._il.MedFall.auth.AuthenticationRequest;
import fr._il.MedFall.auth.VerifyPwdRequest;
import fr._il.MedFall.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IAuthenticationService {
    /**
     * Authenticate user and generate JWT token.
     */
    UserPublicDto authenticate(AuthenticationRequest request, HttpServletResponse response);

    /**
     * Find user and send reset code by mail.
     */
    boolean resetPasswordRequest(String email);

    /**
     * Verify reset pwd code.
     */
    User verifyResetPassword(VerifyPwdRequest request);

    /**
     * Reset pwd.
     */
    void resetPassword(User user, String password);

    /**
     * Log out
     */
    Map<String, String> logOut(HttpServletResponse response);

    ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response);
}
