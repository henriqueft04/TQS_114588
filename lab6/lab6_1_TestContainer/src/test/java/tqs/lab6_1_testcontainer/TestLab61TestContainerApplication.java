package tqs.lab6_1_testcontainer;

import org.springframework.boot.SpringApplication;

public class TestLab61TestContainerApplication {

	public static void main(String[] args) {
		SpringApplication.from(Lab61TestContainerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
