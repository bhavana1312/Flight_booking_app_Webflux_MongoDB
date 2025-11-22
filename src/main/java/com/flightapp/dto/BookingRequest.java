package com.flightapp.dto;

import java.util.List;

public class BookingRequest {
	public String name;
	public String email;
	public int seats;
	public List<PassengerDto> passengers;
	public String meal;
	public List<String> seatNumbers;
}
