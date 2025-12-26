package fr._il.MedFall.auth.services;

import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

public interface IJWTService {
    /**
     * Extract username (subject) from JWT token.
     */
    String extractUsername(String token);

    /**
     * Extract a specific claim from the token.
     */
    <T> T extractClaim(String token, Function<Claims, T> claimResolver);

    /**
     * Extract all claims from the JWT token.
     */
    Claims extractAllClaims(String token);
    /**
     * Generate a JWT token for the given user.
     */
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    String generateRefreshToken(Map<String, Object> extraClaims,UserDetails userDetails);

    String generateNewAccessToken(String oldToken);

    /**
     * Check if the token is valid for the given user.
     */
    boolean isTokenValid(String token, UserDetails userDetails);
    /**
     * Check if the token is expired.
     */
    boolean isTokenExpired(String token) throws ExpiredJwtException ;

    /**
     * Extract the expiration date from the token.
     */
    Date extractExpiration(String token);

    /**
     * Get the signing key for JWT.
     */
    SecretKey getSignInKey();
}
