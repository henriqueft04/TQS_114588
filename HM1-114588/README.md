# Restaurant Management System

A Spring Boot application for managing restaurant reservations, menus, and weather data.

## Prerequisites

- Docker
- Docker Compose

## Getting Started

1. Clone the repository:
```bash
git clone <repository-url>
cd <repository-directory>
```

2. Build and start the containers:
```bash
docker-compose up --build
```

The application will be available at http://localhost:8080

## Services

- **App**: Spring Boot application (port 8080)
- **PostgreSQL**: Database (port 5432)
- **Redis**: Cache (port 6379)

## Environment Variables

The following environment variables are configured in docker-compose.yml:

- `SPRING_DATASOURCE_URL`: PostgreSQL connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_REDIS_HOST`: Redis host
- `SPRING_REDIS_PORT`: Redis port

## API Endpoints

### Reservations
- GET /api/reservations - Get all reservations
- GET /api/reservations/{id} - Get reservation by ID
- POST /api/reservations - Create a new reservation
- PUT /api/reservations/{id} - Update a reservation
- DELETE /api/reservations/{id} - Delete a reservation
- POST /api/reservations/{id}/cancel - Cancel a reservation
- POST /api/reservations/{id}/confirm - Confirm a reservation
- POST /api/reservations/{id}/checkin - Check in a reservation

### Restaurants
- GET /api/restaurants - Get all restaurants
- GET /api/restaurants/{id} - Get restaurant by ID
- POST /api/restaurants - Create a new restaurant
- PUT /api/restaurants/{id} - Update a restaurant
- DELETE /api/restaurants/{id} - Delete a restaurant

### Menus
- GET /api/menus - Get all menus
- GET /api/menus/{id} - Get menu by ID
- POST /api/menus - Create a new menu
- PUT /api/menus/{id} - Update a menu
- DELETE /api/menus/{id} - Delete a menu

### Weather
- GET /api/weather/{city} - Get weather data for a city

## Development

To run the application in development mode:

```bash
./mvnw spring-boot:run
```

## Testing

To run the tests:

```bash
./mvnw test
```

## License

This project is licensed under the MIT License - see the LICENSE file for details. 