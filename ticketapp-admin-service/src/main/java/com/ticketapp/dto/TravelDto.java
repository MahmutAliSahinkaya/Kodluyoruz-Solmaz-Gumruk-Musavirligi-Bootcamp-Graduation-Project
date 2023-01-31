package com.ticketapp.dto;

import com.ticketapp.model.enums.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class TravelDto implements Serializable {
    private Long id;
    private Vehicle vehicle;
    private BigDecimal ticketPrice;
    private String fromStation;
    private String toStation;
    private Integer seatCapacity;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Set<Integer> purchasedSeats;
    private Boolean isCanceled;
}
