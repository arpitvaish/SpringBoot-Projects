package com.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
//load the spring xml file from classpath
@ImportResource("classpath:my-camel.xml")
public class Application {


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
