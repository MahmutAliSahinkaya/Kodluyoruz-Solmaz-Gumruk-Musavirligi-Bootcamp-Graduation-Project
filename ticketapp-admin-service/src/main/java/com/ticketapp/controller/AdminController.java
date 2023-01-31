package com.ticketapp.controller;

import com.ticketapp.dto.AdminDto;
import com.ticketapp.dto.TravelDto;
import com.ticketapp.model.enums.Vehicle;
import com.ticketapp.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("admins")
public class AdminController {
    private final AdminService adminService;
    @PostMapping
    public ResponseEntity<String> registerAdmin(@RequestBody AdminDto adminDto){
        return adminService.register(adminDto);
    }

    @PostMapping("/{email}/{password}")
    public ResponseEntity<String> loginAdmin(@PathVariable String email, @PathVariable String password){
        log.info("admin conroller : login admin");
        return adminService.login(email,password);
    }

    @PostMapping("travels/{email}")
    public ResponseEntity<TravelDto> addTravel(@RequestBody TravelDto travelDto, @PathVariable String email){
        log.info("admin conroller : add Travel");
        return adminService.addTravel(travelDto,email);
    }
    @DeleteMapping("travels/{travelId}/{email}")
    public ResponseEntity<String> cancelTravel(@PathVariable Long travelId,@PathVariable String email){
        log.info("admin conroller : cancel Travel");
        return adminService.cancelTravel(travelId,email);
    }
    @GetMapping("total/{email}")
    public ResponseEntity<String> getTotalAndCounts(@PathVariable String email){
        log.info("admin conroller : get total and counts");
        return adminService.getTotalAndCounts(email);
    }
    @GetMapping("travels")
    public ResponseEntity<List<TravelDto>> getTravelByPropertiesOrAll(@RequestParam(value = "vehicle", required = false) Vehicle vehicle
            , @RequestParam(value = "to", required = false) String toStation
            , @RequestParam(value = "from", required = false) String fromStation
            , @RequestParam(value = "arrivalTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalTime
            , @RequestParam(value = "departureTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime) {
        log.info("admin conroller : get travel by properties");
        return adminService.getTravelByPropertiesOrAll(vehicle, toStation, fromStation, arrivalTime, departureTime);
    }

}
