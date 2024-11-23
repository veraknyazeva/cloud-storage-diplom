package ru.netology.cloudstoragediplom.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudstoragediplom.service.JwtService;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String ERROR_LOG_MESSAGE_PATTERN = "Некорректный JWT auth-token! details: {}";
    private static final String AUTH_TOKEN = "auth-token";
    private static final String AUTHORIZATION = "authorization";
    private static final String BEARER = "Bearer";
    private static final int BEARER_BEGIN_INDEX = 7;
    private final JwtService jwtService;

    @Autowired
    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authTokenBearerHeaderValue = request.getHeader(AUTH_TOKEN);
        authTokenBearerHeaderValue = StringUtils.isEmpty(authTokenBearerHeaderValue) ?
                request.getHeader(AUTHORIZATION) : authTokenBearerHeaderValue;
        String username = null;
        String token = null;
        try {
            if (authTokenBearerHeaderValue != null && authTokenBearerHeaderValue.contains(BEARER)) {
                token = authTokenBearerHeaderValue.substring(BEARER_BEGIN_INDEX);
                username = jwtService.extractUsername(token);
            }
        } catch (Exception exception) {
            if (!StringUtils.isEmpty(token)) {
                JwtService.USER_DETAILS_BY_JWT_TOKENS.remove(token);
            }
            log.error(ERROR_LOG_MESSAGE_PATTERN, exception.getMessage());
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = JwtService.USER_DETAILS_BY_JWT_TOKENS.get(token);
            if (Objects.nonNull(userDetails)) {
                try {
                    if (jwtService.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                } catch (Exception ex) {
                    JwtService.USER_DETAILS_BY_JWT_TOKENS.remove(token);
                    log.error(ERROR_LOG_MESSAGE_PATTERN, ex.getMessage());
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
