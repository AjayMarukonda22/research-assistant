package com.example.smart_research;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync  // Add this line
public class SmartResearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartResearchApplication.class, args);
	}

}
