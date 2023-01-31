package com.ticketapp.service;

import com.google.common.hash.Hashing;
import com.ticketapp.client.PaymentClient;
import com.ticketapp.dto.AdminDto;
import com.ticketapp.dto.PaymentDto;
import com.ticketapp.dto.TravelDto;
import com.ticketapp.exception.MailAlreadyInUseException;
import com.ticketapp.exception.UserNotFoundException;
import com.ticketapp.exception.WrongPasswordException;
import com.ticketapp.model.Admin;
import com.ticketapp.model.Ticket;
import com.ticketapp.model.Travel;
import com.ticketapp.model.enums.Vehicle;
import com.ticketapp.repository.AdminRepository;
import com.ticketapp.repository.TicketRepository;
import com.ticketapp.repository.TravelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AdminServiceTest {
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private TravelRepository travelRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private PaymentClient paymentClient;
    @InjectMocks
    private AdminService adminService;

    @Test
    void cant_register_because_email_inuse() {
        AdminDto request = new AdminDto();
        request.setPassword("123456");
        request.setEmail("");

        when(adminRepository.findAdminByEmail(anyString())).thenReturn(Optional.of(mock(Admin.class))).thenThrow(MailAlreadyInUseException.class);
        verify(adminRepository, never()).save(any(Admin.class));

        assertThrows(MailAlreadyInUseException.class, () -> adminService.register(request));
    }

    @Test
    void register_succeeded() {
        AdminDto request = new AdminDto();
        request.setPassword("123456");
        request.setEmail("");
        var admin = modelMapper.map(request, Admin.class);

        when(adminRepository.findAdminByEmail(anyString())).thenReturn(Optional.empty());
        var response = adminService.register(request);
        verify(adminRepository, times(1)).save(admin);


        assertEquals(ResponseEntity.ok().body("Kaydınız başarı ile tamamlandı."), response);
    }

    @Test
    void login_fail_because_admin_not_found() {
        var email = "test@gmail.com";
        var password = "123123";
        when(adminRepository.findAdminByEmail(email)).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class, () -> adminService.login(email, password));
    }

    @Test
    void login_fail_because_wrong_password() {
        var email = "test@gmail.com";
        var password = "123123";
        when(adminRepository.findAdminByEmail(anyString())).thenReturn(Optional.of(mock(Admin.class)));
        assertThrows(WrongPasswordException.class, () -> adminService.login(email, password));
    }

    @Test
    void login_succeeded() {
        var email = "test@gmail.com";
        var password = "123123";

        Admin admin = new Admin();
        when(adminRepository.findAdminByEmail(anyString())).thenReturn(Optional.of(admin));
        admin.setPassword(Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
        assertEquals(adminService.login(email, password), ResponseEntity.ok().body("Oturum başarıyla açıldı."));
    }

    @Test
    void add_travel_bus() {
        var email = "test@gmail.com";
        var password = "123123";
        Admin admin = new Admin();
        TravelDto travelDto = new TravelDto();
        travelDto.setVehicle(Vehicle.BUS);
        travelDto.setId(1L);
        var travel = new Travel();
        travel.setVehicle(Vehicle.BUS);
        travelDto.setId(1L);

        when(modelMapper.map(travelDto, Travel.class)).thenReturn(travel);
        when(adminRepository.findAdminByEmail(email)).thenReturn(Optional.of(admin));
        when(travelRepository.save(travel)).thenReturn(travel);

        admin.setPassword(Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
        var response = adminService.addTravel(travelDto, email);


        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void add_travel_plane() {
        var email = "test@gmail.com";
        var password = "123123";
        Admin admin = new Admin();
        TravelDto travelDto = new TravelDto();
        travelDto.setVehicle(Vehicle.PLANE);
        travelDto.setId(1L);
        var travel = new Travel();
        travel.setVehicle(Vehicle.PLANE);
        travelDto.setId(1L);

        when(modelMapper.map(travelDto, Travel.class)).thenReturn(travel);
        when(adminRepository.findAdminByEmail(email)).thenReturn(Optional.of(admin));
        when(travelRepository.save(travel)).thenReturn(travel);

        admin.setPassword(Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
        var response = adminService.addTravel(travelDto, email);


        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void get_total_and_counts() {
        var email = "test@gmail.com";

        Admin admin = new Admin();
        Long count = 1L;
        when(travelRepository.count()).thenReturn(count);
        when(ticketRepository.totalPrice()).thenReturn(BigDecimal.ONE);
        when(adminRepository.findAdminByEmail(email)).thenReturn(Optional.of(admin));
        var response = adminService.getTotalAndCounts(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getTravelByPropertiesOrAll() {
        List<Travel> travels = new ArrayList<>();
        Travel travel = new Travel();
        var time = LocalDateTime.now();
        when(travelRepository.findTravelsByProperties(time, time, Vehicle.BUS, "İSTANBUL", "ANTALYA")).thenReturn(travels);
        when(modelMapper.map(travel, TravelDto.class)).thenReturn(new TravelDto());

        var response = adminService.getTravelByPropertiesOrAll(Vehicle.BUS, "İSTANBUL", "ANTALYA", time, time);
        assertEquals(travels.stream().map(travel1 -> modelMapper.map(travel1, TravelDto.class)).toList(), response.getBody());
    }

    @Test
    void cancelTravel() {
        var email = "test@gmail.com";
        Admin admin = new Admin();
        Travel travel = new Travel();
        Ticket ticket = new Ticket();

        travel.getTickets().add(ticket);
        var listpayment = new ArrayList<PaymentDto>();
        when(paymentClient.getPaymentsOfTravel(1L)).thenReturn(ResponseEntity.ok().body(listpayment));
        when(paymentClient.createOrUpdatePayment(PaymentDto.builder().build())).thenReturn(ResponseEntity.ok().body(PaymentDto.builder().build()));
        when(adminRepository.findAdminByEmail(email)).thenReturn(Optional.of(admin));
        when(travelRepository.save(travel)).thenReturn(travel);
        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));

        var response = adminService.cancelTravel(1L, email);

        assertEquals(1L + " numaralı sefer iptal edildi.Ve biletlerin ödemeleri geri ödenecek şeklinde işaretlendi.", response.getBody());
    }


}