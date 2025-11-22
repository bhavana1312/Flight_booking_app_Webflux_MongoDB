package com.flightapp.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.flightapp.model.Booking;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingRepository extends ReactiveMongoRepository<Booking, String> {
	Flux<Booking> findByEmail(String email);

	Mono<Booking> findByPnr(String pnr);
}
