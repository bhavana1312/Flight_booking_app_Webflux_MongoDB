package com.flightapp.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Document
public class Inventory{
 @Id public String id;
 public String airlineId;
 public String flightNumber;
 public String from;
 public String to;
 public LocalDateTime departure;
 public LocalDateTime arrival;
 public int totalSeats;
 public int availableSeats;
 public double price;
 public Map<String,Boolean> seatMap;
}
