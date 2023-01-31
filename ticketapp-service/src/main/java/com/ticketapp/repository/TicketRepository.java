package com.ticketapp.repository;

import com.ticketapp.model.Ticket;
import com.ticketapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findTicketsByUser(User user);
}
