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
@Testcontainers
@ActiveProfiles("test")
public class ReservationApiIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

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
    void whenCreateReservation_thenReservationIsCreated() {
        // First create a location
        String locationJson = """
            {
                "name": "Test Location",
                "latitude": 40.7128,
                "longitude": -74.0060
            }
            """;
        
        Integer locationIdInt = given()
            .contentType(ContentType.JSON)
            .body(locationJson)
            .when()
            .post("/api/locations")
            .then()
            .statusCode(200)
            .extract()
            .path("id");
            
        Long locationId = locationIdInt.longValue();

        // Then create a restaurant
        String restaurantJson = String.format("""
            {
                "name": "Test Restaurant",
                "location": {
                    "id": %s,
                    "name": "Test Location",
                    "latitude": 40.7128,
                    "longitude": -74.0060
                },
                "capacity": 100,
                "availableMenus": 10
            }
            """, locationId);

        Integer restaurantIdInt = given()
            .contentType(ContentType.JSON)
            .body(restaurantJson)
            .when()
            .post("/api/restaurants")
            .then()
            .statusCode(200)
            .extract()
            .path("id");
            
        Long restaurantId = restaurantIdInt.longValue();

        // Then create a reservation
        String reservationJson = String.format("""
            {
                "restaurant": {
                    "id": %d
                },
                "customerName": "John Doe",
                "customerEmail": "john@example.com",
                "customerPhone": "987654321",
                "partySize": 4,
                "reservationTime": "2024-06-20T19:00:00",
                "mealType": "DINNER",
                "specialRequests": "Window seat",
                "isGroupReservation": false,
                "menusRequired": 4
            }
            """, restaurantId);

        given()
            .contentType(ContentType.JSON)
            .body(reservationJson)
            .when()
            .post("/api/reservations")
            .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("customerName", equalTo("John Doe"))
            .body("customerEmail", equalTo("john@example.com"))
            .body("partySize", equalTo(4))
            .body("status", equalTo("PENDING"));
    }

    @Test
    void whenGetReservationById_thenReservationIsReturned() {
        // First create a location
        String locationJson = """
            {
                "name": "Test Location for Get",
                "latitude": 40.7128,
                "longitude": -74.0060
            }
            """;
        
        Integer locationIdInt = given()
            .contentType(ContentType.JSON)
            .body(locationJson)
            .when()
            .post("/api/locations")
            .then()
            .statusCode(200)
            .extract()
            .path("id");
            
        Long locationId = locationIdInt.longValue();

        // Then create a restaurant
        String restaurantJson = String.format("""
            {
                "name": "Test Restaurant for Get",
                "location": {
                    "id": %s,
                    "name": "Test Location for Get",
                    "latitude": 40.7128,
                    "longitude": -74.0060
                },
                "capacity": 100,
                "availableMenus": 10
            }
            """, locationId);

        Integer restaurantIdInt = given()
            .contentType(ContentType.JSON)
            .body(restaurantJson)
            .when()
            .post("/api/restaurants")
            .then()
            .statusCode(200)
            .extract()
            .path("id");
            
        Long restaurantId = restaurantIdInt.longValue();

        // Then create a reservation
        String reservationJson = String.format("""
            {
                "restaurant": {
                    "id": %d
                },
                "customerName": "Test Customer for Get",
                "customerEmail": "testget@example.com",
                "customerPhone": "1234567890",
                "partySize": 4,
                "reservationTime": "2024-06-20T19:00:00",
                "mealType": "DINNER",
                "specialRequests": "No nuts",
                "isGroupReservation": false,
                "menusRequired": 4
            }
            """, restaurantId);

        Integer reservationIdInt = given()
            .contentType(ContentType.JSON)
            .body(reservationJson)
            .when()
            .post("/api/reservations")
            .then()
            .statusCode(200)
            .extract()
            .path("id");
            
        Long reservationId = reservationIdInt.longValue();

        // Then get the reservation by ID
        given()
            .when()
            .get("/api/reservations/" + reservationId)
            .then()
            .statusCode(200)
            .body("id", equalTo(reservationIdInt))
            .body("customerName", equalTo("Test Customer for Get"))
            .body("customerEmail", equalTo("testget@example.com"));
    }

    @Test
    void whenUpdateReservationStatus_thenReservationIsUpdated() {
        // First create a location
        String locationJson = """
            {
                "name": "Test Location for Update",
                "latitude": 40.7128,
                "longitude": -74.0060
            }
            """;
        
        Integer locationIdInt = given()
            .contentType(ContentType.JSON)
            .body(locationJson)
            .when()
            .post("/api/locations")
            .then()
            .statusCode(200)
            .extract()
            .path("id");
            
        Long locationId = locationIdInt.longValue();

        // Then create a restaurant
        String restaurantJson = String.format("""
            {
                "name": "Test Restaurant for Update",
                "location": {
                    "id": %s,
                    "name": "Test Location for Update",
                    "latitude": 40.7128,
                    "longitude": -74.0060
                },
                "capacity": 100,
                "availableMenus": 10
            }
            """, locationId);

        Integer restaurantIdInt = given()
            .contentType(ContentType.JSON)
            .body(restaurantJson)
            .when()
            .post("/api/restaurants")
            .then()
            .statusCode(200)
            .extract()
            .path("id");
            
        Long restaurantId = restaurantIdInt.longValue();

        // Then create a reservation
        String reservationJson = String.format("""
            {
                "restaurant": {
                    "id": %d
                },
                "customerName": "Test Customer for Update",
                "customerEmail": "testupdate@example.com",
                "customerPhone": "1234567890",
                "partySize": 4,
                "reservationTime": "2024-06-20T19:00:00",
                "mealType": "DINNER",
                "specialRequests": "No nuts",
                "isGroupReservation": false,
                "menusRequired": 4
            }
            """, restaurantId);

        Integer reservationIdInt = given()
            .contentType(ContentType.JSON)
            .body(reservationJson)
            .when()
            .post("/api/reservations")
            .then()
            .statusCode(200)
            .extract()
            .path("id");
            
        Long reservationId = reservationIdInt.longValue();

        // Then update the reservation status
        String updateJson = """
            {
                "status": "CONFIRMED"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(updateJson)
            .when()
            .put("/api/reservations/" + reservationIdInt + "/status")
            .then()
            .statusCode(200)
            .body("status", equalTo("CONFIRMED"));
    }

    @Test
    void whenDeleteReservation_thenReservationIsDeleted() {
        // First create a location
        String locationJson = """
            {
                "name": "Test Location for Delete",
                "latitude": 40.7128,
                "longitude": -74.0060
            }
            """;
        
        Integer locationIdInt = given()
            .contentType(ContentType.JSON)
            .body(locationJson)
            .when()
            .post("/api/locations")
            .then()
            .statusCode(200)
            .extract()
            .path("id");
            
        Long locationId = locationIdInt.longValue();

        // Then create a restaurant
        String restaurantJson = String.format("""
            {
                "name": "Test Restaurant for Delete",
                "location": {
                    "id": %s,
                    "name": "Test Location for Delete",
                    "latitude": 40.7128,
                    "longitude": -74.0060
                },
                "capacity": 100,
                "availableMenus": 10
            }
            """, locationId);

        Integer restaurantIdInt = given()
            .contentType(ContentType.JSON)
            .body(restaurantJson)
            .when()
            .post("/api/restaurants")
            .then()
            .statusCode(200)
            .extract()
            .path("id");
            
        Long restaurantId = restaurantIdInt.longValue();

        // Then create a reservation
        String reservationJson = String.format("""
            {
                "restaurant": {
                    "id": %d
                },
                "customerName": "Test Customer for Delete",
                "customerEmail": "testdelete@example.com",
                "customerPhone": "0987654321",
                "partySize": 4,
                "reservationTime": "2024-06-20T19:00:00",
                "mealType": "DINNER",
                "specialRequests": "No nuts",
                "isGroupReservation": false,
                "menusRequired": 4
            }
            """, restaurantId);

        Integer reservationIdInt = given()
            .contentType(ContentType.JSON)
            .body(reservationJson)
            .when()
            .post("/api/reservations")
            .then()
            .statusCode(200)
            .extract()
            .path("id");
            
        Long reservationId = reservationIdInt.longValue();

        // Then delete the reservation
        given()
            .when()
            .delete("/api/reservations/" + reservationIdInt)
            .then()
            .statusCode(204);

        // Verify the reservation is deleted
        given()
            .when()
            .get("/api/reservations/" + reservationIdInt)
            .then()
            .statusCode(404);
    }
} 