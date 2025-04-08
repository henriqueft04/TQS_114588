-- Create restaurant table
CREATE TABLE restaurants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location_id BIGINT NOT NULL REFERENCES locations(id),
    capacity INTEGER NOT NULL,
    available_menus INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create schedules table
CREATE TABLE schedules (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id BIGINT NOT NULL REFERENCES restaurants(id),
    name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create dishes table
CREATE TABLE dishes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create menus table
CREATE TABLE menus (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id BIGINT NOT NULL REFERENCES restaurants(id),
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create menu_dishes junction table
CREATE TABLE menu_dishes (
    menu_id BIGINT NOT NULL REFERENCES menus(id),
    dish_id BIGINT NOT NULL REFERENCES dishes(id),
    PRIMARY KEY (menu_id, dish_id)
);

-- Create meals table
CREATE TABLE meals (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id BIGINT NOT NULL REFERENCES restaurants(id),
    schedule_id BIGINT REFERENCES schedules(id),
    meal_type VARCHAR(20) NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (restaurant_id, day_of_week, meal_type)
);

-- Create reservations table
CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id BIGINT NOT NULL REFERENCES restaurants(id),
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(50) NOT NULL,
    party_size INTEGER NOT NULL,
    reservation_time TIMESTAMP NOT NULL,
    meal_type VARCHAR(50) NOT NULL,
    special_requests TEXT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_group_reservation BOOLEAN NOT NULL DEFAULT FALSE,
    menus_required INTEGER NOT NULL,
    token VARCHAR(255) UNIQUE
);

-- Create indexes for faster lookups
CREATE INDEX idx_restaurants_location ON restaurants(location_id);
CREATE INDEX idx_schedules_restaurant ON schedules(restaurant_id);
CREATE INDEX idx_menus_restaurant ON menus(restaurant_id);
CREATE INDEX idx_meals_restaurant ON meals(restaurant_id);
CREATE INDEX idx_meals_schedule ON meals(schedule_id);
CREATE INDEX idx_meals_restaurant_day ON meals(restaurant_id, day_of_week);
CREATE INDEX idx_reservations_restaurant_time ON reservations(restaurant_id, reservation_time);
CREATE INDEX idx_reservations_status ON reservations(status); 