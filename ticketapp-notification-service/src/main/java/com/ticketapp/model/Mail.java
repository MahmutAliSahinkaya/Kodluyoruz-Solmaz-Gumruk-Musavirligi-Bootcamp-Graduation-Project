package com.ticketapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Mail {
    @Id
    private String id;
    private String title;
    private String to;
    private String from;
    private String text;
    private LocalDateTime sendingTime;

    public Mail(String title, String to, String from, String text, LocalDateTime sendingTime) {
        this.title = title;
        this.to = to;
        this.from = from;
        this.text = text;
        this.sendingTime = sendingTime;
    }
}
