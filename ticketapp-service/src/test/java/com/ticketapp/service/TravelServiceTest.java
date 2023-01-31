package com.ticketapp.service;

import com.ticketapp.exception.TravelNotFoundException;
import com.ticketapp.model.Travel;
import com.ticketapp.model.enums.Vehicle;
import com.ticketapp.repository.TravelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class TravelServiceTest {
    @Mock
    private TravelRepository travelRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private TravelService travelService;

    @Test
    void get_travels_by_properties() {
        var time = LocalDateTime.now();
        var vehicle = Vehicle.BUS;
        var list = new ArrayList<Travel>();
        list.add(new Travel());
        when(travelRepository.findActiveTravelsByProperties(time, time, vehicle, "toStation", "fromStation"))
                .thenReturn(list);
        var response = travelService.getTravelByPropertiesOrAll(vehicle, "toStation", "fromStation", time, time);
        verify(travelRepository, times(1)).findActiveTravelsByProperties(time, time, vehicle, "toStation", "fromStation");
        assertEquals(travelService.getTravelByPropertiesOrAll(vehicle, "toStation", "fromStation", time, time), response);
    }

    @Test
    void get_travel_by_id() {
        Long id = 15L;
        when(travelRepository.findById(id)).thenReturn(Optional.of(mock(Travel.class)));
        var response = travelService.getTravelById(id);

        verify(travelRepository, times(1)).findById(id);

        assertEquals(travelService.getTravelById(id), response);

    }

    @Test
    void cant_get_travel_by_id() {

        Long id = 15L;
        when(travelRepository.findById(id)).thenReturn(Optional.empty()).thenThrow(TravelNotFoundException.class);
        assertThrows(TravelNotFoundException.class, () -> travelService.getTravelById(id));
        verify(travelRepository, times(1)).findById(id);

    }
}