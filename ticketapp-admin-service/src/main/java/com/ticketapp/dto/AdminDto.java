package com.ticketapp.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@Data
public class AdminDto implements Serializable {
    private Long id;
    private String email;
    private String password;
}
