package tqs.hm1114588.steps;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import tqs.hm1114588.model.User;
import tqs.hm1114588.service.UserService;

@SpringBootTest
@ContextConfiguration
public class UserSteps {

    @Autowired
    private UserService userService;

    private User user;
    private List<User> userList;
    private Exception exception;

    @When("I create a user with the following details:")
    public void i_create_a_user_with_the_following_details(DataTable dataTable) {
        Map<String, String> details = dataTable.asMap();
        try {
            user = userService.createUser(
                details.get("Email"),
                details.get("Name"),
                details.get("Role")
            );
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the user should be created successfully")
    public void the_user_should_be_created_successfully() {
        assertNotNull(user);
        assertNull(exception);
    }

    @Then("the user's role should be {string}")
    public void the_user_s_role_should_be(String role) {
        assertEquals(role, user.getRole());
    }

    @Given("a user exists with email {string} and name {string} and role {string}")
    public void a_user_exists_with_email_and_name_and_role(String email, String name, String role) {
        user = userService.createUser(email, name, role);
        assertNotNull(user);
    }

    @When("I search for a user with email {string}")
    public void i_search_for_a_user_with_email(String email) {
        try {
            Optional<User> result = userService.findByEmail(email);
            user = result.orElse(null);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("I should find a user with name {string}")
    public void i_should_find_a_user_with_name(String name) {
        assertNotNull(user);
        assertEquals(name, user.getName());
    }

    @Given("the following users exist:")
    public void the_following_users_exist(DataTable dataTable) {
        List<Map<String, String>> users = dataTable.asMaps();
        for (Map<String, String> userMap : users) {
            userService.createUser(
                userMap.get("Email"),
                userMap.get("Name"),
                userMap.get("Role")
            );
        }
    }

    @When("I search for users with role {string}")
    public void i_search_for_users_with_role(String role) {
        try {
            userList = userService.findByRole(role);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("I should find {int} users")
    public void i_should_find_users(Integer count) {
        assertNotNull(userList);
        assertEquals(count, userList.size());
    }

    @Then("the results should include {string} and {string}")
    public void the_results_should_include_and(String name1, String name2) {
        List<String> names = userList.stream()
            .map(User::getName)
            .toList();
        assertTrue(names.contains(name1));
        assertTrue(names.contains(name2));
    }
} 