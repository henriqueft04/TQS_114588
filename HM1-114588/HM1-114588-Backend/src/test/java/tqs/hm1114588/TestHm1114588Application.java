package tqs.hm1114588;

import org.springframework.boot.SpringApplication;

public class TestHm1114588Application {

    public static void main(String[] args) {
        SpringApplication.from(Hm1114588Application::main).with(TestcontainersConfiguration.class).run(args);
    }

}
