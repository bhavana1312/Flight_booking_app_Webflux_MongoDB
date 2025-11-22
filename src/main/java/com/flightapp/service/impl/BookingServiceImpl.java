package com.flightapp.service.impl;

import org.springframework.stereotype.Service;
import com.flightapp.service.BookingService;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.InventoryRepository;
import com.flightapp.model.Booking;
import com.flightapp.model.Passenger;
import com.flightapp.dto.BookingRequest;
import com.flightapp.util.PnrGenerator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
	private final BookingRepository bookingRepo;
	private final InventoryRepository invRepo;

	public BookingServiceImpl(BookingRepository bookingRepo, InventoryRepository invRepo) {
		this.bookingRepo = bookingRepo;
		this.invRepo = invRepo;
	}

	@Override
	public Mono<Booking> book(String flightId, BookingRequest req) {
		return invRepo.findById(flightId).flatMap(inv -> {
			if (inv.availableSeats < req.seats)
				return Mono.error(new RuntimeException("not enough seats"));
			inv.availableSeats = inv.availableSeats - req.seats;
			return bookingRepo.save(makeBooking(inv, req)).flatMap(b -> invRepo.save(inv).thenReturn(b));
		});
	}

	private Booking makeBooking(com.flightapp.model.Inventory inv, BookingRequest req) {
		Booking b = new Booking();
		b.pnr = PnrGenerator.generate();
		b.flightId = inv.id;
		b.email = req.email;
		b.name = req.name;
		b.seatsBooked = req.seats;
		b.bookedAt = LocalDateTime.now();
		b.journeyDate = inv.departure;
		b.amount = inv.price * req.seats;
		b.cancelled = false;
		b.passengers = req.passengers.stream().map(pd -> {
			Passenger p = new Passenger();
			p.name = pd.name;
			p.age = pd.age;
			p.gender = pd.gender;
			p.seatNo = pd.seatNo;
			return p;
		}).collect(Collectors.toList());
		return b;
	}

	@Override
	public Mono<Booking> findByPnr(String pnr) {
		return bookingRepo.findByPnr(pnr);
	}

	@Override
	public Mono<Booking> cancelTicket(String pnr) {
		return bookingRepo.findByPnr(pnr).flatMap(b -> {
			if (b == null)
				return Mono.error(new RuntimeException("PNR not found"));

			LocalDateTime now = LocalDateTime.now();
			if (now.plusHours(24).isAfter(b.journeyDate)) {
				return Mono.error(new RuntimeException("Cancellation allowed only 24 hours before journey"));
			}

			b.cancelled = true;
			return bookingRepo.save(b);
		});
	}

	@Override
	public Flux<Booking> history(String email) {
		return bookingRepo.findByEmail(email);
	}

}
