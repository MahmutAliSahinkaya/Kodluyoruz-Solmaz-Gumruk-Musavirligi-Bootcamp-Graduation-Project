package com.ticketapp.service;

import com.ticketapp.dto.NotificationDto;

public interface NotificationService {
    void send(NotificationDto dto);
}
