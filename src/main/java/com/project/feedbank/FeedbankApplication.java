package com.project.feedbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class FeedbankApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedbankApplication.class, args);
	}

}
