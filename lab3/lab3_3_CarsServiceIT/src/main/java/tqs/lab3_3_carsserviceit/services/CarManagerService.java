package tqs.lab3_3_carsserviceit.services;

import org.springframework.stereotype.Service;
import tqs.lab3_3_carsserviceit.data.CarRepository;
import tqs.lab3_3_carsserviceit.model.Car;

import java.util.Optional;

@Service
public class CarManagerService {

    final CarRepository carRepository;

    public CarManagerService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public void save(Car car) {
        carRepository.save(car);
    }

    public Car findById(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        carRepository.deleteById(id);
    }

    public Iterable<Car> findAll() {
        return carRepository.findAll();
    }

    public void update(Car car) {
        carRepository.save(car);
    }

    public Optional<Car> getCarDetails(Long carId) {
        return Optional.of(carRepository.findById(carId).orElse(null));
    }

}
