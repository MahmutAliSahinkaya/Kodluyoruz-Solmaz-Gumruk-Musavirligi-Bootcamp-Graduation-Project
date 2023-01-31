package com.ticketapp.service;

import com.ticketapp.dto.PaymentDto;
import com.ticketapp.model.Payment;
import com.ticketapp.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    PaymentService paymentService;

    @Test
    void create_payment() {

        PaymentDto request= new PaymentDto();
        Payment payment = modelMapper.map(request,Payment.class);
        when(modelMapper.map(request, Payment.class)).thenReturn(payment);
        when(modelMapper.map(payment, PaymentDto.class)).thenReturn(request);
        when(paymentRepository.save(payment)).thenReturn(payment);
        var response= paymentService.createOrSavePayment(request);
        verify(paymentRepository,times(1)).save(payment);
        assertEquals(request,response.getBody());


    }

    @Test
    void get_payment() {
        PaymentDto request= new PaymentDto();
        Long id = 1L;
        var email = "test@gmail.com";
        Payment payment = modelMapper.map(request,Payment.class);
        when(modelMapper.map(request, Payment.class)).thenReturn(payment);
        when(modelMapper.map(payment, PaymentDto.class)).thenReturn(request);
        when(paymentRepository.findPaymentByUserEmailAndTravelIdAndSeatNumber(email,id,5)).thenReturn(payment);
        var response= paymentService.getPaymentOfTicket(email,id,5);
        verify(paymentRepository,times(1)).findPaymentByUserEmailAndTravelIdAndSeatNumber(email,id,5);
        assertEquals(request,response.getBody());
    }

    @Test
    void getPaymentOfTravelByEmailAndTravelId() {
        PaymentDto request= new PaymentDto();
        Long id = 1L;
        var email = "test@gmail.com";
        Payment payment = modelMapper.map(request,Payment.class);
        var list = new ArrayList<Payment>();
        list.add(payment);
        var listDto = new ArrayList<PaymentDto>();
        listDto.add(request);
        when(modelMapper.map(request, Payment.class)).thenReturn(payment);
        when(modelMapper.map(payment, PaymentDto.class)).thenReturn(request);
        when(paymentRepository.findPaymentsByTravelId(id)).thenReturn(list);
        var response=paymentService.getPaymentOfTravelByEmailAndTravelId(id);

        assertEquals(listDto,response.getBody());

    }
}