package com.ticketapp.service;


import com.ticketapp.dto.NotificationDto;
import com.ticketapp.model.Sms;
import com.ticketapp.repository.SmsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Slf4j
@RequiredArgsConstructor
@Service
public class SmsService implements NotificationService{
    private final SmsRepository smsRepository;
    @Override
    public void send(NotificationDto notificationDto) {
        log.info("sms service");

        smsRepository.save(new Sms( notificationDto.getTo(), notificationDto.getFrom(),notificationDto.getText(), LocalDateTime.parse(notificationDto.getSendingTime())));

    }
}
