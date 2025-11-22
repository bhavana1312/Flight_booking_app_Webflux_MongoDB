package com.flightapp.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.flightapp.dto.InventoryRequest;
import com.flightapp.model.Airline;
import com.flightapp.model.Inventory;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.InventoryRepository;
import com.flightapp.service.InventoryService;

import reactor.core.publisher.Mono;

@Service
public class InventoryServiceImpl implements InventoryService {

	private final InventoryRepository repo;
	private final AirlineRepository airlineRepo;

	public InventoryServiceImpl(InventoryRepository repo, AirlineRepository airlineRepo) {
		this.repo = repo;
		this.airlineRepo = airlineRepo;
	}

	@Override
	public Mono<Inventory> addInventory(InventoryRequest req) {

		return airlineRepo.findByCode(req.getAirlineCode()).switchIfEmpty(Mono.defer(() -> {
			Airline airline = new Airline();
			airline.setName(req.getAirlineName());
			airline.setCode(req.getAirlineCode());
			airline.setLogoUrl(req.getAirlineLogo());
			return airlineRepo.save(airline);
		})).flatMap(airline -> {

			Inventory inv = new Inventory();
			
			inv.setAirlineId(airline.getId());

			inv.setFlightNumber(req.getFlightNumber());
			inv.setFrom(req.getFrom());
			inv.setTo(req.getTo());
			inv.setDeparture(req.getDeparture());
			inv.setArrival(req.getArrival());
			inv.setPrice(req.getPrice());
			inv.setTotalSeats(req.getTotalSeats());
			inv.setAvailableSeats(req.getTotalSeats());

			Map<String, Boolean> seats = new HashMap<>();
			for (int i = 1; i <= req.getTotalSeats(); i++) {
			    seats.put("S" + i, false);
			}
			inv.setSeatMap(seats);
			
			return repo.findByAirlineIdAndFlightNumber(airline.getId(), req.getFlightNumber()).hasElement().flatMap(exists -> {
				if (Boolean.TRUE.equals(exists)) {
					return Mono.error(new RuntimeException("Flight already exists for this airline"));
				}
				return repo.save(inv);
			});
		});
	}
}
