package com.ticketapp.model;

import com.ticketapp.model.enums.PaymentType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String userEmail;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    private Long travelId;
    private Integer seatNumber;
    private String iban = null;
    private String cardNumber = null;
    private String securityCode = null;
    private BigDecimal amount;
    private LocalDateTime paymentTime;
    private Boolean isCanceled = false;
}
