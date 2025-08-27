package com.Ankit.Kaarya.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final OtpAuthenticationProvider otpAuthenticationProvider;
    private final JwtUtil jwtUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(otpAuthenticationProvider));
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        JwtAuthFilter filter = new JwtAuthFilter();
        filter.setJwtUtil(jwtUtil);
        return filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/otp/**",
                                "/login/otp/verify", // Explicitly added
                                "/login/**",         // Keep broader pattern
                                "/industry/register",
                                "/users",
                                "/api/payments/update-payment",
                                "/api/payments/**",
                                "/index.html",
                                "/index.html",
                                "/",
                                "/static/**",
                                "/js/**",
                                "/css/**",
                                "/images/**",
                                "/favicon.ico",
                                "/jobApplication/**",
                                "/ws-chat/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        return new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(
                    ServerHttpRequest request,
                    WebSocketHandler wsHandler,
                    Map<String, Object> attributes) {

                // Safely extract token
                String query = request.getURI().getQuery();
                if (query == null || !query.contains("token=")) {
                    return null;
                }

                String[] params = query.split("token=");
                if (params.length < 2) return null;

                String token = params[1].split("&")[0];

                if (jwtUtil.isTokenValid(token)) {
                    return () -> jwtUtil.getUsername(token);
                }
                return null;
            }
        };
    }
}