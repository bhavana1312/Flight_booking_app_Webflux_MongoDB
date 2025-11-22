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
        inv.setId("FL1");
        inv.setTotalSeats(10);
        inv.setAvailableSeats(10);
        inv.setDeparture(LocalDateTime.now().plusDays(1));
        inv.setPrice(1000);

        HashMap<String, Boolean> map = new HashMap<>();
        for (int i = 1; i <= 10; i++) map.put("S" + i, false);
        inv.setSeatMap(map);

        req = new BookingRequest();
        req.setName("Bhavana");
        req.setEmail("bhavana@gmail.com");
        req.setSeats(1);
        req.setSeatNumbers(List.of("S1"));

        PassengerDto p = new PassengerDto();
        p.setName("Bhavana");
        p.setGender("Female");
        p.setAge(21);
        p.setSeatNo("S1");
        req.setPassengers(List.of(p));
    }

    @Test
    void book_success() {

        when(invRepo.findById("FL1")).thenReturn(Mono.just(inv));
        when(bookingRepo.save(any())).thenReturn(Mono.just(new Booking()));
        when(invRepo.save(any())).thenReturn(Mono.just(inv));

        StepVerifier.create(bookingService.book("FL1", req))
                .expectNextCount(1)
                .verifyComplete();

        verify(invRepo).findById("FL1");
        verify(bookingRepo).save(any());
    }

    @Test
    void book_invalidSeat() {

        req.setSeatNumbers(List.of("S999"));

        when(invRepo.findById("FL1")).thenReturn(Mono.just(inv));

        StepVerifier.create(bookingService.book("FL1", req))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void book_notEnoughSeats() {

        inv.setAvailableSeats(0);

        when(invRepo.findById("FL1")).thenReturn(Mono.just(inv));

        StepVerifier.create(bookingService.book("FL1", req))
                .expectError()
                .verify();
    }

    @Test
    void seatAlreadyBooked() {
        inv.getSeatMap().put("S1", true);

        when(invRepo.findById("FL1")).thenReturn(Mono.just(inv));

        StepVerifier.create(bookingService.book("FL1", req))
                .expectError()
                .verify();
    }

    @Test
    void passengerCountMismatch() {
        req.setSeats(2);

        when(invRepo.findById("FL1")).thenReturn(Mono.just(inv));

        StepVerifier.create(bookingService.book("FL1", req))
                .expectError()
                .verify();
    }

    @Test
    void emptyPassengerList() {
        req.setPassengers(List.of());

        when(invRepo.findById("FL1")).thenReturn(Mono.just(inv));

        StepVerifier.create(bookingService.book("FL1", req))
                .expectError()
                .verify();
    }

    @Test
    void cancelTicket_success() {

        Booking b = new Booking();
        b.setPnr("PNR123");
        b.setFlightId("FL1");
        b.setSeatsBooked(2);
        b.setJourneyDate(LocalDateTime.now().plusDays(2));
        b.setCancelled(false);

        Passenger p1 = new Passenger();
        p1.setName("X");
        p1.setGender("F");
        p1.setAge(20);
        p1.setSeatNo("S1");

        Passenger p2 = new Passenger();
        p2.setName("Y");
        p2.setGender("M");
        p2.setAge(22);
        p2.setSeatNo("S2");

        b.setPassengers(List.of(p1, p2));

        when(bookingRepo.findByPnr("PNR123")).thenReturn(Mono.just(b));
        when(invRepo.findById("FL1")).thenReturn(Mono.just(inv));
        when(invRepo.save(inv)).thenReturn(Mono.just(inv));
        when(bookingRepo.save(b)).thenReturn(Mono.just(b));

        StepVerifier.create(bookingService.cancelTicket("PNR123"))
                .expectNext(b)
                .verifyComplete();
    }

    @Test
    void cancelTicket_tooLate() {

        Booking b = new Booking();
        b.setPnr("PNR123");
        b.setFlightId("FL1");
        b.setJourneyDate(LocalDateTime.now().plusHours(5));

        when(bookingRepo.findByPnr("PNR123")).thenReturn(Mono.just(b));

        StepVerifier.create(bookingService.cancelTicket("PNR123"))
                .expectError()
                .verify();
    }

    @Test
    void cancelTicket_notFound() {
        when(bookingRepo.findByPnr("X")).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.cancelTicket("X"))
                .expectError()
                .verify();
    }
}
