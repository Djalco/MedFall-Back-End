package fr._il.MedFall.auth;

import java.util.List;

public class SecurityConstants {
    public static final List<String> PUBLIC_URLS = List.of(
            "/auth/**",
//            "/user/**",
//            "/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );
}
