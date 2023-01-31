package com.ticketapp.dto;

import com.ticketapp.model.enums.Gender;
import lombok.Data;

@Data
public class PassengerDto {
    private Long id;
    private String name;
    private String surname;
    private Gender gender;
}
