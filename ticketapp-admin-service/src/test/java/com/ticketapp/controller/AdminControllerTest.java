package com.ticketapp.controller;

import com.ticketapp.dto.AdminDto;
import com.ticketapp.dto.TravelDto;
import com.ticketapp.model.enums.Vehicle;
import com.ticketapp.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AdminControllerTest {
    @Mock
    private AdminService adminService;
    @InjectMocks
    private AdminController adminController;

    @Test
    public void testRegisterAdmin() {
        AdminDto adminDto = new AdminDto();
        adminDto.setEmail("admin@example.com");
        adminDto.setPassword("password");
        when(adminService.register(adminDto)).thenReturn(ResponseEntity.ok("Admin successfully registered"));
        ResponseEntity<String> response = adminController.registerAdmin(adminDto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Admin successfully registered", response.getBody());
        verify(adminService, times(1)).register(adminDto);
    }

    @Test
    public void testLoginAdmin() {
        String email = "admin@example.com";
        String password = "password";
        when(adminService.login(email, password)).thenReturn(ResponseEntity.ok("Admin successfully logged in"));
        ResponseEntity<String> response = adminController.loginAdmin(email, password);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Admin successfully logged in", response.getBody());
        verify(adminService, times(1)).login(email, password);
    }

    @Test
    public void testAddTravel() {
        String email = "admin@example.com";
        TravelDto travelDto = new TravelDto();
        when(adminService.addTravel(travelDto, email)).thenReturn(ResponseEntity.ok(new TravelDto()));
        ResponseEntity<TravelDto> response = adminController.addTravel(travelDto, email);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(adminService, times(1)).addTravel(travelDto, email);
    }

    @Test
    public void testCancelTravel() {
        Long travelId = 1L;
        String email = "admin@example.com";
        when(adminService.cancelTravel(travelId, email)).thenReturn(ResponseEntity.ok("Travel successfully canceled"));
        ResponseEntity<String> response = adminController.cancelTravel(travelId, email);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Travel successfully canceled", response.getBody());
        verify(adminService, times(1)).cancelTravel(travelId, email);
    }

    @Test
    public void testGetTotalAndCounts() {
        String email = "admin@example.com";
        when(adminService.getTotalAndCounts(email)).thenReturn(ResponseEntity.ok("Total and counts retrieved successfully"));
        ResponseEntity<String> response = adminController.getTotalAndCounts(email);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Total and counts retrieved successfully", response.getBody());
        verify(adminService, times(1)).getTotalAndCounts(email);
    }

    @Test
    public void testGetTravelByPropertiesOrAll() {
        Vehicle vehicle = Vehicle.BUS;
        String toStation = "Istanbul";
        String fromStation = "Ankara";
        LocalDateTime arrivalTime = LocalDateTime.of(2022, 12, 31, 12, 0, 0);
        LocalDateTime departureTime = LocalDateTime.of(2022, 12, 31, 12, 0, 0);
        when(adminService.getTravelByPropertiesOrAll(vehicle, toStation, fromStation, arrivalTime, departureTime)).thenReturn(ResponseEntity.ok(new ArrayList<TravelDto>()));
        ResponseEntity<List<TravelDto>> response = adminController.getTravelByPropertiesOrAll(vehicle, toStation, fromStation, arrivalTime, departureTime);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(adminService, times(1)).getTravelByPropertiesOrAll(vehicle, toStation, fromStation, arrivalTime, departureTime);
    }
}