package com.example.spacecatsmarket.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.example.spacecatsmarket.util.SecurityUtil.API_KEY_HEADER;

public class CosmicKeyFilter extends OncePerRequestFilter {

    private final BearerTokenResolver bearerTokenResolver = new HeaderBearerTokenResolver(API_KEY_HEADER);

    private final BearerTokenAuthenticationEntryPoint authenticationEntryPoint = new BearerTokenAuthenticationEntryPoint();

    private final AuthenticationFailureHandler authenticationFailureHandler = new AuthenticationEntryPointFailureHandler(authenticationEntryPoint);

    private final AuthenticationProvider jwtAuthenticationProvider;

    public CosmicKeyFilter(JwtDecoder jwtDecoder) {
        this.jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token;
        try {
            token = this.bearerTokenResolver.resolve(request);
        } catch (OAuth2AuthenticationException invalid) {
            this.logger.trace("Failed to resolve bearer token", invalid);
            this.authenticationEntryPoint.commence(request, response, invalid);
            return;
        }
        if (token == null) {
            this.logger.trace("No bearer token present");
            this.authenticationEntryPoint.commence(request, response, null);
            return;
        }

        try {
            BearerTokenAuthenticationToken authenticationRequest = new BearerTokenAuthenticationToken(token);
            Authentication authenticate = jwtAuthenticationProvider.authenticate(authenticationRequest);
            if (!authenticate.isAuthenticated()) {
                this.logger.trace("Failed to authenticate authentication request: " + authenticate);
                this.authenticationFailureHandler.onAuthenticationFailure(request, response, new OAuth2AuthenticationException("Unauthenticated"));
            } else {
                this.logger.trace("Successfully authenticated authentication request: " + authenticate);
                filterChain.doFilter(request, response);
            }
        } catch (AuthenticationException failed) {
            this.logger.trace("Failed to process authentication request", failed);
            this.authenticationFailureHandler.onAuthenticationFailure(request, response, failed);
        }
    }

}

