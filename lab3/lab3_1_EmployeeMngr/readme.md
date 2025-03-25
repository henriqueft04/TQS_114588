
# Lab3 Exercises

## 3.1 Employee Manager Example

### a) Identify a couple of examples that use AssertJ expressive methods chaining.

```java
assertThat(allEmployees).hasSize(3).extracting(Employee::getName).containsOnly(alex.getName(), ron.getName(), bob.getName());

assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
assertThat(response.getBody()).extracting(Employee::getName).containsExactly("bob", "alex");

assertThat(found.getName()).isEqualTo(name);
```

### b) Take note of transitive annotations included in @DataJpaTest.

```java
// @DataJpaTest is a specialized test annotation for JPA tests that automatically configures an in-memory database, 
// sets up entity scanning, and configures the Spring Data JPA repositories.
```

### c) Identify an example in which you mock the behavior of the repository (and avoid involving a database).

```java
@WebMvcTest(EmployeeRestController.class)
class C_EmployeeController_WithMockServiceTest {

    @Autowired
    private MockMvc mvc;    //entry point to the web framework

    @MockBean
    private EmployeeService service;  // Mocking the EmployeeService

    @Test
    void whenPostEmployee_thenCreateEmployee() throws Exception {
        Employee alex = new Employee("alex", "alex@deti.com");

        // Mocking the service layer to avoid involving the repository/database
        when(service.save(Mockito.any())).thenReturn(alex);

        mvc.perform(
                post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(JsonUtils.toJson(alex)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("alex")));

        verify(service, times(1)).save(Mockito.any());  // Verify that the mock service was called
    }

    @Test
    void givenManyEmployees_whenGetEmployees_thenReturnJsonArray() throws Exception {
        Employee alex = new Employee("alex", "alex@deti.com");
        Employee john = new Employee("john", "john@deti.com");
        Employee bob = new Employee("bob", "bob@deti.com");

        List<Employee> allEmployees = Arrays.asList(alex, john, bob);

        // Mocking the service layer to return predefined employees
        when(service.getAllEmployees()).thenReturn(allEmployees);

        mvc.perform(
                get("/api/employees").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is(alex.getName())))
                .andExpect(jsonPath("$[1].name", is(john.getName())))
                .andExpect(jsonPath("$[2].name", is(bob.getName())));

        verify(service, times(1)).getAllEmployees();  // Verify that the mock service was called
    }
}

```

### d) What is the difference between standard @Mock and @MockBean?

- **@Mock** is a shorthand for the `Mockito.mock()` method. This annotation is typically used in a test class to create mock objects. It allows you to create mock objects for classes or interfaces and use these mocks to stub return values for methods or to verify if certain methods were called.

- **@MockBean** is used to add mock objects to the Spring application context. The mock will replace any existing bean of the same type in the application context, making it useful for integration tests where you want to mock dependencies in the Spring context.

### e) What is the role of the file “application-integrationtest.properties”? In which conditions will it be used?

The `application-integrationtest.properties` file is used to configure persistence settings for integration tests. Here, you can define properties such as the database connection, username, password, and Hibernate settings.

Example content of `application-integrationtest.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:33060/tqsdemo
spring.jpa.hibernate.ddl-auto=create-drop
spring.datasource.username=demo
spring.datasource.password=demo
```


### f) The sample project demonstrates three test strategies to assess an API (C, D, and E) developed with SpringBoot. Which are the main/key differences?

The main differences between the three strategies (C, D, and E) for assessing a SpringBoot API are centered around the scope of the application context they load, the level of integration they simulate, and the way they interact with the application's components.

- **Strategy C** uses the `@WebMvcTest` annotation for focused unit tests on the web layer. It **mocks out** other components, making it ideal for testing only the web layer (controllers, etc.) without involving services or repositories.

- **Strategy D** uses the `@SpringBootTest` annotation with `@MockMvc` for integration testing. This strategy involves interactions between different layers of the application (such as the controller, service, and repository layers), while still allowing for the mocking of certain components.

- **Strategy E** uses the `@SpringBootTest` annotation with `@TestRestTemplate` for **end-to-end testing**. This strategy simulates real HTTP requests and responses, ensuring that all parts of the application (controller, service, repository, etc.) work together as expected in a real-world environment.