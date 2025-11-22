package com.flightapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document
public class Booking {

	@Id
	private String id;

	private String pnr;
	private String flightId;
	private String email;
	private String name;
	private int seatsBooked;
	private List<Passenger> passengers;
	private boolean cancelled;
	private LocalDateTime bookedAt;
	private LocalDateTime journeyDate;
	private double amount;
}
