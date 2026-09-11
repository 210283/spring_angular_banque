package com.votrebanque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VotrebanqueApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotrebanqueApplication.class, args);
	}

}
