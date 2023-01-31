package com.ticketapp.controller;

import com.ticketapp.dto.TravelDto;
import com.ticketapp.model.enums.Vehicle;
import com.ticketapp.service.TravelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class TravelControllerTest {
    @InjectMocks
    private TravelController travelController;
    @Mock
    private TravelService travelService;

    @Test
    public void testGetTravelByPropertiesOrAll() {
        Vehicle vehicle = Vehicle.BUS;
        String toStation = "Istanbul";
        String fromStation = "Ankara";
        LocalDateTime arrivalTime = LocalDateTime.now();
        LocalDateTime departureTime = LocalDateTime.now();
        List<TravelDto> expectedTravels = Arrays.asList(
                new TravelDto(),
                new TravelDto()
        );
        when(travelService.getTravelByPropertiesOrAll(vehicle, toStation, fromStation, arrivalTime, departureTime)).thenReturn(ResponseEntity.ok(expectedTravels));
        ResponseEntity<List<TravelDto>> response = travelController.getTravelByPropertiesOrAll(vehicle, toStation, fromStation, arrivalTime, departureTime);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTravels, response.getBody());
        verify(travelService, times(1)).getTravelByPropertiesOrAll(vehicle, toStation, fromStation, arrivalTime, departureTime);
    }
    @Test
    public void testGetTravelById() {
        Long id = 1L;
        TravelDto expectedTravel = new TravelDto();
        when(travelService.getTravelById(id)).thenReturn(ResponseEntity.ok(expectedTravel));
        ResponseEntity<TravelDto> response = travelController.getTravelById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTravel, response.getBody());
        verify(travelService, times(1)).getTravelById(id);
    }

}