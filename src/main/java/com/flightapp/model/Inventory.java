package com.flightapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document
public class Inventory {

    @Id
    private String id;

    private String airlineId;
    private String flightNumber;
    private String from;
    private String to;
    private LocalDateTime departure;
    private LocalDateTime arrival;
    private int totalSeats;
    private int availableSeats;
    private double price;
    private Map<String, Boolean> seatMap;
}
