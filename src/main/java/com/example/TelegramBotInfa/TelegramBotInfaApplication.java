package com.example.TelegramBotInfa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
//@ComponentScan(basePackages ={"com.example.TelegramBotInfa.servec"})
public class TelegramBotInfaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramBotInfaApplication.class, args);
		
	}
}
