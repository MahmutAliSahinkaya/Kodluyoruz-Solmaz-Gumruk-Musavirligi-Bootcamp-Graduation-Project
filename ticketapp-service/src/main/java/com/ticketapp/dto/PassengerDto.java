package com.ticketapp.dto;

import com.ticketapp.model.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
@Data
@Builder
public class PassengerDto implements Serializable {
    private Long id;
    private String name;
    private String surname;
    private String mobilePhoneNumber;
    private Gender gender;

}
