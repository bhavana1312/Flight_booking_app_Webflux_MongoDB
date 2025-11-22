package com.flightapp.service.impl;

import com.flightapp.dto.InventoryRequest;
import com.flightapp.model.Airline;
import com.flightapp.model.Inventory;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.InventoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class InventoryServiceImplTest {

	@Mock
	private InventoryRepository invRepo;

	@Mock
	private AirlineRepository airlineRepo;

	@InjectMocks
	private InventoryServiceImpl service;

	private InventoryRequest req;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);

		req = new InventoryRequest();
		req.airlineName = "Air India";
		req.airlineCode = "AI";
		req.airlineLogo = "logo.png";
		req.flightNumber = "AI123";
		req.from = "DEL";
		req.to = "MUM";
		req.departure = java.time.LocalDateTime.now().plusDays(1);
		req.arrival = req.departure.plusHours(2);
		req.totalSeats = 100;
		req.price = 5000;
	}

	@Test
	void addInventory_createsAirlineIfNotExists() {

		when(airlineRepo.findByCode("AI")).thenReturn(Mono.empty());
		when(airlineRepo.save(any())).thenReturn(Mono.just(new Airline("Air India", "AI", "logo.png")));
		when(invRepo.findByAirlineIdAndFlightNumber(any(), any())).thenReturn(Mono.empty());
		when(invRepo.save(any())).thenReturn(Mono.just(new Inventory()));

		StepVerifier.create(service.addInventory(req)).expectNextCount(1).verifyComplete();
	}

	
}
