package com.ticketapp.repository;

import com.ticketapp.model.Sms;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsRepository extends MongoRepository<Sms, String> {
}
