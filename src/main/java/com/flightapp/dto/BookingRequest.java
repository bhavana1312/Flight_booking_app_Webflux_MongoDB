package com.flightapp.dto;

import java.util.List;
import lombok.Data;

@Data
public class BookingRequest {
	private String name;
	private String email;
	private int seats;
	private List<PassengerDto> passengers;
	private String meal;
	private List<String> seatNumbers;
}
