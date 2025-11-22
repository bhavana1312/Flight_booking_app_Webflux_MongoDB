package com.flightapp.service.impl;

import org.springframework.stereotype.Service;

import com.flightapp.service.BookingService;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.InventoryRepository;
import com.flightapp.model.Booking;
import com.flightapp.model.Inventory;
import com.flightapp.model.Passenger;
import com.flightapp.dto.BookingRequest;
import com.flightapp.util.ValidationException;
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

		if (req.seats <= 0)
			return Mono.error(new ValidationException("Seat count must be positive"));

		if (req.passengers == null || req.passengers.isEmpty())
			return Mono.error(new ValidationException("Passengers list cannot be empty"));

		if (req.passengers.size() != req.seats)
			return Mono.error(new ValidationException("Passengers count must match seat count"));

		if (req.seatNumbers == null || req.seatNumbers.isEmpty())
			return Mono.error(new ValidationException("Seat numbers cannot be empty"));

		if (req.seatNumbers.size() != req.seats)
			return Mono.error(new ValidationException("Seat number count must match seat count"));

		return invRepo.findById(flightId).flatMap(inv -> {

			if (inv.departure.isBefore(LocalDateTime.now()))
				return Mono.error(new ValidationException("Cannot book past flights"));

			if (inv.availableSeats < req.seats)
				return Mono.error(new ValidationException("Not enough seats"));

			for (String seat : req.seatNumbers) {
				if (!inv.seatMap.containsKey(seat))
					return Mono.error(new ValidationException("Invalid seat number: " + seat));
			}

			for (String seat : req.seatNumbers) {
				if (inv.seatMap.get(seat))
					return Mono.error(new ValidationException("Seat already booked: " + seat));
			}

			req.seatNumbers.forEach(seat -> inv.seatMap.put(seat, true));

			inv.availableSeats -= req.seats;

			Booking booking = makeBooking(inv, req);

			return invRepo.save(inv).then(bookingRepo.save(booking));
		});
	}

	private Booking makeBooking(Inventory inv, BookingRequest req) {

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
				return Mono.error(new ValidationException("PNR not found"));

			if (b.cancelled)
				return Mono.error(new ValidationException("Ticket already cancelled"));

			LocalDateTime now = LocalDateTime.now();
			if (now.plusHours(24).isAfter(b.journeyDate))
				return Mono.error(new ValidationException("Cancellation allowed only 24 hours before journey"));

			return invRepo.findById(b.flightId).flatMap(inv -> {

				b.passengers.forEach(p -> inv.seatMap.put(p.seatNo, false));

				inv.availableSeats += b.seatsBooked;

				b.cancelled = true;

				return invRepo.save(inv).then(bookingRepo.save(b));
			});
		});
	}

	@Override
	public Flux<Booking> history(String email) {
		return bookingRepo.findByEmail(email);
	}
}
