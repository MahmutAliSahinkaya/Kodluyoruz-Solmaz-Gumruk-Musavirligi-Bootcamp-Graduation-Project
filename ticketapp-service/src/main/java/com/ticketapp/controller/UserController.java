package com.ticketapp.controller;

import com.ticketapp.dto.TicketDto;
import com.ticketapp.dto.UserDto;
import com.ticketapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto request) {
        return userService.registerUser(request);
    }
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String email, @RequestParam String password) {
        return userService.loginUser(email, password);
    }
    @GetMapping("/{id}/tickets")
    public ResponseEntity<List<TicketDto>> getTicketsByUserId(@PathVariable Long id) {
        log.info("user controller, getTicketsByUserId");
        return userService.getTicketsByUserId(id);
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getByUserEmail(@PathVariable("email") String email) {
        return userService.getByUserEmail(email);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteByUserEmail(@PathVariable String email) {
        return userService.deleteByUserEmail(email);
    }

}
