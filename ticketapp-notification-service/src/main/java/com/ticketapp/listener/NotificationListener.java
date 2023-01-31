package com.ticketapp.listener;

import com.ticketapp.dto.NotificationDto;
import com.ticketapp.model.enums.NotificationType;
import com.ticketapp.service.MailService;
import com.ticketapp.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationListener {
    private final SmsService smsService;

    private final MailService mailService;

    @RabbitListener(queues = "ticketService")
    public void handleNotification(NotificationDto dto) {
        log.info("notification listener");
        if (dto.getType() == NotificationType.EMAIL) mailService.send(dto);
        else if (dto.getType() == NotificationType.SMS) smsService.send(dto);
        else throw new IllegalArgumentException("Böyle bir mesaj tipi yok");

    }
}
