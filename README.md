# Flight Booking App – WebFlux + MongoDB

## Overview

This project is a fully reactive **Flight Ticket Booking System** built using **Spring WebFlux** and **Reactive MongoDB**.  
It supports end‑to‑end user flow including:

- Flight Search
- Ticket Booking
- Seat Map Handling
- PNR Lookup
- Booking History
- Ticket Cancellation
- Airline & Flight Inventory Management
- Validation + Error Handling
- JUnit + WebFluxTest + Mockito test coverage
- Load testing using JMeter

---
## System Architecture

<img width="1785" height="637" alt="Screenshot 2025-11-23 125206" src="https://github.com/user-attachments/assets/8742b5bf-5406-46e8-8c93-fe66bca1759d" />

---

## Modules Implemented

### Airline Management

- Create airline (auto‑created if not existing during inventory add)

### Flight Inventory

- Add flight schedule
- Maintain available seats
- Maintain seat map (`S001 … S150`)
- Prevent duplicate airline + flight number combinations

### Booking System

- Book ticket
- Validate seats
- Prevent double booking
- Generate unique PNR
- Save passenger details
- Update seat map
- Reduce available seats

### Ticket Retrieval

- Fetch booking details by PNR

### Booking History

- Fetch bookings by email

### Ticket Cancellation

- Allowed only >= 24 hours before flight
- Marks booking as cancelled
- Frees seat numbers
- Increases available seats

---

# Final Outcome

A full‑featured **Reactive Flight Booking System** with:

- Validation & business logic
- Airline + inventory module
- Booking + cancellation
- JUnit + Jacoco + WebFlux tests
- JMeter tested
