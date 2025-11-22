package com.flightapp.service.impl;

import java.util.HashMap;

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

        // Step 1: Check if airline exists
        return airlineRepo.findByCode(req.airlineCode)
                .switchIfEmpty(
                        // Create airline if not exist
                        airlineRepo.save(new Airline(req.airlineName, req.airlineCode, req.airlineLogo))
                )
                .flatMap(airline -> {

                    Inventory inv = new Inventory();

                    inv.airlineId = airline.id;
                    inv.flightNumber = req.flightNumber;
                    inv.from = req.from;
                    inv.to = req.to;
                    inv.departure = req.departure;
                    inv.arrival = req.arrival;
                    inv.price = req.price;
                    inv.totalSeats = req.totalSeats;
                    inv.availableSeats = req.totalSeats;

                    // Seat map initialization
                    inv.seatMap = new HashMap<>();
                    for (int i = 1; i <= req.totalSeats; i++) {
                        inv.seatMap.put("S" + i, false);
                    }

                    // Step 4: Prevent duplicate flight for same airline
                    return repo.findByAirlineIdAndFlightNumber(airline.id, req.flightNumber)
                            .hasElement()
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(new RuntimeException("Flight already exists for this airline"));
                                }
                                return repo.save(inv);
                            });
                });
    }
}
