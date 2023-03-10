package com.ticketapp.service;

import com.ticketapp.client.PaymentClient;
import com.ticketapp.dto.NotificationDto;
import com.ticketapp.dto.TicketDto;
import com.ticketapp.exception.AlreadySoldSeatException;
import com.ticketapp.exception.SeatNotFoundException;
import com.ticketapp.exception.TicketBuyCondidationsException;
import com.ticketapp.exception.UserNotFoundException;
import com.ticketapp.exception.TravelNotFoundException;
import com.ticketapp.model.Passenger;
import com.ticketapp.model.Ticket;
import com.ticketapp.model.Travel;
import com.ticketapp.model.User;
import com.ticketapp.model.enums.Gender;
import com.ticketapp.model.enums.NotificationType;
import com.ticketapp.model.enums.UserType;
import com.ticketapp.repository.PassengerRepository;
import com.ticketapp.repository.TicketRepository;
import com.ticketapp.repository.TravelRepository;
import com.ticketapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TravelRepository travelRepository;
    private final ModelMapper modelMapper;
    private final PaymentClient paymentClient;
    private final RabbitTemplate rabbitTemplate;
    private final PassengerRepository passengerRepository;


    public ResponseEntity<List<TicketDto>> buyTickets(List<TicketDto> ticketDtos, String userEmail, Long travelId) {
        log.info("ticket service, buyTickets");
        User user = userRepository.findUserByEmail(userEmail).orElseThrow(UserNotFoundException::new);
        Travel travel = travelRepository.findById(travelId).orElseThrow(TravelNotFoundException::new);
        List<Ticket> boughtTickets = travel.getTickets().stream().filter(ticket -> ticket.getUser() == user).toList();
        int boughtAndWillBeCount = boughtTickets.size() + ticketDtos.size();
        if (user.getUserType() == UserType.CORPORATE && new CorporateStrategy().checkConditions(ticketDtos, boughtAndWillBeCount)) {
            return saveAndResponse(ticketDtos, user, travel, travelId);
        } else if (user.getUserType() == UserType.INDIVIDUAL && new IndividualStrategy().checkConditions(ticketDtos, boughtAndWillBeCount)) {
            return saveAndResponse(ticketDtos, user, travel, travelId);
        } else throw new IllegalArgumentException("B??yle bir kullan??c?? tipi mevcut de??il");

    }


    public ResponseEntity<List<TicketDto>> getTicketsByUserEmail(String userEmail) {
        log.info("ticket service, getTicketsByUserEmail");

        var tickets = ticketRepository.findTicketsByUser(userRepository.findUserByEmail(userEmail).orElseThrow(UserNotFoundException::new));
        return ResponseEntity.ok().body(tickets.stream().map(ticket -> modelMapper.map(ticket, TicketDto.class)).toList());
    }


    public ResponseEntity<List<TicketDto>> saveAndResponse(List<TicketDto> ticketDtos, User user, Travel travel, Long travelId) {
        log.info("ticket service, saveAndResponse");

        List<TicketDto> purchased = new ArrayList<>();
        ticketDtos.forEach(ticketDto -> {
            if (!travel.getPurchasedSeats().contains(ticketDto.getSeatNumber())) {
                if (ticketDto.getSeatNumber() <= travel.getSeatCapacity() && ticketDto.getSeatNumber() > 0) {
                    travel.getPurchasedSeats().add(ticketDto.getSeatNumber());
                    var paymentDto = ticketDto.getPayment();
                    paymentDto.setUserEmail(user.getEmail());
                    paymentDto.setPaymentTime(LocalDateTime.now());
                    paymentDto.setAmount(travel.getTicketPrice());
                    paymentDto.setSeatNumber(ticketDto.getSeatNumber());
                    paymentDto.setTravelId(travelId);

                    var payment = paymentClient.createOrUpdatePayment(paymentDto).getBody();
                    var ticket = modelMapper.map(ticketDto, Ticket.class);
                    ticket.setTravel(travel);
                    ticket.setUser(user);
                    if (ticketDto.getPassenger().getId() == null) {
                        var passenger = passengerRepository.save(ticket.getPassenger());
                        ticket.setPassenger(passenger);
                    } else
                        ticket.setPassenger(modelMapper.map(ticketDto.getPassenger(), Passenger.class));

                    var savedTicketDto = modelMapper.map(ticketRepository.save(ticket), TicketDto.class);
                    savedTicketDto.setPayment(payment);
                    purchased.add(savedTicketDto);

                    sendSms(travel, ticketDto);

                } else throw new SeatNotFoundException();
            } else {
                throw new AlreadySoldSeatException(ticketDto.getSeatNumber());
            }
        });
        List<TicketDto> returned = purchased.stream().map(ticket -> modelMapper.map(ticket, TicketDto.class)).toList();
        return ResponseEntity.ok().body(returned);
    }

    private void sendSms(Travel travel, TicketDto ticketDto) {
        log.info("sms g??nderildi");

        rabbitTemplate.convertAndSend("ticketService", "ticketService",
                NotificationDto.builder()
                        .sendingTime(LocalDateTime.now().toString())
                        .to(ticketDto.getPassenger().getMobilePhoneNumber())
                        .from("444 0 444")
                        .type(NotificationType.SMS)
                        .title("")
                        .text(travel.getFromStation().toString() + " " + travel.getToStation().toString() + " seferinden biletiniz sat??n al??nm????t??r.")
                        .build()
        );
    }

    private static class CorporateStrategy implements TicketBuyStrategy {
        @Override
        public boolean checkConditions(List<TicketDto> ticketDtos, int boughtAndWillBeCount) {
            if (boughtAndWillBeCount < 20) { //Al??nm???? ve al??nacak biletlerin toplam?? 20ye e??it veya azm?? ?
                return true;
            } else {
                throw new TicketBuyCondidationsException("Kurumsal kullan??c??lar bir seferden 20 den fazla bilet alamazlar.");
            }
        }
    }

    private static class IndividualStrategy implements TicketBuyStrategy {

        @Override
        public boolean checkConditions(List<TicketDto> ticketDtos, int boughtAndWillBeCount) {
            if (boughtAndWillBeCount <= 5) { // Al??nm???? ve al??nacak biletlerin toplam?? 5e e??it veya azm?? ?
                if (ticketDtos.stream().filter(ticketDto -> ticketDto.getPassenger().getGender() == Gender.MALE).count() <= 2) {//ayn?? anda al??nan biletlerde 2 den fazla erkek varm?? ?
                    return true;
                } else
                    throw new TicketBuyCondidationsException("Bireysel kullan??c??lar bir sipari??te en fazla 2 erkek yolcu i??in bilet alabilir");
            } else {
                throw new TicketBuyCondidationsException("Bireysel kullan??c??lar bir seferden 5 den fazla bilet alamazlar.");
            }
        }
    }

    public interface TicketBuyStrategy {
        boolean checkConditions(List<TicketDto> ticketDtos, int boughtAndWillBeCount);
    }

}
