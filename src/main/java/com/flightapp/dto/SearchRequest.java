package com.flightapp.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class SearchRequest {
	private String from;
	private String to;
	private LocalDate date;
	private boolean roundTrip;
}
