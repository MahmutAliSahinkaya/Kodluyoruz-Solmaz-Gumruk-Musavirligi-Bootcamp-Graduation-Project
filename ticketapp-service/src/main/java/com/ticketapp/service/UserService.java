package com.ticketapp.service;

import com.google.common.hash.Hashing;
import com.ticketapp.dto.NotificationDto;
import com.ticketapp.dto.TicketDto;
import com.ticketapp.dto.UserDto;
import com.ticketapp.exception.MailAlreadyInUseException;
import com.ticketapp.exception.WrongPasswordException;
import com.ticketapp.exception.UserNotFoundException;
import com.ticketapp.model.User;
import com.ticketapp.model.enums.NotificationType;
import com.ticketapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RabbitTemplate rabbitTemplate;

    public ResponseEntity<String> registerUser(UserDto request) {
        log.info("user service, register");
        if (userRepository.findUserByEmail(request.getEmail()).isPresent())
            throw new MailAlreadyInUseException();
        else {
            request.setPassword(Hashing.sha256().hashString(request.getPassword(), StandardCharsets.UTF_8).toString());
            userRepository.save(modelMapper.map(request, User.class));
            rabbitTemplate.convertAndSend("ticketMaster", "ticketMaster", new NotificationDto(NotificationType.EMAIL,
                    "Ticket Master",
                    request.getEmail(),
                    "service@ticketmaster.com",
                    "Kaydınız başarı ile gerçekleşti",
                    LocalDateTime.now().toString()));
            return ResponseEntity.ok().body(request.getEmail() + " mailli kullanıcı başarı ile kayıt edildi");
        }
    }
    public ResponseEntity<String> loginUser(String email, String password) {
        log.info("user service, login");
        User user = userRepository.findUserByEmail(email).orElseThrow(UserNotFoundException::new);
        if (Objects.equals(user.getPassword(), Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString())) {
            return ResponseEntity.ok().body("Oturum başarıyla açıldı.");
        }else throw new WrongPasswordException();
    }
    public ResponseEntity<List<TicketDto>> getTicketsByUserId(Long id) {
        log.info("user service, getTicketsByUserId");
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return ResponseEntity.ok().body(user
                .getTickets().stream()
                .map(ticket -> modelMapper.map(ticket, TicketDto.class)).toList());
    }
    public ResponseEntity<UserDto> getByUserEmail(String email) {
        log.info("user service, getByUserEmail");
        User user = userRepository.findUserByEmail(email).orElseThrow(UserNotFoundException::new);
        return ResponseEntity.ok().body(modelMapper.map(user,UserDto.class));
    }
    public ResponseEntity<String> deleteByUserEmail(String email) {
        log.info("user service, deleteByUserEmail");
        User user = userRepository.findUserByEmail(email).orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
        return ResponseEntity.ok().body(email+" kullanıcısı başarıyla silindi");
    }

}
