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

# REST API Endpoints

---

## 1. Add Inventory (Admin)

### **POST**

`http://localhost:8080/api/flight/airline/inventory/add`

### Request Body

{
"airlineName": "Air India",
"airlineCode": "AI",
"airlineLogo": "logo.png",
"flightNumber": "AI123",
"from": "HYD",
"to": "DEL",
"departure": "2025-12-01T10:00:00",
"arrival": "2025-12-01T12:00:00",
"totalSeats": 150,
"price": 4200
}

---

## 2️. Search Flights

### **POST**

`http://localhost:8080/api/flight/search`

### Request Body

{
"from": "HYD",
"to": "DEL",
"date": "2025-12-01"
}

---

## 3️. Book Flight

### **POST**

`http://localhost:8080/api/flight/booking/{flightId}`  
Example:  
`http://localhost:8080/api/flight/booking/6921efee743d6f6a27890955`

### Request Body

{
"name": "bhavana",
"email": "bhavana@gmail.com",
"seats": 2,
"meal": "Veg",
"seatNumbers": ["S12","S13"],
"passengers": [
{ "name": "bhavana", "gender": "Female", "age": 21, "seatNo": "S12" },
{ "name": "siddharth", "gender": "Male", "age": 22, "seatNo": "S13" }
]
}

---

## 4️. Get Ticket by PNR

### **GET**

`http://localhost:8080/api/flight/ticket/587AD95532`

---

## 5️. Booking History

### **GET**

`http://localhost:8080/api/flight/booking/history/bhavana@gmail.com`

---

## 6️. Cancel Ticket

### **DELETE**

`http://localhost:8080/api/flight/booking/cancel/587AD95532`

---

# Final Outcome

A full‑featured **Reactive Flight Booking System** with:

- Validation & business logic
- Airline + inventory module
- Booking + cancellation
- JUnit + Jacoco + WebFlux tests
- JMeter tested
