package com.flightapp.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.flightapp.model.Airline;
import com.flightapp.model.Inventory;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.InventoryRepository;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {
	private final AirlineRepository aRepo;
	private final InventoryRepository iRepo;

	public DataSeeder(AirlineRepository aRepo, InventoryRepository iRepo) {
		this.aRepo = aRepo;
		this.iRepo = iRepo;
	}

	@Override
	public void run(String... args) throws Exception {
		aRepo.count().flatMapMany(c -> {
			return aRepo.save(new Airline("ExampleAir", "EX", ""));
		}).subscribe();
		Inventory inv = new Inventory();
		inv.airlineId = null;
		inv.flightNumber = "EX123";
		inv.from = "HYD";
		inv.to = "DEL";
		inv.departure = LocalDateTime.now().plusDays(3);
		inv.arrival = inv.departure.plusHours(2);
		inv.totalSeats = 120;
		inv.availableSeats = 120;
		inv.price = 3500.0;
		iRepo.save(inv).subscribe();
	}
}
