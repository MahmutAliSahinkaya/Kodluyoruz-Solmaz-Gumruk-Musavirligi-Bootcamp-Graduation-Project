package com.ticketapp.client;

import com.ticketapp.dto.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "payment-service", url = "http://localhost:8082/payment")
public interface PaymentClient {
    @PostMapping
    ResponseEntity<PaymentDto> createOrUpdatePayment(@RequestBody PaymentDto request);

    @GetMapping("/{travelId}")
    ResponseEntity<List<PaymentDto>> getPaymentsOfTravel(@PathVariable Long travelId);

    @GetMapping("{email}/{travelId}/{seatNumber}")
    ResponseEntity<PaymentDto> getPaymentOfTicket(@PathVariable String email, @PathVariable Long travelId, @PathVariable Integer seatNumber);

}
