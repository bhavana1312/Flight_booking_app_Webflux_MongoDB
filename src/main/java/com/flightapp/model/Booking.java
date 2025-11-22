package com.flightapp.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
@Document
public class Booking{
 @Id public String id;
 public String pnr;
 public String flightId;
 public String email;
 public String name;
 public int seatsBooked;
 public List<Passenger> passengers;
 public boolean cancelled;
 public LocalDateTime bookedAt;
 public LocalDateTime journeyDate;
 public double amount;
}
