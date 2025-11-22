package com.flightapp.dto;

import java.time.LocalDateTime;

public class InventoryRequest {
	public String airlineName;
	public String airlineCode;
	public String airlineLogo;

	public String flightNumber;
	public String from;
	public String to;
	public LocalDateTime departure;
	public LocalDateTime arrival;
	public int totalSeats;
	public double price;
}
