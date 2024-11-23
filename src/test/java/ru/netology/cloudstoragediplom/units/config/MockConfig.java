package ru.netology.cloudstoragediplom.units.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.netology.cloudstoragediplom.repository.FileRepository;

@TestConfiguration
public class MockConfig {

    @Bean
    public FileRepository fileRepository() {
        return Mockito.mock(FileRepository.class);
    }
}
