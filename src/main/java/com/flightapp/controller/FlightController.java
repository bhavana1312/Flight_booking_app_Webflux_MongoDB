package com.flightapp.controller;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.InventoryRequest;
import com.flightapp.dto.SearchRequest;
import com.flightapp.model.Booking;
import com.flightapp.model.Inventory;
import com.flightapp.repository.InventoryRepository;
import com.flightapp.service.BookingService;
import com.flightapp.service.InventoryService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/flight")
public class FlightController {
	private final InventoryRepository invRepo;
	private final BookingService bookingService;
	private final InventoryService inventoryService;

	public FlightController(InventoryRepository invRepo, BookingService bookingService,
			InventoryService inventoryService) {
		this.invRepo = invRepo;
		this.bookingService = bookingService;
		this.inventoryService = inventoryService;
	}

	@PostMapping("/search")
	public Flux<Inventory> search(@RequestBody SearchRequest req) {
		LocalDateTime start = req.getDate().atStartOfDay();
		LocalDateTime end = start.plusDays(1);
		return invRepo.findByFromAndToAndDepartureBetween(req.getFrom(), req.getTo(), start, end)
				.sort((a, b) -> a.getDeparture().compareTo(b.getDeparture()));

	}

	@PostMapping("/booking/{flightid}")
	public Mono<Booking> book(@PathVariable("flightid") String flightid, @RequestBody BookingRequest req) {
		return bookingService.book(flightid, req);
	}

	@GetMapping("/ticket/{pnr}")
	public Mono<Booking> getByPnr(@PathVariable("pnr") String pnr) {
		return bookingService.findByPnr(pnr);
	}

	@DeleteMapping("/booking/cancel/{pnr}")
	public Mono<Booking> cancel(@PathVariable("pnr") String pnr) {
		return bookingService.cancelTicket(pnr);
	}

	@GetMapping("/booking/history/{email}")
	public Flux<Booking> history(@PathVariable("email") String email) {
		return bookingService.history(email);
	}

	@PostMapping("/airline/inventory/add")
	public Mono<Inventory> addInventory(@RequestBody InventoryRequest req) {
		return inventoryService.addInventory(req);
	}

}
