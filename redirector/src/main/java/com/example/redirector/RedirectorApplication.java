package com.example.redirector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RedirectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedirectorApplication.class, args);
	}

}
