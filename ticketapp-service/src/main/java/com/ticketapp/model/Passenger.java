package com.ticketapp.model;

import com.ticketapp.model.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
public class Passenger {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String mobilePhoneNumber;
    @OneToMany
    private List<Ticket> ticket;
}
