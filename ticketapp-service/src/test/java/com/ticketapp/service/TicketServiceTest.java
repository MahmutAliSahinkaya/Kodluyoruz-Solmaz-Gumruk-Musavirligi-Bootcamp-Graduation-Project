package com.ticketapp.service;

import com.ticketapp.client.PaymentClient;
import com.ticketapp.dto.NotificationDto;
import com.ticketapp.dto.PassengerDto;
import com.ticketapp.dto.PaymentDto;
import com.ticketapp.dto.TicketDto;
import com.ticketapp.exception.TicketBuyCondidationsException;
import com.ticketapp.exception.TravelNotFoundException;
import com.ticketapp.exception.UserNotFoundException;
import com.ticketapp.model.Passenger;
import com.ticketapp.model.Ticket;
import com.ticketapp.model.Travel;
import com.ticketapp.model.User;
import com.ticketapp.model.enums.Gender;
import com.ticketapp.model.enums.UserType;
import com.ticketapp.repository.PassengerRepository;
import com.ticketapp.repository.TicketRepository;
import com.ticketapp.repository.TravelRepository;
import com.ticketapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class TicketServiceTest {

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TravelRepository travelRepository;
    @Mock
    private PaymentClient paymentClient;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private PassengerRepository passengerRepository;
    @InjectMocks
    private TicketService ticketService;

    @Test
    void get_tickets() {
        var email = "test@gmail.com";
        var list = new ArrayList<Ticket>();
        list.add(new Ticket());
        var user = new User();
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(ticketRepository.findTicketsByUser(user)).thenReturn(list);

        var response = ticketService.getTicketsByUserEmail(email);

        verify(userRepository, times(1)).findUserByEmail(email);
        verify(ticketRepository, times(1)).findTicketsByUser(user);

        assertEquals(list.stream().map(ticket -> modelMapper.map(ticket, TicketDto.class)).toList(), response.getBody());

    }

    @Test
    void cant_get_tickets_because_user_not_found() {
        var email = "test@gmail.com";
        var user = new User();
        var passenger = new Passenger();
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty()).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> ticketService.getTicketsByUserEmail(email));
        verify(userRepository, times(1)).findUserByEmail(email);
        verify(ticketRepository, never()).findTicketsByUser(user);
        verify(ticketRepository, never()).findTicketsByUser(user);
        verify(passengerRepository, never()).save(passenger);

    }

    @Test
    void cant_buy_tickets_because_user_not_found() {
        var email = "test@gmail.com";
        var user = new User();
        var id = 1L;
        PaymentDto paymentDto = new PaymentDto();

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty()).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> ticketService.getTicketsByUserEmail(email));
        verify(userRepository, times(1)).findUserByEmail(email);
        verify(travelRepository, never()).findById(id);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
        verify(paymentClient, never()).createOrUpdatePayment(paymentDto);

    }

    @Test
    void cant_buy_tickets_because_travel_not_found() {
        var email = "test@gmail.com";
        var user = new User();
        var id = 1L;
        PaymentDto paymentDto = new PaymentDto();
        var listTicket = new ArrayList<TicketDto>();
        listTicket.add(new TicketDto());
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(travelRepository.findById(id)).thenReturn(Optional.empty()).thenThrow(TravelNotFoundException.class);
        assertThrows(TravelNotFoundException.class, () -> ticketService.buyTickets(listTicket, email, id));
        verify(userRepository, times(1)).findUserByEmail(email);
        verify(travelRepository, times(1)).findById(id);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
        verify(paymentClient, never()).createOrUpdatePayment(paymentDto);
    }

    @Test
    void cant_buy_tickets_because_individual_buy_to_much_ticket() {
        var email = "test@gmail.com";
        var user = new User();
        var id = 1L;
        user.setUserType(UserType.INDIVIDUAL);
        user.setEmail(email);
        var travel = new Travel();
        var emptyList = new ArrayList<Ticket>();
        var passenger = new Passenger();
        passenger.setGender(Gender.FEMALE);
        var ticket = new Ticket(id, 40, travel, user, passenger);

        var listTicket = prepareTickets(5, emptyList, ticket);

        travel.setSeatCapacity(45);
        travel.setTickets(listTicket);


        var listTicketDto = new ArrayList<TicketDto>();
        listTicketDto.add(TicketDto.builder()
                .seatNumber(20)
                .passenger(PassengerDto.builder().gender(Gender.MALE).build()).build());

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(travelRepository.findById(id)).thenReturn(Optional.of(travel));
        travel.getTickets().addAll(listTicket);
        assertThrows(TicketBuyCondidationsException.class, () -> ticketService.buyTickets(listTicketDto, email, id));

        verify(userRepository, times(1)).findUserByEmail(email);
        verify(travelRepository, times(1)).findById(id);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
        verify(paymentClient, never()).createOrUpdatePayment(any(PaymentDto.class));

    }

    @Test
    void cant_buy_tickets_because_individual_buy_to_much_ticket_gender_male_for_one_buy() {
        var email = "test@gmail.com";
        var user = new User();
        var id = 1L;
        user.setUserType(UserType.INDIVIDUAL);
        user.setEmail(email);
        var travel = new Travel();
        var passenger = new Passenger();
        passenger.setGender(Gender.MALE);


        travel.setSeatCapacity(45);


        var listTicketDto = new ArrayList<TicketDto>();
        TicketDto dto1 = TicketDto.builder()
                .seatNumber(20)
                .payment(PaymentDto.builder().build())
                .passenger(PassengerDto.builder().gender(Gender.MALE).build()).build();
        TicketDto dto2 = TicketDto.builder()
                .payment(PaymentDto.builder().build())
                .seatNumber(21)
                .passenger(PassengerDto.builder().gender(Gender.MALE).build()).build();
        TicketDto dto3 = TicketDto.builder()
                .payment(PaymentDto.builder().build())
                .seatNumber(2)
                .passenger(PassengerDto.builder().gender(Gender.MALE).build()).build();
        listTicketDto.add(dto1);
        listTicketDto.add(dto2);
        listTicketDto.add(dto3);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(travelRepository.findById(id)).thenReturn(Optional.of(travel));
        assertThrows(TicketBuyCondidationsException.class, () -> ticketService.buyTickets(listTicketDto, email, id));

        verify(userRepository, times(1)).findUserByEmail(email);
        verify(travelRepository, times(1)).findById(id);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
        verify(paymentClient, never()).createOrUpdatePayment(any(PaymentDto.class));
    }

    @Test
    void cant_buy_tickets_because_corporate_buy_to_much_ticket() {
        var email = "test@gmail.com";
        var user = new User();
        var id = 1L;
        user.setUserType(UserType.CORPORATE);
        user.setEmail(email);
        var travel = new Travel();
        var emptyList = new ArrayList<Ticket>();
        var passenger = new Passenger();
        passenger.setGender(Gender.FEMALE);
        var ticket = new Ticket(id, 40, travel, user, passenger);

        var listTicket = prepareTickets(20, emptyList, ticket);

        travel.setSeatCapacity(45);
        travel.setTickets(listTicket);


        var listTicketDto = new ArrayList<TicketDto>();
        listTicketDto.add(TicketDto.builder()
                .seatNumber(20)
                .passenger(PassengerDto.builder().gender(Gender.MALE).build()).build());

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(travelRepository.findById(id)).thenReturn(Optional.of(travel));
        travel.getTickets().addAll(listTicket);
        assertThrows(TicketBuyCondidationsException.class, () -> ticketService.buyTickets(listTicketDto, email, id));

        verify(userRepository, times(1)).findUserByEmail(email);
        verify(travelRepository, times(1)).findById(id);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
        verify(paymentClient, never()).createOrUpdatePayment(any(PaymentDto.class));
    }

    @Test
    void cant_buy_tickets_because_user_type_wrong() {
        var email = "test@gmail.com";
        var user = new User();
        var id = 1L;
        user.setEmail(email);
        var travel = new Travel();
        var listTicketDto = new ArrayList<TicketDto>();
        listTicketDto.add(TicketDto.builder()
                .seatNumber(20)
                .passenger(PassengerDto.builder().gender(Gender.MALE).build()).build());
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user)).thenThrow(IllegalArgumentException.class);
        when(travelRepository.findById(id)).thenReturn(Optional.of(travel));

        assertThrows(IllegalArgumentException.class, () -> ticketService.buyTickets(listTicketDto, email, id));

    }

    @Test
    void buy_ticket_corporate() {
        var email = "test@gmail.com";
        var user = new User();
        var id = 1L;
        user.setUserType(UserType.CORPORATE);
        user.setEmail(email);
        var travel = new Travel();
        travel.setFromStation("ANTALYA");
        travel.setToStation("ANKARA");
        var passenger = new Passenger();
        passenger.setGender(Gender.FEMALE);


        travel.setSeatCapacity(45);

        PaymentDto paymentDto = PaymentDto.builder().build();
        var listTicketDto = new ArrayList<TicketDto>();
        listTicketDto.add(TicketDto.builder()
                .id(id)
                .seatNumber(20)
                .payment(paymentDto)
                .passenger(PassengerDto.builder().gender(Gender.MALE).build()).build());
        var ticketDto = listTicketDto.stream().findFirst().get();

        var ticket = new Ticket();
        ticket.setTravel(travel);
        ticket.setUser(user);
        ticket.setPassenger(passenger);
        ticket.setSeatNumber(20);
        ticket.setId(id);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(travelRepository.findById(id)).thenReturn(Optional.of(travel));
        when(ticketRepository.save(ticket)).thenReturn(ticket);


        when(paymentClient.createOrUpdatePayment(paymentDto)).thenReturn(ResponseEntity.ok().body(paymentDto));
        when(modelMapper.map(ticketDto, Ticket.class)).thenReturn(ticket);
        when(modelMapper.map(ticket, TicketDto.class)).thenReturn(ticketDto);

        var response = ticketService.buyTickets(listTicketDto, email, id);
        verify(userRepository, times(1)).findUserByEmail(email);
        verify(travelRepository, times(1)).findById(id);
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(NotificationDto.class));
        verify(paymentClient, times(1)).createOrUpdatePayment(any(PaymentDto.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void buy_ticket_individual() {
        var email = "test@gmail.com";
        var user = new User();
        var id = 1L;
        user.setUserType(UserType.INDIVIDUAL);
        user.setEmail(email);
        var travel = new Travel();
        travel.setFromStation("İSTANBUL");
        travel.setToStation("MUĞLA");
        var passenger = new Passenger();
        passenger.setGender(Gender.FEMALE);


        travel.setSeatCapacity(45);

        PaymentDto paymentDto = PaymentDto.builder().build();
        var listTicketDto = new ArrayList<TicketDto>();
        listTicketDto.add(TicketDto.builder()
                .id(id)
                .seatNumber(20)
                .payment(paymentDto)
                .passenger(PassengerDto.builder().gender(Gender.MALE).build()).build());
        var ticketDto = listTicketDto.stream().findFirst().get();

        var ticket = new Ticket();
        ticket.setTravel(travel);
        ticket.setUser(user);
        ticket.setPassenger(passenger);
        ticket.setSeatNumber(20);
        ticket.setId(id);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(travelRepository.findById(id)).thenReturn(Optional.of(travel));
        when(ticketRepository.save(ticket)).thenReturn(ticket);


        when(paymentClient.createOrUpdatePayment(paymentDto)).thenReturn(ResponseEntity.ok().body(paymentDto));
        when(modelMapper.map(ticketDto, Ticket.class)).thenReturn(ticket);
        when(modelMapper.map(ticket, TicketDto.class)).thenReturn(ticketDto);

        var response = ticketService.buyTickets(listTicketDto, email, id);
        verify(userRepository, times(1)).findUserByEmail(email);
        verify(travelRepository, times(1)).findById(id);
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(NotificationDto.class));
        verify(paymentClient, times(1)).createOrUpdatePayment(any(PaymentDto.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private List<Ticket> prepareTickets(int i, List<Ticket> emptyList, Ticket ticket) {
        for (int j = 1; j <= i; j++) {
            emptyList.add(ticket);
        }
        return emptyList;
    }
}