package tqs.lab6_2_DatabaseMigration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "tqs.lab6_2_DatabaseMigration")
@EnableJpaRepositories(basePackages = "tqs.lab6_2_DatabaseMigration")
public class Lab62DatabaseMigration {
	public static void main(String[] args) {
		SpringApplication.run(Lab62DatabaseMigration.class, args);
	}
}
