package com.ticketapp;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "TicketAppProject API", version = "1.0", description = "Main API Information"))
public class TicketappPaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketappPaymentServiceApplication.class, args);
	}

}
