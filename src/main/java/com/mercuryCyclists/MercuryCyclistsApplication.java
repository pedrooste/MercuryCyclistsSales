package com.mercuryCyclists;

import com.mercuryCyclists.Sales.entity.Store;
import com.mercuryCyclists.Sales.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application
 */
@SpringBootApplication
public class MercuryCyclistsApplication implements CommandLineRunner {
	@Autowired
	StoreService storeService;

	public static void main(String[] args) {
		SpringApplication.run(MercuryCyclistsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Store store = new Store();
		store.setManager("Zhifa");
		store.setAddress("Crown Street Wollongong");
		storeService.registerStore(store);
	}
}
