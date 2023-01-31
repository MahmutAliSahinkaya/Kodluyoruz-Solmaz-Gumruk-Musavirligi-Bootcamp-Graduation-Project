package com.ticketapp.controller;

import com.ticketapp.dto.TicketDto;
import com.ticketapp.model.Travel;
import com.ticketapp.model.User;
import com.ticketapp.repository.TravelRepository;
import com.ticketapp.repository.UserRepository;
import com.ticketapp.exception.TravelNotFoundException;
import com.ticketapp.exception.UserNotFoundException;
import com.ticketapp.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;
    private final UserRepository userRepository;
    private final TravelRepository travelRepository;


    @PostMapping("/buy")
    public ResponseEntity<List<TicketDto>> buyTickets(@RequestBody List<TicketDto> ticketDtos, @RequestParam String userEmail, @RequestParam Long travelId) {
        log.info("ticket controller, buyTickets");
        return ticketService.buyTickets(ticketDtos, userEmail, travelId);
    }




    @GetMapping("/user/{userEmail}")
    public ResponseEntity<List<TicketDto>> getTicketsByUserEmail(@PathVariable String userEmail) {
        return ticketService.getTicketsByUserEmail(userEmail);
    }

    @PostMapping("/save")
    public ResponseEntity<List<TicketDto>> saveAndResponse(@RequestBody List<TicketDto> ticketDtos, @RequestParam String userEmail, @RequestParam Long travelId) {
        User user = userRepository.findUserByEmail(userEmail).orElseThrow(UserNotFoundException::new);
        Travel travel = travelRepository.findById(travelId).orElseThrow(TravelNotFoundException::new);
        return ticketService.saveAndResponse(ticketDtos, user, travel, travelId);
    }

}
