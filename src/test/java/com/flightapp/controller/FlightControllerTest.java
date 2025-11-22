package com.flightapp.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.InventoryRequest;
import com.flightapp.dto.SearchRequest;
import com.flightapp.model.Booking;
import com.flightapp.model.Inventory;
import com.flightapp.repository.InventoryRepository;
import com.flightapp.service.BookingService;
import com.flightapp.service.InventoryService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = FlightController.class)
class FlightControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private InventoryRepository invRepo;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private InventoryService inventoryService;

    @Test
    void getTicketByPnr_success() {

        Booking b = new Booking();
        b.setPnr("PNR123");

        Mockito.when(bookingService.findByPnr("PNR123"))
               .thenReturn(Mono.just(b));

        webTestClient.get()
                .uri("/api/flight/ticket/PNR123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pnr").isEqualTo("PNR123");
    }

    @Test
    void bookTicket_success() {

        BookingRequest req = new BookingRequest();
        req.setName("Bhavana");
        req.setEmail("bhavana@gmail.com");
        req.setSeats(1);
        req.setPassengers(List.of());
        req.setSeatNumbers(List.of());

        Booking b = new Booking();
        b.setPnr("NEWPNR");

        Mockito.when(bookingService.book(Mockito.eq("FL1"), Mockito.any()))
               .thenReturn(Mono.just(b));

        webTestClient.post()
                .uri("/api/flight/booking/FL1")
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pnr").isEqualTo("NEWPNR");
    }

    @Test
    void cancelTicket_success() {

        Booking b = new Booking();
        b.setPnr("PNR777");
        b.setCancelled(true);

        Mockito.when(bookingService.cancelTicket("PNR777"))
               .thenReturn(Mono.just(b));

        webTestClient.delete()
                .uri("/api/flight/booking/cancel/PNR777")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pnr").isEqualTo("PNR777");
    }

    @Test
    void searchFlights_success() {

        SearchRequest req = new SearchRequest();
        req.setFrom("HYD");
        req.setTo("DEL");
        req.setDate(LocalDate.now());

        Inventory inv = new Inventory();
        inv.setId("I1");
        inv.setFrom("HYD");
        inv.setTo("DEL");
        inv.setDeparture(LocalDateTime.now().plusHours(2));
        inv.setPrice(5000);
        inv.setTotalSeats(100);
        inv.setAvailableSeats(90);

        Mockito.when(invRepo.findByFromAndToAndDepartureBetween(Mockito.eq("HYD"), Mockito.eq("DEL"),
                Mockito.any(), Mockito.any()))
                .thenReturn(Flux.just(inv));

        webTestClient.post()
                .uri("/api/flight/search")
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("I1");
    }

    @Test
    void addInventory_success() {

        InventoryRequest req = new InventoryRequest();
        req.setAirlineName("Air India");
        req.setAirlineCode("AI");
        req.setFlightNumber("AI101");
        req.setDeparture(LocalDateTime.now());
        req.setArrival(LocalDateTime.now().plusHours(2));
        req.setTotalSeats(120);
        req.setPrice(4000);

        Inventory saved = new Inventory();
        saved.setId("INV999");
        saved.setAirlineId("A1");
        saved.setFlightNumber("AI101");

        Mockito.when(inventoryService.addInventory(Mockito.any()))
               .thenReturn(Mono.just(saved));

        webTestClient.post()
                .uri("/api/flight/airline/inventory/add")
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("INV999");
    }

    @Test
    void bookingHistory_success() {

        Booking b = new Booking();
        b.setPnr("ABC123");

        Mockito.when(bookingService.history("bhavana@gmail.com"))
               .thenReturn(Flux.just(b));

        webTestClient.get()
                .uri("/api/flight/booking/history/bhavana@gmail.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].pnr").isEqualTo("ABC123");
    }
}
