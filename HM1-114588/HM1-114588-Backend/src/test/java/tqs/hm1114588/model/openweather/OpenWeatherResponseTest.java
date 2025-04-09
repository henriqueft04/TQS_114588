package tqs.hm1114588.model.openweather;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class OpenWeatherResponseTest {

    @Test
    void testOpenWeatherResponse() {
        // Create OpenWeatherResponse
        OpenWeatherResponse response = new OpenWeatherResponse();
        response.setLat(40.6443);
        response.setLon(-8.6455);
        response.setTimezone("Europe/Lisbon");
        response.setTimezoneOffset(3600);
        
        Current current = new Current();
        current.setDt(1684929490);
        current.setSunrise(1684926645);
        current.setSunset(1684977332);
        current.setTemp(292.55);
        current.setFeelsLike(292.87);
        current.setPressure(1014);
        current.setHumidity(89);
        current.setDewPoint(290.69);
        current.setUvi(0.16);
        current.setClouds(53);
        current.setVisibility(10000);
        current.setWindSpeed(3.13);
        current.setWindDeg(93);
        current.setWindGust(6.71);
        
        Weather weather = new Weather();
        weather.setId(803);
        weather.setMain("Clouds");
        weather.setDescription("broken clouds");
        weather.setIcon("04d");
        
        List<Weather> weatherList = new ArrayList<>();
        weatherList.add(weather);
        current.setWeather(weatherList);
        
        response.setCurrent(current);
        
        // Hourly data
        List<Hourly> hourlyList = new ArrayList<>();
        Hourly hourly = new Hourly();
        hourly.setDt(1684926000);
        hourly.setTemp(292.01);
        hourly.setFeelsLike(292.33);
        hourly.setPressure(1014);
        hourly.setHumidity(91);
        hourly.setDewPoint(290.51);
        hourly.setUvi(0);
        hourly.setClouds(54);
        hourly.setVisibility(10000);
        hourly.setWindSpeed(2.58);
        hourly.setWindDeg(86);
        hourly.setWindGust(5.88);
        hourly.setPop(0.15);
        
        Rain rain = new Rain();
        rain.setOneHour(0.5);
        hourly.setRain(rain);
        hourly.setWeather(weatherList);
        hourlyList.add(hourly);
        
        response.setHourly(hourlyList);
        
        // Daily data
        List<Daily> dailyList = new ArrayList<>();
        Daily daily = new Daily();
        daily.setDt(1684951200);
        daily.setSunrise(1684926645);
        daily.setSunset(1684977332);
        daily.setMoonrise(1684941060);
        daily.setMoonset(1684905480);
        daily.setMoonPhase(0.16);
        daily.setSummary("Expect a day of partly cloudy with rain");
        
        Temp temp = new Temp();
        temp.setDay(299.03);
        temp.setMin(290.69);
        temp.setMax(300.35);
        temp.setNight(291.45);
        temp.setEve(297.51);
        temp.setMorn(292.55);
        daily.setTemp(temp);
        
        FeelsLike feelsLike = new FeelsLike();
        feelsLike.setDay(299.21);
        feelsLike.setNight(291.37);
        feelsLike.setEve(297.86);
        feelsLike.setMorn(292.87);
        daily.setFeelsLike(feelsLike);
        
        daily.setPressure(1016);
        daily.setHumidity(59);
        daily.setDewPoint(290.48);
        daily.setWindSpeed(3.98);
        daily.setWindDeg(76);
        daily.setWindGust(8.92);
        daily.setClouds(92);
        daily.setPop(0.47);
        daily.setRain(0.15);
        daily.setUvi(9.23);
        daily.setWeather(weatherList);
        
        dailyList.add(daily);
        response.setDaily(dailyList);
        
        // Assertions
        assertEquals(40.6443, response.getLat());
        assertEquals(-8.6455, response.getLon());
        assertEquals("Europe/Lisbon", response.getTimezone());
        assertEquals(3600, response.getTimezoneOffset());
        
        // Check current weather
        assertNotNull(response.getCurrent());
        assertEquals(292.55, response.getCurrent().getTemp());
        assertEquals(89, response.getCurrent().getHumidity());
        assertEquals(3.13, response.getCurrent().getWindSpeed());
        assertEquals(93, response.getCurrent().getWindDeg());
        
        // Check weather condition
        assertNotNull(response.getCurrent().getWeather());
        assertEquals(1, response.getCurrent().getWeather().size());
        assertEquals(803, response.getCurrent().getWeather().get(0).getId());
        assertEquals("Clouds", response.getCurrent().getWeather().get(0).getMain());
        assertEquals("broken clouds", response.getCurrent().getWeather().get(0).getDescription());
        
        // Check hourly forecast
        assertNotNull(response.getHourly());
        assertEquals(1, response.getHourly().size());
        assertEquals(292.01, response.getHourly().get(0).getTemp());
        assertEquals(91, response.getHourly().get(0).getHumidity());
        assertEquals(0.15, response.getHourly().get(0).getPop());
        
        // Check rain data
        assertNotNull(response.getHourly().get(0).getRain());
        assertEquals(0.5, response.getHourly().get(0).getRain().getOneHour());
        
        // Check daily forecast
        assertNotNull(response.getDaily());
        assertEquals(1, response.getDaily().size());
        assertEquals("Expect a day of partly cloudy with rain", response.getDaily().get(0).getSummary());
        assertEquals(0.16, response.getDaily().get(0).getMoonPhase());
        
        // Check daily temperature
        assertNotNull(response.getDaily().get(0).getTemp());
        assertEquals(299.03, response.getDaily().get(0).getTemp().getDay());
        assertEquals(290.69, response.getDaily().get(0).getTemp().getMin());
        assertEquals(300.35, response.getDaily().get(0).getTemp().getMax());
        
        // Check feels like temperature
        assertNotNull(response.getDaily().get(0).getFeelsLike());
        assertEquals(299.21, response.getDaily().get(0).getFeelsLike().getDay());
        assertEquals(291.37, response.getDaily().get(0).getFeelsLike().getNight());
    }
} 