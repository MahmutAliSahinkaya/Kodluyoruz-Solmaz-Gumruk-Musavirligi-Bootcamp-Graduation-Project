package com.ticketapp.controller;

import com.ticketapp.dto.PaymentDto;
import com.ticketapp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    ResponseEntity<PaymentDto> createOrUpdatePayment(@RequestBody PaymentDto request) {
        log.info("payment conroller, createOrUpdatePayment");
        return paymentService.createOrSavePayment(request);
    }


    @GetMapping("{email}/{travelId}/{seatNumber}")
    ResponseEntity<PaymentDto> getPaymentOfTicket(@PathVariable String email, @PathVariable Long travelId, @PathVariable Integer seatNumber) {
        log.info("payment conroller, getPaymentOfTicket");
        return paymentService.getPaymentOfTicket(email, travelId, seatNumber);
    }

    @GetMapping("{travelId}")
    ResponseEntity<List<PaymentDto>> getPaymentsOfTravel(@PathVariable Long travelId) {
        log.info("payment conroller, getPaymentsOfTravel");
        return paymentService.getPaymentOfTravelByEmailAndTravelId(travelId);
    }

}
