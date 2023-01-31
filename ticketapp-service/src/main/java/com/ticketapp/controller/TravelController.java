package com.ticketapp.controller;

import com.ticketapp.dto.TravelDto;
import com.ticketapp.model.enums.Vehicle;
import com.ticketapp.service.TravelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/travels")
public class TravelController {
    private final TravelService travelService;


    @GetMapping
    public ResponseEntity<List<TravelDto>> getTravelByPropertiesOrAll(@RequestParam(value = "vehicle", required = false) Vehicle vehicle,
                                                                      @RequestParam(value = "toStation", required = false) String toStation,
                                                                      @RequestParam(value = "fromStation", required = false) String fromStation,
                                                                      @RequestParam(value = "arrivalTime", required = false) LocalDateTime arrivalTime,
                                                                      @RequestParam(value = "departureTime", required = false) LocalDateTime departureTime) {
        log.info("Travel controller, getTravelByPropertiesOrAll");
        return travelService.getTravelByPropertiesOrAll(vehicle, toStation, fromStation, arrivalTime, departureTime);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelDto> getTravelById(@PathVariable Long id) {
        return travelService.getTravelById(id);
    }

}
