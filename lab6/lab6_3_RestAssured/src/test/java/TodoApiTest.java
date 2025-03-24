import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TodoApiTest {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    public void testGetAllTodos() {
        given()
                .when()
                .get("/todos")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetTodoById() {
        given()
                .when()
                .get("/todos/4")
                .then()
                .statusCode(200)
                .body("title", equalTo("et porro tempora"));
    }

    @Test
    public void testListTodosWithSpecificIds() {
        given()
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .body("id", hasItems(198, 199));
    }

    @Test
    public void testResponseTime() {
        long responseTime =
                given()
                        .when()
                        .get("/todos")
                        .then()
                        .extract().time();

        assertTrue(responseTime < 2000);
    }
}
