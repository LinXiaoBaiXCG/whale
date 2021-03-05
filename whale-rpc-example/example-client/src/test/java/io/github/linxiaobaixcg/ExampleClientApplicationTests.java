package io.github.linxiaobaixcg;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExampleClientApplicationTests {

	@Value("${whale.enable}")
	private String whale;

	@Test
	void contextLoads() {
		System.out.println(whale);
	}

}
