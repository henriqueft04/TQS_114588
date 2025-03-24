package tqs.lab6_2_DatabaseMigration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class Lab62DatabaseMigrationApplicationTests {

	@Test
	void contextLoads() {
	}

}
