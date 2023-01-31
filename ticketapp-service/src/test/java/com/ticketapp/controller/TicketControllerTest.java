package com.ticketapp.controller;

import com.ticketapp.dto.TicketDto;
import com.ticketapp.model.Travel;
import com.ticketapp.model.User;
import com.ticketapp.repository.TravelRepository;
import com.ticketapp.repository.UserRepository;
import com.ticketapp.service.TicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(SpringExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService ticketService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TravelRepository travelRepository;
    @InjectMocks
    private TicketController ticketController;

    @Test
    public void testBuyTickets() {
        List<TicketDto> ticketDtos = Arrays.asList(
                new TicketDto(),
                new TicketDto()
        );
        String userEmail = "test@example.com";
        Long travelId = 1L;
        List<TicketDto> expectedTickets = Arrays.asList(
                new TicketDto(),
                new TicketDto()
        );
        when(ticketService.buyTickets(ticketDtos, userEmail, travelId)).thenReturn(ResponseEntity.ok(expectedTickets));
        ResponseEntity<List<TicketDto>> response = ticketController.buyTickets(ticketDtos, userEmail, travelId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTickets, response.getBody());
        verify(ticketService, times(1)).buyTickets(ticketDtos, userEmail, travelId);
    }


    @Test
    public void testGetTicketsByUserEmail() {
        String userEmail = "test@example.com";
        List<TicketDto> expectedTickets = Arrays.asList(
                new TicketDto(),
                new TicketDto()
        );
        when(ticketService.getTicketsByUserEmail(userEmail)).thenReturn(ResponseEntity.ok(expectedTickets));
        ResponseEntity<List<TicketDto>> response = ticketController.getTicketsByUserEmail(userEmail);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTickets, response.getBody());
        verify(ticketService, times(1)).getTicketsByUserEmail(userEmail);
    }


    @Test
    public void testSaveAndResponse() {
        String userEmail = "test@example.com";
        Long travelId = 1L;
        List<TicketDto> ticketDtos = Arrays.asList(
                new TicketDto(),
                new TicketDto()
        );
        User user = new User();
        Travel travel = new Travel();
        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(user));
        when(travelRepository.findById(travelId)).thenReturn(Optional.of(travel));
        List<TicketDto> expectedTickets = Arrays.asList(
                new TicketDto(),
                new TicketDto()
        );
        when(ticketService.saveAndResponse(ticketDtos, user, travel, travelId)).thenReturn(ResponseEntity.ok(expectedTickets));

        ResponseEntity<List<TicketDto>> response = ticketController.saveAndResponse(ticketDtos, userEmail, travelId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTickets, response.getBody());
        verify(userRepository, times(1)).findUserByEmail(userEmail);
        verify(travelRepository, times(1)).findById(travelId);
        verify(ticketService, times(1)).saveAndResponse(ticketDtos, user, travel, travelId);
    }

}