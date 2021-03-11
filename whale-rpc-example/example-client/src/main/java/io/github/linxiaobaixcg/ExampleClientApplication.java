package io.github.linxiaobaixcg;

import io.github.linxiaobaixcg.controller.HelloController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
@Slf4j
public class ExampleClientApplication {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ExampleClientApplication.class);
		HelloController helloController = (HelloController) applicationContext.getBean("helloController");
		helloController.test();
	}

}
