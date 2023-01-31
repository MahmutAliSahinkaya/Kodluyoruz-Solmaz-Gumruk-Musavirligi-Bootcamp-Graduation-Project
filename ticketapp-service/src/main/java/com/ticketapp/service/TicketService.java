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
        } else throw new IllegalArgumentException("Böyle bir kullanıcı tipi mevcut değil");

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
        log.info("sms gönderildi");

        rabbitTemplate.convertAndSend("ticketService", "ticketService",
                NotificationDto.builder()
                        .sendingTime(LocalDateTime.now().toString())
                        .to(ticketDto.getPassenger().getMobilePhoneNumber())
                        .from("444 0 444")
                        .type(NotificationType.SMS)
                        .title("")
                        .text(travel.getFromStation().toString() + " " + travel.getToStation().toString() + " seferinden biletiniz satın alınmıştır.")
                        .build()
        );
    }

    private static class CorporateStrategy implements TicketBuyStrategy {
        @Override
        public boolean checkConditions(List<TicketDto> ticketDtos, int boughtAndWillBeCount) {
            if (boughtAndWillBeCount < 20) { //Alınmış ve alınacak biletlerin toplamı 20ye eşit veya azmı ?
                return true;
            } else {
                throw new TicketBuyCondidationsException("Kurumsal kullanıcılar bir seferden 20 den fazla bilet alamazlar.");
            }
        }
    }

    private static class IndividualStrategy implements TicketBuyStrategy {

        @Override
        public boolean checkConditions(List<TicketDto> ticketDtos, int boughtAndWillBeCount) {
            if (boughtAndWillBeCount <= 5) { // Alınmış ve alınacak biletlerin toplamı 5e eşit veya azmı ?
                if (ticketDtos.stream().filter(ticketDto -> ticketDto.getPassenger().getGender() == Gender.MALE).count() <= 2) {//aynı anda alınan biletlerde 2 den fazla erkek varmı ?
                    return true;
                } else
                    throw new TicketBuyCondidationsException("Bireysel kullanıcılar bir siparişte en fazla 2 erkek yolcu için bilet alabilir");
            } else {
                throw new TicketBuyCondidationsException("Bireysel kullanıcılar bir seferden 5 den fazla bilet alamazlar.");
            }
        }
    }

    public interface TicketBuyStrategy {
        boolean checkConditions(List<TicketDto> ticketDtos, int boughtAndWillBeCount);
    }

}
