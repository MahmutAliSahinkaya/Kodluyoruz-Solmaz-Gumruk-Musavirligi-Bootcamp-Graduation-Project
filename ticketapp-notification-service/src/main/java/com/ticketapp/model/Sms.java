package com.ticketapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Sms {
    private String id;
    private String to;
    private String from;
    private String text;
    private LocalDateTime sendingTime;

    public Sms(String to, String from, String text, LocalDateTime sendingTime) {
        this.to = to;
        this.from = from;
        this.text = text;
        this.sendingTime = sendingTime;
    }
}
