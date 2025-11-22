package com.flightapp.service;

import com.flightapp.dto.BookingRequest;
import com.flightapp.model.Booking;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingService {
	Mono<Booking> book(String flightId, BookingRequest req);

	Mono<Booking> findByPnr(String pnr);

	Mono<Booking> cancelTicket(String pnr);

	Flux<Booking> history(String email);
}
