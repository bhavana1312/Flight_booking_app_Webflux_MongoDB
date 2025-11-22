package com.flightapp.controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.flightapp.dto.SearchRequest;
import com.flightapp.dto.BookingRequest;
import com.flightapp.model.Inventory;
import com.flightapp.model.Booking;
import com.flightapp.repository.InventoryRepository;
import com.flightapp.service.BookingService;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/flight")
public class FlightController{
 private final InventoryRepository invRepo;
 private final BookingService bookingService;
 public FlightController(InventoryRepository invRepo,BookingService bookingService){this.invRepo=invRepo;this.bookingService=bookingService;}

 @PostMapping("/search")
 public Flux<Inventory> search(@RequestBody SearchRequest req){
  LocalDateTime start=req.date.atStartOfDay();
  LocalDateTime end=start.plusDays(1);
  return invRepo.findByFromAndToAndDepartureBetween(req.from,req.to,start,end);
 }

 @PostMapping("/booking/{flightid}")
 public Mono<Booking> book(@PathVariable("flightid") String flightid,@RequestBody BookingRequest req){
  return bookingService.book(flightid,req);
 }

 @GetMapping("/ticket/{pnr}")
 public Mono<Booking> getByPnr(@PathVariable("pnr") String pnr){
  return bookingService.findByPnr(pnr);
 }
}
