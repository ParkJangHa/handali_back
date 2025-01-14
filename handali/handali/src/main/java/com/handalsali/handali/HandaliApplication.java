package com.handalsali.handali;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class HandaliApplication {

	public static void main(String[] args) {
		SpringApplication.run(HandaliApplication.class, args);
	}

}
