package com.ticketapp.model;

import com.ticketapp.model.enums.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "travel")
public class Travel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Vehicle vehicle;
    private BigDecimal ticketPrice;
    private String fromStation;
    private String toStation;
    @Column
    private Integer seatCapacity;
    @ElementCollection
    private Set<Integer> purchasedSeats = new HashSet<>();
    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    @OneToMany
    private List<Ticket> tickets = new ArrayList<>();

    private Boolean isCanceled = false;
}
