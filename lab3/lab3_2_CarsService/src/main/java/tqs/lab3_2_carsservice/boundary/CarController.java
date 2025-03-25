package tqs.lab3_2_carsservice.boundary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.lab3_2_carsservice.model.Car;
import tqs.lab3_2_carsservice.services.CarManagerService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {

    private final CarManagerService carManagerService;

    public CarController(CarManagerService injCarManagerService) {
        this.carManagerService = injCarManagerService;
    }

    @PostMapping("/addCar")
    public ResponseEntity<Car> addCar(@RequestBody Car car) {
        HttpStatus status = HttpStatus.CREATED;
        carManagerService.save(car);
        return new ResponseEntity<>(car, status);
    }

    @GetMapping("/cars")
    public List<Car> getCars() {
        return (List<Car>) carManagerService.findAll();
    }

    @GetMapping("/cars/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable(value = "id") Long id) {
        Car car = carManagerService.findById(id);
        if (car == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(car, HttpStatus.OK);
    }

    @DeleteMapping("/cars/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable(value = "id") Long id) {
        carManagerService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
