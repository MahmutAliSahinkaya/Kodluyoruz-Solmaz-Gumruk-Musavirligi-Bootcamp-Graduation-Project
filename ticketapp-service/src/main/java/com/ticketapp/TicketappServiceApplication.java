package com.ticketapp;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "TicketApp API", version = "1.0", description = "Main API Information"))
public class TicketappServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketappServiceApplication.class, args);
	}

}
