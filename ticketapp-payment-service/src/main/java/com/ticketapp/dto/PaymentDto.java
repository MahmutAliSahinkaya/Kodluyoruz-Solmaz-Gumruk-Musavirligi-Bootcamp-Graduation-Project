package com.ticketapp.dto;

import com.ticketapp.model.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto implements Serializable {
    private Long id;
    private String userEmail;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    private String iban;
    private String cardNumber;
    private Long travelId;
    private Integer seatNumber;
    private String securityCode;
    private BigDecimal amount;
    private LocalDateTime paymentTime;
    private Boolean isCanceled;
}
