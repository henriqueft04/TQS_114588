package tqs.hm1114588.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class RestaurantApiIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.data.redis.enabled", () -> "true");
        registry.add("spring.cache.type", () -> "redis");
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void whenCreateRestaurant_thenRestaurantIsCreated() {
        // First create a location
        String locationJson = """
                {
                    "name": "Test Location",
                    "latitude": 40.7128,
                    "longitude": -74.0060
                }
                """;
        
        Integer locationId = given()
                .contentType(ContentType.JSON)
                .body(locationJson)
                .when()
                .post("/api/locations")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
        
        // Then create a restaurant
        String requestBody = String.format("""
                {
                    "name": "Test Restaurant",
                    "location": {
                        "id": %s,
                        "name": "Test Location",
                        "latitude": 40.7128,
                        "longitude": -74.0060
                    },
                    "capacity": 100,
                    "operatingHours": "10:00-22:00",
                    "contactInfo": "123-456-7890",
                    "description": "A test restaurant"
                }
                """, locationId);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/restaurants")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Test Restaurant"))
                .body("capacity", equalTo(100))
                .body("location.id", equalTo(locationId));
    }

    @Test
    void whenGetRestaurantById_thenRestaurantIsReturned() {
        // First create a location
        String locationJson = """
                {
                    "name": "Test Location for Get",
                    "latitude": 40.7128,
                    "longitude": -74.0060
                }
                """;
        
        Integer locationId = given()
                .contentType(ContentType.JSON)
                .body(locationJson)
                .when()
                .post("/api/locations")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
                
        // Then create a restaurant
        String createRequestBody = String.format("""
                {
                    "name": "Test Restaurant for Get",
                    "location": {
                        "id": %s,
                        "name": "Test Location for Get",
                        "latitude": 40.7128,
                        "longitude": -74.0060
                    },
                    "capacity": 100,
                    "operatingHours": "10:00-22:00",
                    "contactInfo": "123-456-7890",
                    "description": "A test restaurant for get"
                }
                """, locationId);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(createRequestBody)
                .when()
                .post("/api/restaurants")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Then get the restaurant by ID
        given()
                .when()
                .get("/api/restaurants/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Test Restaurant for Get"))
                .body("capacity", equalTo(100))
                .body("location.id", equalTo(locationId));
    }

    @Test
    void whenUpdateRestaurant_thenRestaurantIsUpdated() {
        // First create a location
        String locationJson = """
                {
                    "name": "Test Location for Update",
                    "latitude": 40.7128,
                    "longitude": -74.0060
                }
                """;
        
        Integer locationId = given()
                .contentType(ContentType.JSON)
                .body(locationJson)
                .when()
                .post("/api/locations")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
                
        // Then create a restaurant
        String createRequestBody = String.format("""
                {
                    "name": "Test Restaurant for Update",
                    "location": {
                        "id": %s,
                        "name": "Test Location for Update",
                        "latitude": 40.7128,
                        "longitude": -74.0060
                    },
                    "capacity": 100,
                    "operatingHours": "10:00-22:00",
                    "contactInfo": "123-456-7890",
                    "description": "A test restaurant for update"
                }
                """, locationId);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(createRequestBody)
                .when()
                .post("/api/restaurants")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Create a new location for the update
        String newLocationJson = """
                {
                    "name": "Updated Location",
                    "latitude": 41.8781,
                    "longitude": -87.6298
                }
                """;
        
        Integer newLocationId = given()
                .contentType(ContentType.JSON)
                .body(newLocationJson)
                .when()
                .post("/api/locations")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        // Then update the restaurant
        String updateRequestBody = String.format("""
                {
                    "name": "Updated Restaurant",
                    "location": {
                        "id": %s,
                        "name": "Updated Location",
                        "latitude": 41.8781,
                        "longitude": -87.6298
                    },
                    "capacity": 200,
                    "operatingHours": "09:00-23:00",
                    "contactInfo": "987-654-3210",
                    "description": "An updated restaurant"
                }
                """, newLocationId);

        given()
                .contentType(ContentType.JSON)
                .body(updateRequestBody)
                .when()
                .put("/api/restaurants/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Updated Restaurant"))
                .body("capacity", equalTo(200))
                .body("location.id", equalTo(newLocationId));
    }

    @Test
    void whenDeleteRestaurant_thenRestaurantIsDeleted() {
        // First create a location
        String locationJson = """
                {
                    "name": "Test Location for Delete",
                    "latitude": 40.7128,
                    "longitude": -74.0060
                }
                """;
        
        Integer locationId = given()
                .contentType(ContentType.JSON)
                .body(locationJson)
                .when()
                .post("/api/locations")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
                
        // Then create a restaurant
        String createRequestBody = String.format("""
                {
                    "name": "Test Restaurant for Delete",
                    "location": {
                        "id": %s,
                        "name": "Test Location for Delete",
                        "latitude": 40.7128,
                        "longitude": -74.0060
                    },
                    "capacity": 100,
                    "operatingHours": "10:00-22:00",
                    "contactInfo": "123-456-7890",
                    "description": "A test restaurant for delete"
                }
                """, locationId);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(createRequestBody)
                .when()
                .post("/api/restaurants")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Then delete the restaurant
        given()
                .when()
                .delete("/api/restaurants/" + id)
                .then()
                .statusCode(204);

        // Verify the restaurant is deleted
        given()
                .when()
                .get("/api/restaurants/" + id)
                .then()
                .statusCode(404);
    }
} 