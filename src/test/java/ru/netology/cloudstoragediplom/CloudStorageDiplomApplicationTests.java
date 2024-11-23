package ru.netology.cloudstoragediplom;

import lombok.SneakyThrows;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudstoragediplom.dto.login.LoginRequest;
import ru.netology.cloudstoragediplom.dto.login.LoginResponse;
import ru.netology.cloudstoragediplom.entity.User;
import ru.netology.cloudstoragediplom.repository.FileRepository;
import ru.netology.cloudstoragediplom.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = {CloudStorageDiplomApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CloudStorageDiplomApplicationTests {

    static {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    @Container
//    @ServiceConnection
    public static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:12");
    public static final String ORIGIN = "http://localhost:8080";

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> POSTGRE_SQL_CONTAINER.getJdbcUrl());
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
        registry.add("spring.liquibase.enabled", () -> true);
        registry.add("spring.datasource.hikari.schema", () -> "public");
    }

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();
    private static String authToken = "";
    private static final String LOGIN_URL = "http://localhost:8081/login";
    private static final String FILE_LIST_URL = "http://localhost:8081/list?limit=5";
    private static final String FILE_URL = "http://localhost:8081/file";
    private static final String FILE_NAME_PARAM_NAME = "?filename=";
    private static final String HASH_PART_NAME = "hash";
    private static final String FILE_PART_NAME = "file";
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${service.props.required-header-name}")
    private String authTokenHeaderName;

    @Value("classpath:test.txt")
    private Resource testFileSystemResource;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileRepository fileRepository;

    @Test
    @Order(1)
    public void login_controller_test() {
        List<User> users = userRepository.findAll();
        User user1 = users.get(0);

        LoginRequest request = new LoginRequest();
        request.setLogin(user1.getLogin());
        request.setPassword(user1.getPassword());

        ResponseEntity<LoginResponse> response =
                testRestTemplate.postForEntity(LOGIN_URL, request, LoginResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();

        LoginResponse loginResponse = response.getBody();
        assertThat(loginResponse.getAuthToken()).isNotNull();
        assertThat(loginResponse.getAuthToken()).isNotBlank();
        authToken = BEARER_PREFIX + loginResponse.getAuthToken();
    }


    @SneakyThrows
    @Test
    @Order(2)
    public void upload_file_test() {
        assertThat(authToken).isNotBlank();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(authTokenHeaderName, authToken);
        httpHeaders.setOrigin(ORIGIN);
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> multipart = new LinkedMultiValueMap<>();
        multipart.add(HASH_PART_NAME, "oasdfksdjgnskg");
        multipart.add(FILE_PART_NAME, new FileSystemResource(testFileSystemResource.getFile()));

        HttpEntity<MultiValueMap<String, Object>> request
                = new HttpEntity<>(multipart, httpHeaders);

        String fullUrl = FILE_URL + FILE_NAME_PARAM_NAME + testFileSystemResource.getFilename();

        ResponseEntity<Void> response =
                testRestTemplate.postForEntity(fullUrl, request, Void.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }


    @Test
    @Order(3)
    public void get_file_list_repository_test() {
        assertThat(authToken).isNotBlank();

        assertThat(fileRepository.findAll().isEmpty()).isFalse();
    }

    @Test
    @Order(4)
    public void logout_test() {
        assertThat(authToken).isNotBlank();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(authTokenHeaderName, authToken);
        httpHeaders.setOrigin(ORIGIN);

        HttpEntity<Void> requestEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Void> response =
                testRestTemplate.exchange(LOGIN_URL,
                        HttpMethod.GET,
                        requestEntity,
                        Void.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @Order(5)
    public void error_401_test() {
        assertThat(authToken).isNotBlank();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(authTokenHeaderName, authToken);
        httpHeaders.setOrigin(ORIGIN);

        HttpEntity<Void> requestEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Void> response =
                testRestTemplate.exchange(FILE_LIST_URL,
                        HttpMethod.GET,
                        requestEntity,
                        Void.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull();
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }
}
