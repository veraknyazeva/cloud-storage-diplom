package ru.netology.cloudstoragediplom.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;
import ru.netology.cloudstoragediplom.entity.User;
import ru.netology.cloudstoragediplom.repository.UserRepository;

import java.util.List;

@Service
@EnableScheduling
@EnableAsync
@Slf4j
public class DbUserDetailsService {

    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public DbUserDetailsService(UserRepository userRepository,
                                UserDetailsService userDetailsService,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Scheduled(fixedRate = 60000)
    @Async
    public void loadUsersFromDatabase() {
        // берем всех пользователей из базы
        List<User> users = userRepository.findAll();

        org.springframework.security.core.userdetails.User.UserBuilder usersBuilder = org.springframework.security.core.userdetails.User.builder()
                .passwordEncoder(passwordEncoder::encode);

        // грузим пользователей в user details spring security
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            String userName = user.getLogin();
            String password = user.getPassword();
            try {
                userDetailsService.loadUserByUsername(userName);
            } catch (Exception ex) {
                // не нашли пользователя и значит создаем
                ((InMemoryUserDetailsManager) userDetailsService).createUser(
                        usersBuilder.username(userName)
                                .password(password)
                                .build()
                );
            }
        }
    }
}
