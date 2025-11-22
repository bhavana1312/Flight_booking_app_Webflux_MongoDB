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

		String validationError = validateBookingRequest(req);
		if (validationError != null)
			return Mono.error(new ValidationException(validationError));

		return invRepo.findById(flightId).switchIfEmpty(Mono.error(new ValidationException("Flight not found")))
				.flatMap(inv -> validateInventory(inv, req).then(Mono.fromSupplier(() -> {
					req.getSeatNumbers().forEach(seat -> inv.getSeatMap().put(seat, true));
					inv.setAvailableSeats(inv.getAvailableSeats() - req.getSeats());
					return makeBooking(inv, req);
				})).flatMap(booking -> invRepo.save(inv).then(bookingRepo.save(booking))));
	}

	private String validateBookingRequest(BookingRequest req) {

		if (req.getSeats() <= 0)
			return "Seat count must be positive";

		if (req.getPassengers() == null || req.getPassengers().isEmpty())
			return "Passengers list cannot be empty";

		if (req.getPassengers().size() != req.getSeats())
			return "Passengers count must match seat count";

		if (req.getSeatNumbers() == null || req.getSeatNumbers().isEmpty())
			return "Seat numbers cannot be empty";

		if (req.getSeatNumbers().size() != req.getSeats())
			return "Seat number count must match seat count";

		return null;
	}

	private Mono<Inventory> validateInventory(Inventory inv, BookingRequest req) {

		if (inv.getDeparture().isBefore(LocalDateTime.now()))
			return Mono.error(new ValidationException("Cannot book past flights"));

		if (inv.getAvailableSeats() < req.getSeats())
			return Mono.error(new ValidationException("Not enough seats"));

		for (String seat : req.getSeatNumbers()) {

			if (!inv.getSeatMap().containsKey(seat))
				return Mono.error(new ValidationException("Invalid seat number: " + seat));

			if (Boolean.TRUE.equals(inv.getSeatMap().get(seat)))
				return Mono.error(new ValidationException("Seat already booked: " + seat));
		}

		return Mono.just(inv);
	}

	private Booking makeBooking(Inventory inv, BookingRequest req) {

		Booking b = new Booking();
		b.setPnr(PnrGenerator.generate());
		b.setFlightId(inv.getId());
		b.setEmail(req.getEmail());
		b.setName(req.getName());
		b.setSeatsBooked(req.getSeats());
		b.setBookedAt(LocalDateTime.now());
		b.setJourneyDate(inv.getDeparture());
		b.setAmount(inv.getPrice() * req.getSeats());
		b.setCancelled(false);

		b.setPassengers(req.getPassengers().stream().map(pd -> {
			Passenger p = new Passenger();
			p.setName(pd.getName());
			p.setAge(pd.getAge());
			p.setGender(pd.getGender());
			p.setSeatNo(pd.getSeatNo());
			return p;
		}).toList());

		return b;
	}

	@Override
	public Mono<Booking> findByPnr(String pnr) {
		return bookingRepo.findByPnr(pnr);
	}

	@Override
	public Mono<Booking> cancelTicket(String pnr) {

		return bookingRepo.findByPnr(pnr).switchIfEmpty(Mono.error(new ValidationException("PNR not found")))
				.flatMap(b -> {

					if (b.isCancelled())
						return Mono.error(new ValidationException("Ticket already cancelled"));

					LocalDateTime now = LocalDateTime.now();
					if (now.plusHours(24).isAfter(b.getJourneyDate()))
						return Mono.error(new ValidationException("Cancellation allowed only 24 hours before journey"));

					return invRepo.findById(b.getFlightId())
							.switchIfEmpty(Mono.error(new ValidationException("Flight not found"))).flatMap(inv -> {

								if (b.getPassengers() != null && inv.getSeatMap() != null) {
									b.getPassengers().forEach(p -> inv.getSeatMap().put(p.getSeatNo(), false));
								}

								inv.setAvailableSeats(inv.getAvailableSeats() + b.getSeatsBooked());

								b.setCancelled(true);

								return invRepo.save(inv).then(bookingRepo.save(b));
							});
				});
	}

	@Override
	public Flux<Booking> history(String email) {
		return bookingRepo.findByEmail(email);
	}
}
