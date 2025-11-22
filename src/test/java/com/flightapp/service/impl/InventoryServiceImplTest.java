package com.flightapp.service.impl;

import com.flightapp.dto.InventoryRequest;
import com.flightapp.model.Airline;
import com.flightapp.model.Inventory;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.InventoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

class InventoryServiceImplTest {

    @Mock
    private InventoryRepository invRepo;

    @Mock
    private AirlineRepository airlineRepo;

    @InjectMocks
    private InventoryServiceImpl service;

    private InventoryRequest req;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        req = new InventoryRequest();
        req.setAirlineName("Air India");
        req.setAirlineCode("AI");
        req.setAirlineLogo("logo.png");
        req.setFlightNumber("AI123");
        req.setFrom("DEL");
        req.setTo("MUM");
        req.setDeparture(LocalDateTime.now().plusDays(1));
        req.setArrival(req.getDeparture().plusHours(2));
        req.setTotalSeats(100);
        req.setPrice(5000);
    }

    @Test
    void addInventory_createsAirlineIfNotExists() {

        when(airlineRepo.findByCode("AI")).thenReturn(Mono.empty());

        Airline a = new Airline();
        a.setId("A1");
        a.setName("Air India");
        a.setCode("AI");
        a.setLogoUrl("logo.png");

        when(airlineRepo.save(any())).thenReturn(Mono.just(a));
        when(invRepo.findByAirlineIdAndFlightNumber("A1", "AI123")).thenReturn(Mono.empty());
        when(invRepo.save(any())).thenReturn(Mono.just(new Inventory()));

        StepVerifier.create(service.addInventory(req))
                .expectNextCount(1)
                .verifyComplete();
    }
}
