-- Create tables for the weather API

-- Create table for weather locations
CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    country VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create table for weather data
CREATE TABLE weather_data (
    id BIGSERIAL PRIMARY KEY,
    location_id BIGINT NOT NULL REFERENCES locations(id),
    temperature DOUBLE PRECISION NOT NULL,
    humidity DOUBLE PRECISION NOT NULL,
    wind_speed DOUBLE PRECISION NOT NULL,
    wind_speed_km DOUBLE PRECISION,
    wind_direction VARCHAR(50),
    wind_direction_id INTEGER,
    precipitation DOUBLE PRECISION,
    pressure DOUBLE PRECISION,
    radiation DOUBLE PRECISION,
    station_id VARCHAR(50),
    timestamp TIMESTAMP NOT NULL,
    forecast_date DATE NOT NULL,
    UNIQUE (location_id, forecast_date)
);

-- Create index for faster lookups
CREATE INDEX idx_locations_name ON locations(name);
CREATE INDEX idx_weather_data_location_date ON weather_data(location_id, forecast_date); 