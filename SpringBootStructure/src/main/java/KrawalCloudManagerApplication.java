package com.siemens.krawal.krawalcloudmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.siemens.authorization.Authorizer;

@SpringBootApplication
@EnableScheduling
public class KrawalCloudManagerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(KrawalCloudManagerApplication.class, args);
	}

	@Bean
	public Authorizer returnAUthorizer() {
		return new Authorizer();
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*");
			}
		};
	}

}
