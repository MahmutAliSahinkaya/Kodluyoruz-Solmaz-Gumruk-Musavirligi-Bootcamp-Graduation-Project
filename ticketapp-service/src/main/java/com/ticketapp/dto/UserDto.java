package com.ticketapp.dto;

import com.ticketapp.model.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto implements Serializable {
    private String email;
    private String password;
    private String mobilePhoneNumber;
    private List<TicketDto> tickets;
    private UserType userType;
}
