package tqs.lab3_3_carsserviceit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tqs.lab3_3_carsserviceit.data.CarRepository;
import tqs.lab3_3_carsserviceit.model.Car;

import java.util.Objects;

@DataJpaTest
public class CarRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CarRepository carRepository;

    @Test
    void getSuitableReplacement() {
        Car car = new Car("Ford", "Mustang");
        entityManager.persistAndFlush(car);
        Car found = carRepository.findById(car.getCarId()).orElse(null);
        assert(Objects.equals(found, car));
    }

}
