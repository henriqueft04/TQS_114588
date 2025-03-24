package tqs.lab6_2_DatabaseMigration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerServiceTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
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
    @Order(1)
    void shouldInsertCustomer() {
        Customer customer = new Customer("John");
        customerRepository.save(customer);

        assertThat(customer.getId()).isNotNull();
    }

    @Test
    @Order(2)
    void shouldRetrieveCustomers() {
        Iterable<Customer> customers = customerRepository.findAll();
        assertThat(customers).asList().hasSize(1);

        Customer customer = customers.iterator().next();
        assertThat(customer.getName()).isEqualTo("John");
    }

    @Test
    @Order(3)
    void shouldUpdateCustomer() {
        Customer customer = customerRepository.findAll().iterator().next();
        customer.setName("Johnny");
        customerRepository.save(customer);

        customer = customerRepository.findById(customer.getId()).get();
        assertThat(customer.getName()).isEqualTo("Johnny");
    }

    @Test
    @Order(4)
    void shouldRetrieveUpdatedCustomer() {
        Customer customer = customerRepository.findAll().iterator().next();
        assertThat(customer.getName()).isEqualTo("Johnny");
    }
}
