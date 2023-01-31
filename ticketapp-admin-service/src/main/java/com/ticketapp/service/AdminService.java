package com.ticketapp.service;

import com.google.common.hash.Hashing;
import com.ticketapp.client.PaymentClient;
import com.ticketapp.dto.AdminDto;
import com.ticketapp.dto.PaymentDto;
import com.ticketapp.dto.TravelDto;
import com.ticketapp.exception.MailAlreadyInUseException;
import com.ticketapp.exception.WrongPasswordException;
import com.ticketapp.exception.UserNotFoundException;
import com.ticketapp.model.Admin;
import com.ticketapp.model.Travel;
import com.ticketapp.model.enums.Vehicle;
import com.ticketapp.repository.AdminRepository;
import com.ticketapp.repository.TicketRepository;
import com.ticketapp.repository.TravelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;
    private final TravelRepository travelRepository;
    private final TicketRepository ticketRepository;
    private final PaymentClient paymentClient;

    public ResponseEntity<String> register(AdminDto adminDto) {
        log.info("admin service, register");
        adminRepository.findAdminByEmail(adminDto.getEmail()).ifPresent(admin -> {
            throw new MailAlreadyInUseException();
        });
        adminDto.setPassword(Hashing.sha256().hashString(adminDto.getPassword(), StandardCharsets.UTF_8).toString());
        adminRepository.save(modelMapper.map(adminDto, Admin.class));
        return ResponseEntity.ok().body("Kaydınız başarı ile tamamlandı.");
    }

    public ResponseEntity<String> login(String email, String password) {
        log.info("admin service login");
        Admin admin = adminRepository.findAdminByEmail(email).orElseThrow(UserNotFoundException::new);
        if (Objects.equals(admin.getPassword(), Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString())) {
            return ResponseEntity.ok().body("Oturum başarıyla açıldı.");
        } else throw new WrongPasswordException();
    }

    public ResponseEntity<TravelDto> addTravel(TravelDto request, String email) {
        log.info("admin service, add travel");
        adminRepository.findAdminByEmail(email).orElseThrow(UserNotFoundException::new);
        Travel travel = modelMapper.map(request, Travel.class);
        if (request.getVehicle() == Vehicle.BUS) travel.setSeatCapacity(45);
        else if (request.getVehicle() == Vehicle.PLANE) travel.setSeatCapacity(189);
        return ResponseEntity.ok().body(modelMapper.map(travelRepository.save(travel), TravelDto.class));
    }

    public ResponseEntity<String> getTotalAndCounts(String email) {
        log.info("admin service, get total and counts");
        adminRepository.findAdminByEmail(email).orElseThrow(UserNotFoundException::new);
        long count = ticketRepository.count();
        return ResponseEntity.ok().body("Toplam bilet sayısı: " + count + "\n"
                + "Topam gelir: " + ticketRepository.totalPrice());

    }

    public ResponseEntity<List<TravelDto>> getTravelByPropertiesOrAll(Vehicle vehicle, String toStation, String fromStation, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        log.info("admin service, get travels by properties");
        List<Travel> travels = travelRepository.findTravelsByProperties(arrivalTime, departureTime, vehicle, toStation, fromStation);
        return ResponseEntity.ok().body(travels.stream().map(travel -> modelMapper.map(travel, TravelDto.class)).toList());
    }

    public ResponseEntity<String> cancelTravel(Long travelId, String email) {
        log.info("admin service, cancelTravel");
        adminRepository.findAdminByEmail(email).orElseThrow(UserNotFoundException::new);
        Travel travel = travelRepository.findById(travelId).orElseThrow();
        travel.setIsCanceled(Boolean.TRUE);
        travelRepository.save(travel);
        if (travel.getTickets().isEmpty()) {
            log.info("hiç bilet yok.");
            return ResponseEntity.badRequest().body("Sefer iptal edildi,seferden hiç bilet satın alınmamış.");
        } else {
            travel.getTickets().forEach(ticket -> {
                List<PaymentDto> paymentDtoList = paymentClient.getPaymentsOfTravel(travelId).getBody();
                assert paymentDtoList != null;
                paymentDtoList.forEach(paymentDto -> {
                    paymentDto.setIsCanceled(Boolean.TRUE);
                    log.info(paymentDto.toString() + " iptal edildi");
                    paymentClient.createOrUpdatePayment(paymentDto);
                });
            });
        }
        return ResponseEntity.ok().body(travelId + " numaralı sefer iptal edildi.Ve biletlerin ödemeleri geri ödenecek şeklinde işaretlendi.");
    }

}
