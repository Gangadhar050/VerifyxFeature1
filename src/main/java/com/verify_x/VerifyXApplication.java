package com.verify_x;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VerifyXApplication {

	public static void main(String[] args) {
		SpringApplication.run(VerifyXApplication.class, args);
	}

}
