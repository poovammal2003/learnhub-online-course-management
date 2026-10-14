
package com.example.onlinecoursesystem;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/admin/login",
                    "/api/admin/logout",
                    "/api/students/register",
                    "/api/students/login",
                    "/api/courses",
                    "/api/courses/**"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/students")
                .permitAll()
                .anyRequest().permitAll()
            )
            .addFilterAfter(new AdminSessionFilter(),
                    AnonymousAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://127.0.0.1:5500",
            "http://localhost:5500"
        ));
        config.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    static class AdminSessionFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain chain)
                throws ServletException, IOException {

            String path = request.getRequestURI();

            if ("GET".equals(request.getMethod())
                    && "/api/students".equals(path)) {

                HttpSession session = request.getSession(false);

                if (session == null ||
                    !Boolean.TRUE.equals(
                        session.getAttribute("adminLoggedIn"))) {

                    response.setStatus(
                        HttpServletResponse.SC_FORBIDDEN
                    );
                    response.getWriter().write(
                        "Admin login required to view students"
                    );
                    return;
                }
            }

            chain.doFilter(request, response);
        }
    }
}
