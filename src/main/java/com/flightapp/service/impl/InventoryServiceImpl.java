package com.flightapp.service.impl;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.flightapp.model.Inventory;
import com.flightapp.repository.InventoryRepository;
import com.flightapp.service.InventoryService;

import reactor.core.publisher.Mono;

@Service
public class InventoryServiceImpl implements InventoryService {
	private final InventoryRepository repo;

	public InventoryServiceImpl(InventoryRepository repo) {
		this.repo = repo;
	}

	@Override
	public Mono<Inventory> addInventory(Inventory inv) {
	    inv.availableSeats = inv.totalSeats;

	    inv.seatMap = new HashMap<>();
	    for (int i = 1; i <= inv.totalSeats; i++) {
	        String seat = "S" + i;   
	        inv.seatMap.put(seat, false);
	    }

	    return repo.save(inv);
	}
}
