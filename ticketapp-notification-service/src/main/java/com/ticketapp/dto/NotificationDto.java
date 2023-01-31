package com.ticketapp.dto;

import com.ticketapp.model.enums.NotificationType;
import lombok.*;

import java.io.Serializable;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationDto implements Serializable {
    private NotificationType type;
    private String title;
    private String to;
    private String from;
    private String text;
    private String sendingTime;
}
