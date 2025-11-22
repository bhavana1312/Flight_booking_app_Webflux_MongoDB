package com.flightapp.service;

import com.flightapp.model.Inventory;

import reactor.core.publisher.Mono;

public interface InventoryService {
	Mono<Inventory> addInventory(Inventory inv);
}