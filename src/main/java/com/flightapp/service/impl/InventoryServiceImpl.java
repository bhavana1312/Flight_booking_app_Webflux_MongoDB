package com.flightapp.service.impl;

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
		return repo.save(inv);
	}
}
