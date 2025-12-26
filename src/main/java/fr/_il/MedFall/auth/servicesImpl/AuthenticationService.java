package fr._il.MedFall.auth.servicesImpl;

import fr._il.MedFall.DTO.User.UserPublicDto;
import fr._il.MedFall.auth.AuthenticationRequest;
import fr._il.MedFall.auth.UserPrincipal;
import fr._il.MedFall.auth.VerifyPwdRequest;
import fr._il.MedFall.auth.services.IAuthenticationService;
import fr._il.MedFall.auth.services.IJWTService;
import fr._il.MedFall.entities.User;
import fr._il.MedFall.exceptions.User.ExpiredOtpException;
import fr._il.MedFall.exceptions.User.IncorrectMailException;
import fr._il.MedFall.exceptions.User.IncorrectOtpException;
import fr._il.MedFall.repositories.UserRepository;
import fr._il.MedFall.services.IEmailService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static fr._il.MedFall.handler.BusinessErrorCodes.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements IAuthenticationService {

    private final UserRepository userRepository;
    private final IJWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final IEmailService emailService;
    @Value("${security.jwt.access-token-expiration}")
    private Long accessTokenExpiration;
    @Value("${security.jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Override
    public UserPublicDto authenticate(AuthenticationRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Retrieve authenticated user details
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userPrincipal.getUser();
            UserPublicDto authenticatedUser = new UserPublicDto(user);
            // Prepare claims for JWT
            Map<String, Object> claims = Map.of(
                    "fullName", user.getFullName(),
                    "role", user.getRole().name()
            );

            // Generate JWT token
            String jwtToken = jwtService.generateToken(claims, userPrincipal);
            String refreshToken = jwtService.generateRefreshToken(claims, userPrincipal);

            // Set HttpOnly refresh token cookie
            // Access token cookie
            ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", jwtToken)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(Duration.ofSeconds(accessTokenExpiration))
                    .build();

            // Refresh token cookie
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(Duration.ofDays(refreshTokenExpiration))
                    .build();

            response.addHeader("Set-Cookie", accessTokenCookie.toString());
            response.addHeader("Set-Cookie", refreshTokenCookie.toString());
            System.out.println("response : " + response);
            return authenticatedUser;

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid credentials. Please try again.");
        }
    }

    @Override
    public boolean resetPasswordRequest(String email) {
        User user = userRepository.findByEmailEquals(email).orElseThrow(
                () -> new IncorrectMailException(INCORRECT_MAIL.getDescription())
        );
        String code = AuthenticationService.generateSecureCode();
        user.setResetPwdCode(code);
        emailService.sendResetPwdMail(user.getEmail(), user.getFullName(), code);
        user.setResetPwdDate(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }

    @Override
    public User verifyResetPassword(VerifyPwdRequest request) {
        User user = userRepository.findByEmailEquals(request.getEmail()).orElseThrow(
                () -> new IncorrectMailException(INCORRECT_MAIL.getDescription())
        );
        if (ChronoUnit.MINUTES.between(user.getResetPwdDate(), LocalDateTime.now()) > 15 || user.getResetPwdCode().isEmpty()) {
            throw new ExpiredOtpException(EXPIRED_OTP.getDescription());
        } else {
            if (user.getResetPwdCode().equals(request.getReset_code()))
                return user;
        }
        throw new IncorrectOtpException(INCORRECT_OTP.getDescription());
    }

    @Override
    public void resetPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setResetPwdCode("");
        userRepository.save(user);
    }

    @Override
    public Map<String, String> logOut(HttpServletResponse response) {
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", "qsd")
                .maxAge(0)
                .path("/")
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", "qsd")
                .maxAge(0)
                .path("/")
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .build();

        // Add the cookies to the response
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        // Return a successful response to indicate the user is logged out
        return Map.of("message","Logged out successfully");
    }

    @Override
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = "";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
            try {
                if (!jwtService.isTokenExpired(refreshToken)) {
                    log.info("refresh token valid");
                    String newToken = jwtService.generateNewAccessToken(refreshToken);
                    // Access token cookie
                    ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", newToken)
                            .httpOnly(true)
                            .secure(true)
                            .sameSite("Strict")
                            .path("/")
                            .maxAge(Duration.ofSeconds(accessTokenExpiration))
                            .build();
                    response.addHeader("Set-Cookie", accessTokenCookie.toString());

                    return ResponseEntity.status(HttpStatus.OK).build();
                }
            }catch (ExpiredJwtException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Utils methods
     */
    public static String generateSecureCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < 6; i++) {
            int index = secureRandom.nextInt(characters.length());
            code.append(characters.charAt(index));
        }

        return code.toString();
    }
}
