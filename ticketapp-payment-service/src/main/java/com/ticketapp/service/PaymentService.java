package com.ticketapp.service;

import com.ticketapp.dto.PaymentDto;
import com.ticketapp.model.Payment;
import com.ticketapp.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    public ResponseEntity<PaymentDto> createOrSavePayment(PaymentDto request) {
        log.info("payment service,createOrSavePayment ");

        return ResponseEntity.ok().body(modelMapper.map(paymentRepository.save(modelMapper.map(request, Payment.class)),PaymentDto.class));
    }


    public ResponseEntity<PaymentDto> getPaymentOfTicket(String email, Long travelId, Integer seatNumber) {
        log.info("payment service,getPaymentOfTicket ");

        return ResponseEntity.ok().body(modelMapper.map(paymentRepository.findPaymentByUserEmailAndTravelIdAndSeatNumber(email, travelId, seatNumber), PaymentDto.class));

    }

    public ResponseEntity<List<PaymentDto>> getPaymentOfTravelByEmailAndTravelId(Long travelId) {
        log.info("payment service,getPaymentOfTravelByEmailAndTravelId ");

        return ResponseEntity.ok().body(paymentRepository.findPaymentsByTravelId(travelId)
                .stream().map(payment -> modelMapper.map(payment, PaymentDto.class)).toList());
    }

}
