package com.wxy.springbackend.repository;

import com.wxy.springbackend.model.OrderProcess;
import org.hibernate.query.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderProcessRepositroy {
    private final JdbcTemplate jdbcTemplate;

    public OrderProcessRepositroy(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String payment(int ticketid){
        String sql = """
                SELECT
                    invoice_id AS invoice_id,
                    payment_state AS PaymentState,
                    valid_state AS ValidState
                FROM
                    wxy_invoice
                WHERE
                    ticket_id = ?;
                """;
        OrderProcess order = jdbcTemplate.query(sql, new Object[]{ticketid},
            (ResultSet rs) ->{
                OrderProcess orderProcess = new OrderProcess();
                if(rs.next()){
                    orderProcess.setTicketId(ticketid);
                    orderProcess.setInvoiceId(rs.getInt("invoice_id"));
                    orderProcess.setPaymentState(rs.getString("PaymentState"));
                    orderProcess.setValidState(rs.getString("ValidState"));
                }
                return orderProcess;
            }
        );


        if(order.getValidState().equals("refund")){
            return "payment failure: ticket has been cancelled";
        }else if(order.getPaymentState().equals("true")){
            return "payment failure: already paid";
        }else{
            jdbcTemplate.update("""
                    UPDATE wxy_invoice
                    SET
                        payment_state = 'true',
                        valid_state = 'valid'
                    WHERE
                        invoice_id = ?;
                    """,
                    order.getInvoiceId());
            return "payment success!";
        }

    }

    public String Cancel(int ticketid){
        String sql = """
                SELECT
                    invoice_id AS invoice_id,
                    payment_state AS PaymentState,
                    valid_state AS ValidState
                FROM
                    wxy_invoice
                WHERE
                    ticket_id = ?;
                """;
        OrderProcess order = jdbcTemplate.query(sql, new Object[]{ticketid},
                (ResultSet rs) ->{
                    OrderProcess orderProcess = new OrderProcess();
                    if(rs.next()){
                        orderProcess.setTicketId(ticketid);
                        orderProcess.setInvoiceId(rs.getInt("invoice_id"));
                        orderProcess.setPaymentState(rs.getString("PaymentState"));
                        orderProcess.setValidState(rs.getString("ValidState"));
                    }
                    return orderProcess;
                }
        );

        if(order.getValidState().equals("refund") || order.getValidState().equals("cancelled")){
            return "cancel failure: ticket has been cancelled";
        }else{


            //add seat number back
            jdbcTemplate.update("""
                CREATE TEMPORARY TABLE TempStations AS
                SELECT
                    ps.path_id,
                    ps.station_id,
                    ps.start_time
                FROM
                    wxy_path_station ps
                WHERE
                    ps.path_id = (SELECT path_id FROM wxy_ticket WHERE ticket_id = ?)
                    AND DATE(ps.start_time) = (SELECT date FROM wxy_ticket WHERE ticket_id = ?)
                    AND ps.start_time >= (
                        SELECT start_time
                        FROM wxy_path_station
                        WHERE station_id = (SELECT depart_station FROM wxy_ticket WHERE ticket_id = ?)
                        AND path_id = (SELECT path_id FROM wxy_ticket WHERE ticket_id = ?)
                        AND DATE(start_time) = (SELECT date FROM wxy_ticket WHERE ticket_id = ?)
                    )
                    AND ps.start_time <= (
                        SELECT start_time
                        FROM wxy_path_station
                        WHERE station_id = (SELECT arrival_station FROM wxy_ticket WHERE ticket_id = ?)
                        AND path_id = (SELECT path_id FROM wxy_ticket WHERE ticket_id = ?)
                        AND DATE(start_time) = (SELECT date FROM wxy_ticket WHERE ticket_id = ?)
                    );
            """, ticketid, ticketid, ticketid, ticketid, ticketid, ticketid, ticketid, ticketid);
            jdbcTemplate.update("""
                UPDATE wxy_path_station ps
                JOIN TempStations ts
                ON
                    ps.station_id = ts.station_id
                    AND ps.path_id = ts.path_id
                    AND DATE(ps.start_time) = DATE(ts.start_time)
                SET
                    a_seats_available = CASE
                        WHEN (SELECT seat_level FROM wxy_ticket WHERE ticket_id = ?) = 'A' THEN ps.a_seats_available + 1
                        ELSE ps.a_seats_available
                    END,
                    b_seats_available = CASE
                        WHEN (SELECT seat_level FROM wxy_ticket WHERE ticket_id = ?) = 'B' THEN ps.b_seats_available + 1
                        ELSE ps.b_seats_available
                    END,
                    c_seats_available = CASE
                        WHEN (SELECT seat_level FROM wxy_ticket WHERE ticket_id = ?) = 'C' THEN ps.c_seats_available + 1
                        ELSE ps.c_seats_available
                    END;
            """, ticketid, ticketid, ticketid);
            jdbcTemplate.update("DROP TEMPORARY TABLE TempStations;");


            if(order.getPaymentState().equals("true")){
                jdbcTemplate.update("""
                    UPDATE wxy_invoice
                    SET
                        valid_state = 'refund'
                    WHERE
                        invoice_id = ?;
                    """,
                        order.getInvoiceId());
                return "refund success";
            }else{
                jdbcTemplate.update("""
                    UPDATE wxy_invoice
                    SET
                        valid_state = 'cancelled'
                    WHERE
                        invoice_id = ?;
                    """,
                        order.getInvoiceId());
                return "cancel success";
            }
        }

    }

}
