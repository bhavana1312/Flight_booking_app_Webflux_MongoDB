package com.flightapp.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.PassengerDto;
import com.flightapp.model.Booking;
import com.flightapp.model.Inventory;
import com.flightapp.model.Passenger;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.InventoryRepository;
import com.flightapp.util.ValidationException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class BookingServiceImplTest {

	@Mock
	private BookingRepository bookingRepo;

	@Mock
	private InventoryRepository invRepo;

	@InjectMocks
	private BookingServiceImpl bookingService;

	private Inventory inv;
	private BookingRequest req;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);

		inv = new Inventory();
		inv.id = "FL1";
		inv.totalSeats = 10;
		inv.availableSeats = 10;
		inv.departure = LocalDateTime.now().plusDays(1);
		inv.price = 1000;
		inv.seatMap = new HashMap<>();
		for (int i = 1; i <= 10; i++)
			inv.seatMap.put("S" + i, false);

		req = new BookingRequest();
		req.name = "Bhavana";
		req.email = "bhavana@gmail.com";
		req.seats = 1;
		req.seatNumbers = List.of("S1");

		PassengerDto p = new PassengerDto();
		p.name = "Bhavana";
		p.gender = "Female";
		p.age = 21;
		p.seatNo = "S1";
		req.passengers = List.of(p);
	}

	@Test
	void book_success() {

		when(invRepo.findById("FL1")).thenReturn(Mono.just(inv));
		when(bookingRepo.save(any())).thenReturn(Mono.just(new Booking()));
		when(invRepo.save(any())).thenReturn(Mono.just(inv));

		StepVerifier.create(bookingService.book("FL1", req)).expectNextCount(1).verifyComplete();

		verify(invRepo).findById("FL1");
		verify(bookingRepo).save(any());
	}

	@Test
	void book_invalidSeat() {

		req.seatNumbers = List.of("S999");

		when(invRepo.findById("FL1")).thenReturn(Mono.just(inv));

		StepVerifier.create(bookingService.book("FL1", req)).expectError(ValidationException.class).verify();
	}

	@Test
	void book_notEnoughSeats() {

		inv.availableSeats = 0;

		when(invRepo.findById("FL1")).thenReturn(Mono.just(inv));

		StepVerifier.create(bookingService.book("FL1", req)).expectError().verify();
	}

	@Test
	void seatAlreadyBooked() {
		inv.seatMap.put("S1", true);

		when(invRepo.findById("FL1")).thenReturn(Mono.just(inv));

		StepVerifier.create(bookingService.book("FL1", req)).expectError().verify();
	}

	@Test
	void passengerCountMismatch() {
		req.seats = 2;

		when(invRepo.findById("FL1")).thenReturn(Mono.just(inv));

		StepVerifier.create(bookingService.book("FL1", req)).expectError().verify();
	}

	@Test
	void emptyPassengerList() {
		req.passengers = List.of();

		when(invRepo.findById("FL1")).thenReturn(Mono.just(inv));

		StepVerifier.create(bookingService.book("FL1", req)).expectError().verify();
	}

	@Test
	void cancelTicket_success() {

		Booking b = new Booking();
		b.pnr = "PNR123";
		b.flightId = "FL1";
		b.seatsBooked = 2;
		b.journeyDate = LocalDateTime.now().plusDays(2);
		b.cancelled = false;

		Passenger p = new Passenger();
		p.name = "X";
		p.age = 20;
		p.gender = "F";
		p.seatNo = "S1";

		Passenger p2 = new Passenger();
		p2.name = "Y";
		p2.age = 22;
		p2.gender = "M";
		p2.seatNo = "S2";

		b.passengers = List.of(p, p2);

		when(bookingRepo.findByPnr("PNR123")).thenReturn(Mono.just(b));
		when(invRepo.findById("FL1")).thenReturn(Mono.just(inv));
		when(invRepo.save(inv)).thenReturn(Mono.just(inv));
		when(bookingRepo.save(b)).thenReturn(Mono.just(b));

		StepVerifier.create(bookingService.cancelTicket("PNR123")).expectNext(b).verifyComplete();
	}

	@Test
	void cancelTicket_tooLate() {
		Booking b = new Booking();
		b.pnr = "PNR123";
		b.flightId = "FL1";
		b.journeyDate = LocalDateTime.now().plusHours(5);

		when(bookingRepo.findByPnr("PNR123")).thenReturn(Mono.just(b));

		StepVerifier.create(bookingService.cancelTicket("PNR123")).expectError().verify();
	}

	@Test
	void cancelTicket_notFound() {
		when(bookingRepo.findByPnr("X")).thenReturn(Mono.empty());

		StepVerifier.create(bookingService.cancelTicket("X")).expectError().verify();
	}
}
