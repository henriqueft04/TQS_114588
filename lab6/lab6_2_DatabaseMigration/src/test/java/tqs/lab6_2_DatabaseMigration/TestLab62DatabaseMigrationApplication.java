package tqs.lab6_2_DatabaseMigration;

import org.springframework.boot.SpringApplication;

public class TestLab62DatabaseMigrationApplication {

	public static void main(String[] args) {
		SpringApplication.from(Lab62DatabaseMigration::main).with(TestcontainersConfiguration.class).run(args);
	}

}
