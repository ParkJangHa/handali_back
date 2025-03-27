package com.handalsali.handali;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling  // 스케줄링 기능 활성화
public class HandaliApplication {
	public static void main(String[] args) {
		SpringApplication.run(HandaliApplication.class, args);
	}

}
