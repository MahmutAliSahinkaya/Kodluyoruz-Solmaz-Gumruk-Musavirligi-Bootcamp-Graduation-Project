package com.ticketapp.service;


import com.ticketapp.dto.NotificationDto;
import com.ticketapp.model.Mail;
import com.ticketapp.repository.MailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Slf4j
@RequiredArgsConstructor
@Service
public class MailService implements NotificationService {
    private final MailRepository mailRepository;
    @Override
    public void send(NotificationDto notificationDto) {
        log.info("mail service");

        mailRepository.save(new Mail(notificationDto.getTitle(), notificationDto.getTo(), notificationDto.getFrom(),notificationDto.getText(), LocalDateTime.parse(notificationDto.getSendingTime())));
    }
}
