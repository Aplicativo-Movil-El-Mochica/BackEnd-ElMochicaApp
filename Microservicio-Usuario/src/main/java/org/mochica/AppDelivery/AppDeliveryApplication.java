package org.mochica.AppDelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppDeliveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppDeliveryApplication.class, args);
		System.out.println("Microservicio En Ejecucion!");
	}

}
