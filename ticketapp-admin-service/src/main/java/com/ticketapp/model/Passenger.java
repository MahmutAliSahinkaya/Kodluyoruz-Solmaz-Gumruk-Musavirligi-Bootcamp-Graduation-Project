package com.ticketapp.model;

import javax.persistence.*;

import com.ticketapp.model.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Passenger {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    private String fullName;
    private String tcNo;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @OneToMany(mappedBy = "passenger")
    private List<Ticket> ticket;
}
