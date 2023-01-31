package com.ticketapp.service;

import com.ticketapp.dto.NotificationDto;
import com.ticketapp.model.Sms;
import com.ticketapp.model.enums.NotificationType;
import com.ticketapp.repository.SmsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
@ExtendWith(SpringExtension.class)
class SmsServiceTest {

    @Mock
    private SmsRepository smsRepository;
    @InjectMocks
    private SmsService smsService;
    @Test
    void send() {

        var sms = new Sms();
        var a= LocalDateTime.now();
        sms.setSendingTime(a);
        when(smsRepository.save(sms)).thenReturn(sms);
        smsService.send(new NotificationDto(NotificationType.SMS,null,null,null,null, a.toString()));
        verify(smsRepository,times(1)).save(sms);
    }
}