package tqs.hm1114588.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "weather_data", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"location_id", "forecast_date"})
})
public class WeatherData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    
    @Column(nullable = false)
    private Double temperature;
    
    @Column(nullable = false)
    private Double humidity;
    
    @Column(name = "wind_speed_km")
    private Double windSpeedKm;
    
    @Column(name = "wind_direction_id")
    private Integer windDirectionId;
    
    @Column(name = "precipitation")
    private Double precipitation;
    
    @Column(name = "pressure")
    private Double pressure;
    
    @Column(name = "radiation")
    private Double radiation;
    
    @Column(name = "station_id")
    private String stationId;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "forecast_date", nullable = false)
    private LocalDate forecastDate;
    
    // Default constructor for JPA
    protected WeatherData() {}
    
    // Constructor with all fields
    public WeatherData(Location location, Double temperature, Double humidity, 
                      Double windSpeedKm, Integer windDirectionId, Double precipitation,
                      Double pressure, Double radiation, String stationId, 
                      LocalDateTime timestamp, LocalDate forecastDate) {
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeedKm = windSpeedKm;
        this.windDirectionId = windDirectionId;
        this.precipitation = precipitation;
        this.pressure = pressure;
        this.radiation = radiation;
        this.stationId = stationId;
        this.timestamp = timestamp;
        this.forecastDate = forecastDate;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getWindSpeedKm() {
        return windSpeedKm;
    }

    public void setWindSpeedKm(Double windSpeedKm) {
        this.windSpeedKm = windSpeedKm;
    }

    public Integer getWindDirectionId() {
        return windDirectionId;
    }

    public void setWindDirectionId(Integer windDirectionId) {
        this.windDirectionId = windDirectionId;
    }

    public Double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Double precipitation) {
        this.precipitation = precipitation;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Double getRadiation() {
        return radiation;
    }

    public void setRadiation(Double radiation) {
        this.radiation = radiation;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDate getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(LocalDate forecastDate) {
        this.forecastDate = forecastDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherData that = (WeatherData) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(location, that.location) &&
               Objects.equals(forecastDate, that.forecastDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location, forecastDate);
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "id=" + id +
                ", location=" + location +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", windSpeedKm=" + windSpeedKm +
                ", windDirectionId=" + windDirectionId +
                ", precipitation=" + precipitation +
                ", pressure=" + pressure +
                ", radiation=" + radiation +
                ", stationId='" + stationId + '\'' +
                ", timestamp=" + timestamp +
                ", forecastDate=" + forecastDate +
                '}';
    }
} 