package com.wxy.springbackend.service;

import com.wxy.springbackend.model.BookingResponse;
import com.wxy.springbackend.repository.BookingRepository;
import com.wxy.springbackend.model.Booking;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ReentrantLock lock = new ReentrantLock();

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public BookingResponse bookTicket(Booking booking) {
        int userId = booking.getUserId();
        int pathId = booking.getPathId();
        String departureStationName = booking.getDepartureStationName();
        String arrivalStationName = booking.getArrivalStationName();
        String departureTime = booking.getDepartureTime();
        String arrivalTime = booking.getArrivalTime();
        String seatLevel = booking.getSeatLevel();
        BigDecimal price = booking.getPrice();
        // Fetch the stations in range
        // Get all remain tickets (query)

        List<Map<String, Object>> stationsInRange;
        lock.lock();
        try {
            stationsInRange = bookingRepository.getPathStationsForRange(
                    pathId, departureStationName, arrivalStationName, departureTime, arrivalTime
            );

            // Check seat availability
            for (Map<String, Object> stationData : stationsInRange) {
                int availableSeats;
                switch (seatLevel.toUpperCase()) {
                    case "A":
                        availableSeats = (int) stationData.get("a_seats_available");
                        break;
                    case "B":
                        availableSeats = (int) stationData.get("b_seats_available");
                        break;
                    case "C":
                        availableSeats = (int) stationData.get("c_seats_available");
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown seat level: " + seatLevel);
                }

                if (availableSeats <= 0) {
                    throw new RuntimeException("Not enough seats available for the entire trip at level " + seatLevel);
                }
            }

            // Decrement seats for the entire range
            // Modify
            bookingRepository.decrementSeatsForRange(stationsInRange, pathId, seatLevel);

        } finally {
            lock.unlock();
        }

        int departStationId = (int) stationsInRange.get(0).get("station_id");
        int arrivalStationId = (int) stationsInRange.get(stationsInRange.size() - 1).get("station_id");

        // Parse the date (YYYY-MM-DD from the departureTime)
        LocalDate travelDate = LocalDate.parse(departureTime.substring(0, 10));

        // Create the ticket
        int ticketId = bookingRepository.createTicket(
                userId,
                pathId,
                departStationId,
                arrivalStationId,
                seatLevel,
                travelDate,
                price
        );

        // Create the invoice
        int invoiceId = bookingRepository.createInvoice(ticketId);

        // If no exception thrown until here, operation succeeded
        return new BookingResponse(invoiceId, ticketId);
    }
}
