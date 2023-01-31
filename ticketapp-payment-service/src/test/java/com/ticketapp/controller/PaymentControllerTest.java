package com.ticketapp.controller;

import com.ticketapp.dto.PaymentDto;
import com.ticketapp.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PaymentControllerTest {
    @Mock
    private PaymentService paymentService;
    @InjectMocks
    private PaymentController paymentController;

    @Test
    public void testCreateOrUpdatePayment() {
        PaymentDto request = new PaymentDto();
        PaymentDto response = new PaymentDto();
        when(paymentService.createOrSavePayment(request)).thenReturn(ResponseEntity.ok(response));
        ResponseEntity<PaymentDto> result = paymentController.createOrUpdatePayment(request);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(paymentService, times(1)).createOrSavePayment(request);
    }
    @Test
    void testGetPaymentOfTicket() {
        String email = "test@example.com";
        Long travelId = 12345L;
        Integer seatNumber = 5;
        PaymentDto paymentDto = new PaymentDto();
        when(paymentService.getPaymentOfTicket(email, travelId, seatNumber)).thenReturn(ResponseEntity.ok(paymentDto));
        ResponseEntity<PaymentDto> response = paymentController.getPaymentOfTicket(email, travelId, seatNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paymentDto, response.getBody());
        verify(paymentService).getPaymentOfTicket(email, travelId, seatNumber);
    }
    @Test
    void testGetPaymentsOfTravel() {
        Long travelId = 12345L;
        List<PaymentDto> paymentDtos = new ArrayList<>();
        paymentDtos.add(new PaymentDto());
        when(paymentService.getPaymentOfTravelByEmailAndTravelId(travelId)).thenReturn(ResponseEntity.ok(paymentDtos));
        ResponseEntity<List<PaymentDto>> response = paymentController.getPaymentsOfTravel(travelId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paymentDtos, response.getBody());
        verify(paymentService).getPaymentOfTravelByEmailAndTravelId(travelId);
    }

}