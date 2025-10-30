package com.michelin.restaurants;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestaurantsApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantsApplication.class, args);

        System.out.println("=============================================================\n\n\tMichelin's new restaurants API just started !\n\n=============================================================");
	}

}
