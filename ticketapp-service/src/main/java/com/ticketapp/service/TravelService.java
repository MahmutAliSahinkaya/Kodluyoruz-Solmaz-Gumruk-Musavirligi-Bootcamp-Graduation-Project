package com.ticketapp.service;

import com.ticketapp.dto.TravelDto;
import com.ticketapp.model.Travel;
import com.ticketapp.model.enums.Vehicle;
import com.ticketapp.repository.TravelRepository;
import com.ticketapp.exception.TravelNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@RequiredArgsConstructor
@Slf4j
@Service
public class TravelService {
    private final TravelRepository travelRepository;
    private final ModelMapper modelMapper;

    public ResponseEntity<List<TravelDto>> getTravelByPropertiesOrAll(Vehicle vehicle, String toStation, String fromStation, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        log.info("Travel service, getTravelByPropertiesOrAll");
        List<Travel> travels = travelRepository.findActiveTravelsByProperties(arrivalTime, departureTime, vehicle, toStation, fromStation);
        return ResponseEntity.ok().body(travels.stream().map(travel -> modelMapper.map(travel, TravelDto.class)).toList());
    }


    public ResponseEntity<TravelDto> getTravelById(Long id) {
        log.info("travel service, getTravelById");
        Travel travel = travelRepository.findById(id).orElseThrow(TravelNotFoundException::new);
        return ResponseEntity.ok().body(modelMapper.map(travel, TravelDto.class));
    }

}
