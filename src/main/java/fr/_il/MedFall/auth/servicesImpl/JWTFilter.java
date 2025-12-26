package fr._il.MedFall.auth.servicesImpl;

import fr._il.MedFall.auth.SecurityConstants;
import fr._il.MedFall.auth.services.IJWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.AntPathMatcher;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final IJWTService jwtService;
    private final UserDetailsService userDetailsService;
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean isPublic = SecurityConstants.PUBLIC_URLS.stream().anyMatch(p -> MATCHER.match(p, path));
        if (log.isDebugEnabled()) {
            log.debug("JWT Filter -> path: {}, shouldNotFilter(public): {}", path, isPublic);
        }
        return isPublic;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Extract JWT token from Authorization header
        String jwt = extractJwtFromRequest(request);
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract username from JWT token securely
        String userEmail;
        try {
            userEmail = jwtService.extractUsername(jwt);

        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return; // Invalid or expired token
        }

        // Proceed only if the user is not already authenticated
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            // Validate the token before setting authentication
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

//    /**
//     * Determines whether the filter should be skipped for certain paths.
//     */
//    private boolean shouldSkipFilter(HttpServletRequest request) {
//        String path = request.getServletPath();
//        return SecurityConstants.PUBLIC_URLS.stream().anyMatch(pattern -> path.matches(convertAntToRegex(pattern)));
//    }
//
//    private String convertAntToRegex(String antPattern) {
//        return antPattern
//                .replace("**", ".*")
//                .replace("*", "[^/]*");
//    }

    /**
     * Safely extracts the JWT token from the Authorization header.
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();  // Return the value of the access token cookie
                }
            }
        }
        return null;
    }
}
