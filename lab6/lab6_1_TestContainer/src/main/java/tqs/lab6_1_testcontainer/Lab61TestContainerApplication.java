package tqs.lab6_1_testcontainer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "tqs.lab6_1_testcontainer")
@EnableJpaRepositories(basePackages = "tqs.lab6_1_testcontainer")
public class Lab61TestContainerApplication {
	public static void main(String[] args) {
		SpringApplication.run(Lab61TestContainerApplication.class, args);
	}
}
