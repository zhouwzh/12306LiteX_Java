package com.wxy.springbackend.repository;

import org.hibernate.query.Order;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class BookingRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getPathStationsForRange(int pathId, String departureStationName, String arrivalStationName, String startDate, String endDate) {
        String sql = """
        WITH DepartStation AS(
            SELECT ps.start_time AS depart_time
            FROM wxy_path_station ps
            JOIN wxy_station s ON ps.station_id = s.station_id
            WHERE s.station_name = ?
              AND ps.path_id = ?
              AND ps.start_time = ?
        ),
        ArrivalStation AS(
            SELECT ps.start_time AS arrival_time
            FROM wxy_path_station ps
            JOIN wxy_station s ON ps.station_id = s.station_id
            WHERE s.station_name = ?
              AND ps.path_id = ?
              AND ps.start_time = ?
        )
        SELECT
            s.station_id,
            s.station_name,
            ps.start_time,
            ps.a_seats_available,
            ps.b_seats_available,
            ps.c_seats_available
        FROM wxy_path_station ps
        JOIN wxy_station s ON ps.station_id = s.station_id
        WHERE ps.path_id = ?
          AND ps.start_time >= (SELECT depart_time FROM DepartStation)
          AND ps.start_time <= (SELECT arrival_time FROM ArrivalStation)
        ORDER BY ps.start_time;
        """;

        // Convert the string date parameters into Timestamps
        Timestamp startTimestamp = Timestamp.valueOf(startDate);
        Timestamp endTimestamp = Timestamp.valueOf(endDate);

        return jdbcTemplate.queryForList(sql, new Object[]{
                departureStationName, pathId, startTimestamp,
                arrivalStationName, pathId, endTimestamp,
                pathId
        });
    }
    public void decrementSeatsForRange(List<Map<String, Object>> stationsInRange, int pathId, String seatLevel) {
        String sql = """
        UPDATE wxy_path_station
        SET a_seats_available = CASE WHEN ? = 'A' THEN a_seats_available - 1 ELSE a_seats_available END,
            b_seats_available = CASE WHEN ? = 'B' THEN b_seats_available - 1 ELSE b_seats_available END,
            c_seats_available = CASE WHEN ? = 'C' THEN c_seats_available - 1 ELSE c_seats_available END
        WHERE path_id = ?
          AND station_id = ?
          AND start_time = ?
        """;
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Map<String, Object> station = stationsInRange.get(i);
                ps.setString(1, seatLevel);
                ps.setString(2, seatLevel);
                ps.setString(3, seatLevel);
                ps.setInt(4, pathId);
                ps.setInt(5, (Integer) station.get("station_id"));

                // Convert the station's start_time string to a Timestamp
                LocalDateTime startTime = (LocalDateTime) station.get("start_time");
                Timestamp startTimestamp = Timestamp.valueOf(startTime);
//                System.out.println(station.get("station_id"));
//                System.out.println(startTimestamp);
                ps.setTimestamp(6, startTimestamp);
            }

            @Override
            public int getBatchSize() {
                return stationsInRange.size();
            }
        });
    }
    public int createTicket(int userId, int pathId, int departStationId, int arrivalStationId, String seatLevel, LocalDate travelDate, BigDecimal price) {
        String insertTicketSql = """
            INSERT INTO wxy_ticket (price, seat_level, date, user_id, depart_station, arrival_station, path_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;



        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertTicketSql, Statement.RETURN_GENERATED_KEYS);
            ps.setBigDecimal(1, price);
            ps.setString(2, seatLevel);
            ps.setDate(3, Date.valueOf(travelDate));
            ps.setInt(4, userId);
            ps.setInt(5, departStationId);
            ps.setInt(6, arrivalStationId);
            ps.setInt(7, pathId);
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    /**
     * Creates a new invoice record associated with the given ticket_id.
     */
    public int createInvoice(int ticketId) {
        String insertInvoiceSql = """
            INSERT INTO wxy_invoice (payment_state, ticket_id, valid_state)
            VALUES (?, ?, ?)
        """;

        // Initially, set payment_state = 'false' and valid_state = 'pending'
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertInvoiceSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, "false");
            ps.setInt(2, ticketId);
            ps.setString(3, "pending");
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }
}

