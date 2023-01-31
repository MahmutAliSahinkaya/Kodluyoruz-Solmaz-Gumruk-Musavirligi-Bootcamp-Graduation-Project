package com.ticketapp.service;

import com.ticketapp.dto.NotificationDto;
import com.ticketapp.model.Mail;
import com.ticketapp.model.enums.NotificationType;
import com.ticketapp.repository.MailRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class MailServiceTest {
    @Mock
    private MailRepository mailRepository;
    @InjectMocks
    private MailService mailService;

    @Test
    void send() {
        var mail = new Mail();
        var a= LocalDateTime.now();
        mail.setSendingTime(a);
        when(mailRepository.save(mail)).thenReturn(mail);
        mailService.send(new NotificationDto(NotificationType.EMAIL,null,null,null,null, a.toString()));
        verify(mailRepository,times(1)).save(mail);

    }
}