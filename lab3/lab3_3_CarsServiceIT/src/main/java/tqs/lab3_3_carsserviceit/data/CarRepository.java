package tqs.lab3_3_carsserviceit.data;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.lab3_3_carsserviceit.model.Car;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    public Car findByCarId(Long carId);

    public List<Car> findAll();
}
