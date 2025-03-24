package tqs.lab6_2_DatabaseMigration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class CustomerServiceFlywayTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeAll
    static void beforeAll() {
        postgres.start();

        // Check if Flyway applied migrations
        Flyway flyway = Flyway.configure().dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()).load();
        flyway.migrate();
    }


    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldRetrieveSampleCustomers() {
        // Retrieve all customers from the database
        Iterable<Customer> customers = customerRepository.findAll();

        // Log the customers to verify
        customers.forEach(customer -> System.out.println(customer.getName()));

        // Ensure that there are 3 customers (as per the migration script)
        assertThat(customers).asList().hasSize(3);

        // Validate that the sample data is correctly inserted
        boolean hasJohn = false;
        boolean hasJane = false;
        boolean hasAlice = false;

        for (Customer customer : customers) {
            if ("John".equals(customer.getName())) {
                hasJohn = true;
            } else if ("Jane".equals(customer.getName())) {
                hasJane = true;
            } else if ("Alice".equals(customer.getName())) {
                hasAlice = true;
            }
        }

        assertThat(hasJohn).isTrue();
        assertThat(hasJane).isTrue();
        assertThat(hasAlice).isTrue();
    }

}