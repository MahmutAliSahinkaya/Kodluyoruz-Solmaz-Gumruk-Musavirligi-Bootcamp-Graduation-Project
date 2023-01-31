package com.ticketapp.repository;

import com.ticketapp.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findPaymentByUserEmailAndTravelIdAndSeatNumber(String userEmail, Long travelId, Integer seatNumber);
    List<Payment> findPaymentsByTravelId(Long travelId);
}
