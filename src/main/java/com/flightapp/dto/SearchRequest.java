package com.flightapp.dto;

import java.time.LocalDate;

public class SearchRequest {
	public String from;
	public String to;
	public LocalDate date;
	public boolean roundTrip;
}
