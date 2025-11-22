package com.flightapp.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.flightapp.model.Inventory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface InventoryRepository extends ReactiveMongoRepository<Inventory, String> {
	Flux<Inventory> findByFromAndToAndDepartureBetween(String from, String to, LocalDateTime start, LocalDateTime end);

	Mono<Inventory> findByAirlineIdAndFlightNumber(String airlineId, String flightNumber);
}
