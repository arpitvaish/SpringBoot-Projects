package com.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		// final MVStore store = MVStore.open("C:\\temp\\h2db.mv.db");

		// final MVMap<Object, Object> openMap = store.openMap("undoLog");

		// openMap.clear();

		// close the store (this will persist changes)
		// store.close();
	}

	/*
	 * @Bean public CommandLineRunner commandLineRunner(ApplicationContext ctx)
	 * {
	 * 
	 * return args -> {
	 * System.out.println("Let's inspect the bean provided by Spring Boot");
	 * String[] beanName = ctx.getBeanDefinitionNames(); Arrays.sort(beanName);
	 * for (String bean : beanName) { System.out.println(bean); } }; }
	 */

}
