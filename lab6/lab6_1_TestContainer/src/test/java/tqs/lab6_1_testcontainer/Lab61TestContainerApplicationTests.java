package tqs.lab6_1_testcontainer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class Lab61TestContainerApplicationTests {

	@Test
	void contextLoads() {
	}

}
