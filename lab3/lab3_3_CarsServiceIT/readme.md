# how to run the tests
```docker run --name mysql-tqs -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=tqsdemo -p 3306:3306 -d mysql:5.7```

run the tests
```mvn test```

# Advantages and Disadvantages of Using a Real Database in Testing

## Advantages:

1. **Realistic Testing**:
    - **Consistency with Production**: Ensures tests reflect actual behavior in production.

2. **Full Feature Set of the Database**:
    - **Test Database Features**: Allows testing of advanced database features like indexing, queries, triggers.

3. **Database Constraints and Integrity**:
    - **Data Integrity**: Validates that data is persisted correctly, with no issues during insertion or retrieval.

4. **Scaling and Performance Testing**:
    - **Real-World Performance**: Helps identify performance bottlenecks and scalability issues.

## Disadvantages:

1. **Test Speed**:
    - **Slower Tests**: Real databases are slower due to network latency and disk I/O.
    - **Database Initialization**: Setting up and tearing down the database for each test can be time-consuming and it is harder to set up.

2. **Complex Setup**:
    - **Docker and Database Configuration**: Requires proper database setup and management (local or Docker).
    - **Managing Connections**: Extra effort is needed to manage database connections for each test.

3. **Data Management**:
    - **Test Data Issues**: Managing test data in a real database can be more complex than using an in-memory database.
    - **Risk of Polluting Database**: If the database is not properly cleaned, it could affect subsequent tests or even production.
