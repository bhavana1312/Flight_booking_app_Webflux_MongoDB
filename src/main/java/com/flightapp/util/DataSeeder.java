package com.flightapp.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.flightapp.model.Airline;
import com.flightapp.model.Inventory;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.InventoryRepository;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;

@Component
public class DataSeeder implements CommandLineRunner {

	private final AirlineRepository airlineRepo;
	private final InventoryRepository invRepo;

	public DataSeeder(AirlineRepository airlineRepo, InventoryRepository invRepo) {
		this.airlineRepo = airlineRepo;
		this.invRepo = invRepo;
	}

	@Override
	public void run(String... args) {

		airlineRepo.count().flatMap(count -> {
			if (count == 0) {
				Airline a = new Airline("AirIndia", "AI", "sample-logo.png");
				return airlineRepo.save(a);
			} else {
				return airlineRepo.findAll().next();
			}
		}).flatMap(savedAirline -> invRepo.count().flatMap(invCount -> {
			if (invCount == 0) {
				return createInventory(savedAirline.id);
			} else {
				return Mono.empty();
			}
		})).subscribe();
	}

	private Mono<Inventory> createInventory(String airlineId) {
		Inventory inv = new Inventory();
		inv.airlineId = airlineId;
		inv.flightNumber = "AI123";
		inv.from = "HYD";
		inv.to = "DEL";
		inv.departure = LocalDateTime.now().plusDays(3);
		inv.arrival = inv.departure.plusHours(2);
		inv.totalSeats = 120;
		inv.availableSeats = 120;
		inv.price = 3500.0;
		inv.seatMap = new HashMap<>();
		return invRepo.save(inv);
	}
}
