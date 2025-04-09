-- Create all tables in a single migration

-- Weather tables
CREATE TABLE IF NOT EXISTS locations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS weather_data (
    id BIGSERIAL PRIMARY KEY,
    location_id BIGINT NOT NULL REFERENCES locations(id),
    temperature DOUBLE PRECISION NOT NULL,
    humidity DOUBLE PRECISION NOT NULL,
    wind_speed_km DOUBLE PRECISION,
    wind_direction_id INTEGER,
    precipitation DOUBLE PRECISION,
    pressure DOUBLE PRECISION,
    radiation DOUBLE PRECISION,
    station_id VARCHAR(255),
    timestamp TIMESTAMP NOT NULL,
    forecast_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_location_date UNIQUE (location_id, forecast_date)
);

-- Restaurant tables
CREATE TABLE IF NOT EXISTS restaurants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location_id BIGINT NOT NULL REFERENCES locations(id),
    capacity INTEGER NOT NULL,
    description TEXT NOT NULL,
    operating_hours VARCHAR(255) NOT NULL,
    contact_info VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users table needs to be created before reservations due to foreign key relationship
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(20) NOT NULL,
    staff_role VARCHAR(50),
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    restaurant_id BIGINT REFERENCES restaurants(id),
    hire_date TIMESTAMP,
    employee_id VARCHAR(255)
);

-- Schedule table for restaurant operating hours
CREATE TABLE IF NOT EXISTS schedules (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id BIGINT NOT NULL REFERENCES restaurants(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE IF NOT EXISTS meals (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id BIGINT NOT NULL REFERENCES restaurants(id),
    schedule_id BIGINT REFERENCES schedules(id),
    name VARCHAR(255) NOT NULL,
    meal_type VARCHAR(50) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
    CONSTRAINT fk_schedule FOREIGN KEY (schedule_id) REFERENCES schedules(id)
);

CREATE TABLE IF NOT EXISTS menus (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    date DATE NOT NULL,
    meal_id BIGINT NOT NULL REFERENCES meals(id),
    is_available BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_meal FOREIGN KEY (meal_id) REFERENCES meals(id)
);

CREATE TABLE IF NOT EXISTS dishes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Join table for menu-dish many-to-many relationship
CREATE TABLE IF NOT EXISTS menu_dishes (
    menu_id BIGINT NOT NULL REFERENCES menus(id),
    dish_id BIGINT NOT NULL REFERENCES dishes(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (menu_id, dish_id),
    CONSTRAINT fk_menu FOREIGN KEY (menu_id) REFERENCES menus(id),
    CONSTRAINT fk_dish FOREIGN KEY (dish_id) REFERENCES dishes(id)
);

CREATE TABLE IF NOT EXISTS reservations (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id BIGINT NOT NULL REFERENCES restaurants(id),
    user_id BIGINT REFERENCES users(id),
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    party_size INTEGER NOT NULL,
    reservation_time TIMESTAMP NOT NULL,
    meal_type VARCHAR(50) NOT NULL,
    special_requests TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_group_reservation BOOLEAN DEFAULT FALSE,
    menus_required INTEGER,
    token VARCHAR(255) UNIQUE,
    CONSTRAINT fk_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);