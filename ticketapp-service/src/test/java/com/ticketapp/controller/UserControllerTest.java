package com.ticketapp.controller;

import com.ticketapp.dto.TicketDto;
import com.ticketapp.dto.UserDto;
import com.ticketapp.repository.TicketRepository;
import com.ticketapp.repository.UserRepository;
import com.ticketapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserControllerTest {
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @Test
    public void testRegisterUser_Success() {
        UserDto request = new UserDto();
        request.setEmail("test@test.com");
        request.setPassword("password");

        ResponseEntity<String> expectedResponse = ResponseEntity.ok("User registered successfully");
        when(userService.registerUser(request)).thenReturn(expectedResponse);

        ResponseEntity<String> actualResponse = userController.registerUser(request);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testLoginUser_Success() {
        String email = "test@email.com";
        String password = "password";
        String expectedResponse = "Login Successful";
        ResponseEntity<String> expectedResult = ResponseEntity.ok().body(expectedResponse);

        when(userService.loginUser(email, password)).thenReturn(expectedResult);

        ResponseEntity<String> result = userController.loginUser(email, password);

        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetTicketsByUserId() {
        Long userId = 1L;
        List<TicketDto> expectedTickets = Arrays.asList(
                new TicketDto(),
                new TicketDto()
        );
        when(userService.getTicketsByUserId(userId)).thenReturn(ResponseEntity.ok(expectedTickets));
        ResponseEntity<List<TicketDto>> response = userController.getTicketsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTickets, response.getBody());
        verify(userService, times(1)).getTicketsByUserId(userId);
    }

    @Test
    public void testGetByUserEmail() {
        String email = "test@example.com";
        UserDto expectedUser = new UserDto();
        when(userService.getByUserEmail(email)).thenReturn(ResponseEntity.ok(expectedUser));

        ResponseEntity<UserDto> response = userController.getByUserEmail(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser, response.getBody());
        verify(userService, times(1)).getByUserEmail(email);
    }

    @Test
    public void testDeleteByUserEmail() {
        String email = "test@example.com";
        when(userService.deleteByUserEmail(email)).thenReturn(ResponseEntity.ok("User deleted successfully"));
        ResponseEntity<String> response = userController.deleteByUserEmail(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody());
        verify(userService, times(1)).deleteByUserEmail(email);
    }

}