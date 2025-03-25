package tqs.lab3_3_carsserviceit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tqs.lab3_3_carsserviceit.data.CarRepository;
import tqs.lab3_3_carsserviceit.model.Car;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create")
class Lab33CarsServiceITApplicationTests {

    @Autowired
    private CarRepository carRepository;

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:15.4")
            .withUsername("demo")
            .withPassword("passefixe")
            .withDatabaseName("tqsdemo");

    @LocalServerPort
    int port;
    Car car1, car2;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }

    @BeforeEach
    public void contextLoads() {
        car1 = new Car("Ford", "Mustang");
        car2 = new Car("Toyota", "Corolla");
        carRepository.save(car1);
        carRepository.save(car2);
    }

    @Test
    void testGetCars() {
        String url = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path("api")
                .path("/cars")
                .path(String.valueOf(car1.getCarId()))
                .build()
                .toUriString();
    }
}
