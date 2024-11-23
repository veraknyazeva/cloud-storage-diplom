package ru.netology.cloudstoragediplom.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudstoragediplom.dto.login.LoginRequest;
import ru.netology.cloudstoragediplom.dto.login.LoginResponse;
import ru.netology.cloudstoragediplom.exeption.LoginFailedException;
import ru.netology.cloudstoragediplom.service.JwtService;


@RestController
@RequestMapping("/")
@Slf4j
@CrossOrigin(origins = {"http://localhost:*"})
public class LoginController {

    private static final String UNSUCCESS_ENTER_MSG = "Неудачная попытка входа! Details: ";
    private static final String BEARER = "Bearer";
    private static final int BEARER_BEGIN_INDEX = 7;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginController(JwtService jwtService,
                           UserDetailsService userDetailsService,
                           AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        try {

            final String username = loginRequest.getLogin();
            final String password = loginRequest.getPassword();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                return LoginResponse.builder()
                        .authToken(jwtService.generateToken(username, userDetails))
                        .build();
            } else {
                throw new UsernameNotFoundException("invalid login or password");
            }
        } catch (Exception exception) {
            String errorMessage = UNSUCCESS_ENTER_MSG + exception.getMessage();
            log.error(errorMessage);
            throw new LoginFailedException(errorMessage);
        }
    }

    @GetMapping("/login")
    public ResponseEntity<Void> logout(@RequestHeader(name = "${service.props.required-header-name}", required = false) String authToken,
                                       @RequestParam(name = "logout", required = false) String logout) {

        if (!StringUtils.isEmpty(authToken) && authToken.contains(BEARER)) {
            final var token = authToken.substring(BEARER_BEGIN_INDEX);
            JwtService.USER_DETAILS_BY_JWT_TOKENS.remove(token);
            try {
                String username = jwtService.extractUsername(token);//проверяем корректность токена еще раз
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                authentication.setAuthenticated(false);
            } catch (Exception exception) {
                log.warn(exception.getMessage());
            }
        }

        return ResponseEntity.ok().build();
    }
}
