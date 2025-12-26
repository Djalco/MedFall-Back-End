package fr._il.MedFall.auth.controllers;

import fr._il.MedFall.DTO.User.UserPublicDto;
import fr._il.MedFall.auth.AuthenticationRequest;
import fr._il.MedFall.auth.ResetPwdRequest;
import fr._il.MedFall.auth.VerifyPwdRequest;
import fr._il.MedFall.auth.services.IAuthenticationService;
import fr._il.MedFall.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final IAuthenticationService authenticationService;

    /**
     * Auth method
     */
    @PostMapping("/login")
    public UserPublicDto authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        return authenticationService.authenticate(request, response);
    }

    /**
     * Request for reset pwd code
     */
    @PostMapping("/reset-pwd-request")
    public ResponseEntity<Map<String, String>> resetPasswordRequest(@RequestParam String email) {
        return authenticationService.resetPasswordRequest(email) ?
                ResponseEntity.ok().body(Map.of("status", "success", "message", "A mail has been sent to you"))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "error", "message", "Invalid email"));
    }

    /**
     * Verify reset pwd code
     */
    @PostMapping("/verify-reset-pwd")
    public ResponseEntity<Map<String, String>> verifyResetPassword(@RequestBody VerifyPwdRequest request) {
        User user = authenticationService.verifyResetPassword(request);
        return user != null ?
                ResponseEntity.ok().body(Map.of("status", "success", "message", "success"))
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "error", "message", "Incorrect mail and/or code"));
    }

    /**
     * Reset pwd
     */
    @PostMapping("/reset-pwd")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPwdRequest request) {
        VerifyPwdRequest verifyRequest = new VerifyPwdRequest(request.getEmail(), request.getReset_code());
        User user = authenticationService.verifyResetPassword(verifyRequest);
        if (user != null) {
            authenticationService.resetPassword(user, request.getPassword());
            return ResponseEntity.ok().body(Map.of("status", "success", "message", "success"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "error", "message", "Failed to reset password"));
    }

    /**
     * Log out
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logOut(HttpServletResponse response) {
        return ResponseEntity.ok(authenticationService.logOut(response));
    }

    /**
     * Refresh token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return authenticationService.refreshToken(request, response);
    }
}
