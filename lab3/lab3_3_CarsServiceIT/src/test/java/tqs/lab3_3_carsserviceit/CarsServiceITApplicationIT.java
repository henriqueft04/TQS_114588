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
import tqs.lab3_3_carsserviceit.data.CarRepository;
import tqs.lab3_3_carsserviceit.model.Car;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties") // Use MySQL
class CarsServiceITApplicationIT {

    @Autowired
    private CarRepository carRepository;

    @LocalServerPort
    int port;
    Car car1, car2;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:mysql://localhost:3306/tqsdemo");
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "password");
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
                .path("/api/cars")
                .build()
                .toUriString();

        // Now, you can make a GET request and assert the response here.
        // (You can use TestRestTemplate to perform a GET request if desired)
    }
}
