package com.ticketapp.repository;

import com.ticketapp.model.Travel;
import com.ticketapp.model.enums.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Long> {
    @Query("select t from Travel t where (cast(:arrivalTime as timestamp) is null or t.arrivalTime= :arrivalTime)" +
            "and (cast(:departureTime as timestamp) is null or t.departureTime= :departureTime)" +
            "and (:vehicle is null or t.vehicle= :vehicle)" +
            "and (:toStation is null or t.toStation= :toStation)" +
            "and (:fromStation is null or t.fromStation= :fromStation)")
    List<Travel> findTravelsByProperties(
            @Param("arrivalTime") LocalDateTime arrivalTime,
            @Param("departureTime") LocalDateTime departureTime,
            @Param("vehicle") Vehicle vehicle,
            @Param("toStation") String toStation,
            @Param("fromStation") String fromStation);
}
