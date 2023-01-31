package com.ticketapp.service;

import com.google.common.hash.Hashing;
import com.ticketapp.dto.NotificationDto;
import com.ticketapp.dto.UserDto;
import com.ticketapp.exception.MailAlreadyInUseException;
import com.ticketapp.exception.UserNotFoundException;
import com.ticketapp.exception.WrongPasswordException;
import com.ticketapp.model.User;
import com.ticketapp.model.enums.UserType;
import com.ticketapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @InjectMocks
    private UserService userService;


    @Test
    void register_if_email_is_not_used() {
        UserDto request = new UserDto("test@user.com", "123456", "5555555555", new ArrayList<>(), UserType.CORPORATE);

        when(userRepository.save(any(User.class))).thenReturn(any(User.class));
        when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.empty());
        var result = userService.registerUser(request);


        verify(userRepository, times(1)).findUserByEmail(request.getEmail());
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(NotificationDto.class));
        verify(userRepository, times(1)).save(modelMapper.map(result, User.class));


        assertEquals(request.getEmail() + " mailli kullanıcı başarı ile kayıt edildi", result.getBody());
    }

    @Test
    void cant_register_because_email_is_used() {
        UserDto request = new UserDto("test@user.com", "123456", "5555555555", new ArrayList<>(), UserType.CORPORATE);

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(mock(User.class))).thenThrow(MailAlreadyInUseException.class);

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(NotificationDto.class));
        verify(userRepository, never()).save(any(User.class));

        assertThrows(MailAlreadyInUseException.class, () -> userService.registerUser(request));


    }

    @Test
    void cant_login_because_user_not_found() {
        var email = "test@gmail.com";
        var password = "123123";
        when(userRepository.findUserByEmail(email)).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class, () -> userService.loginUser(email, password));
    }

    @Test
    void cant_login_because_wrong_password() {
        var email = "test@gmail.com";
        var password = "123123";
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(mock(User.class)));
        assertThrows(WrongPasswordException.class, () -> userService.loginUser(email, password));
    }

    @Test
    void login_succeeded() {
        var email = "test@gmail.com";
        var password = "123123";

        User user = new User();
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        user.setPassword(Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
        assertEquals(userService.loginUser(email, password), ResponseEntity.ok().body("Oturum başarıyla açıldı."));
    }

    @Test
    void cant_get_user_with_email() {
        var email = "test@gmail.com";
        when(userRepository.findUserByEmail(anyString())).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class, () -> userService.getByUserEmail(email));

    }

    @Test
    void get_user_succeeded() {
        var email = "test@gmail.com";
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(mock(User.class)));
        var user = userRepository.findUserByEmail(email).orElseThrow(UserNotFoundException::new);
        verify(userRepository, times(1)).findUserByEmail(email);
        assertEquals(ResponseEntity.ok().body(modelMapper.map(user, UserDto.class)), userService.getByUserEmail(email));

    }

    @Test
    void cant_delete_user() {
        var email = "test@gmail.com";
        when(userRepository.findUserByEmail(anyString())).thenThrow(UserNotFoundException.class);
        verify(userRepository, times(0)).delete(any(User.class));
        assertThrows(UserNotFoundException.class, () -> userService.deleteByUserEmail(email));
    }

    @Test
    void delete_user() {
        var email = "test@gmail.com";
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(mock(User.class)));
        userService.deleteByUserEmail(email);
        verify(userRepository, times(1)).delete(any(User.class));
        assertEquals(ResponseEntity.ok().body(email + " kullanıcısı başarıyla silindi"), userService.deleteByUserEmail(email));
    }

    @Test
    void cant_get_user_tickets() {
        var id = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getTicketsByUserId(id));
    }

    @Test
    void user_tickets() {
        var id = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mock(User.class)));
        userService.getTicketsByUserId(id);
        verify(userRepository, times(1)).findById(id);
        assertEquals(ResponseEntity.ok().body(anyList()), userService.getTicketsByUserId(id));
    }
}