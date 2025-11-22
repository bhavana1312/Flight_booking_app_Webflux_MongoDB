package com.flightapp.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class InventoryRequest {
	private String airlineName;
	private String airlineCode;
	private String airlineLogo;

	private String flightNumber;
	private String from;
	private String to;
	private LocalDateTime departure;
	private LocalDateTime arrival;

	private int totalSeats;
	private double price;
}
