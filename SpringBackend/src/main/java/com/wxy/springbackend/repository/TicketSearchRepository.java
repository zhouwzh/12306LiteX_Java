package com.wxy.springbackend.repository;

import com.wxy.springbackend.model.TicketSearch;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TicketSearchRepository {
    private final JdbcTemplate jdbcTemplate;
    public TicketSearchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TicketSearch> findAllTicket(int userid){
        String sql = """
                SELECT    
                  t.ticket_id AS TicketID,
                  t.price AS TicketPrice,
                  t.date AS TicketDate,
                  t.seat_level AS SeatLevel,
                  i.invoice_id AS InvoiceID,
                  i.payment_state AS PaymentState,
                  i.valid_state AS ValidState,
                  sd.station_name AS DepartStation,
                  psd.start_time AS DepartTime,
                  sa.station_name AS ArrivalStation,
                  psa.start_time AS ArrivalTime,
                  tr.train_name AS TrainName
              FROM
                  wxy_user u
              JOIN
                  wxy_ticket t ON u.user_id = t.user_id
              LEFT JOIN
                  wxy_invoice i ON t.ticket_id = i.ticket_id
              JOIN
                  wxy_path_station psd ON t.path_id = psd.path_id AND DATE(psd.start_time) = t.date
              JOIN
                  wxy_station sd ON psd.station_id = sd.station_id AND t.depart_station = sd.station_id
              JOIN
                  wxy_path_station psa ON t.path_id = psa.path_id AND DATE(psa.start_time) = t.date
              JOIN
                  wxy_station sa ON psa.station_id = sa.station_id AND t.arrival_station = sa.station_id
              JOIN\s
                  wxy_path p ON psa.path_id = p.path_id
              JOIN
                  wxy_train tr ON p.train_id = tr.train_id
              WHERE
                  u.user_id = ?;
                """;

        return jdbcTemplate.query(sql, new Object[]{userid},
            (ResultSet rs) ->{
                Map<Integer, TicketSearch> map = new HashMap<>();

                while (rs.next()) {
                    int ticketId = rs.getInt("TicketID");
                    double price = rs.getDouble("TicketPrice");

                    String seatLevel = rs.getString("SeatLevel");
                    int invoiceId = rs.getInt("InvoiceID");
                    String paymentState = rs.getString("PaymentState");
                    String validState = rs.getString("ValidState");
                    String departStation = rs.getString("DepartStation");
                    String departTime = rs.getString("DepartTime");
                    String arrivalStation = rs.getString("ArrivalStation");
                    String arrivalTime = rs.getString("ArrivalTime");
                    String trainName = rs.getString("TrainName");

                    TicketSearch ticketSearch = new TicketSearch();
                    ticketSearch.setTicketId(ticketId);
                    ticketSearch.setPrice(price);
                    ticketSearch.setSeatLevel(seatLevel);
                    ticketSearch.setInvoiceId(invoiceId);
                    ticketSearch.setPaymentState(paymentState);
                    ticketSearch.setValidState(validState);
                    ticketSearch.setDepartStationName(departStation);
                    ticketSearch.setDepartureTime(departTime);
                    ticketSearch.setArrivalStationName(arrivalStation);
                    ticketSearch.setArrivalTime(arrivalTime);
                    ticketSearch.setTrainName(trainName);

                    map.put(ticketId, ticketSearch);
                }

                return new ArrayList<>(map.values());
            }
        );
    }
}
