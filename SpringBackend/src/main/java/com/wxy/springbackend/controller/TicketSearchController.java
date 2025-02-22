package com.wxy.springbackend.controller;

import com.wxy.springbackend.model.TicketSearch;
import com.wxy.springbackend.model.TripSearch;
import com.wxy.springbackend.service.TicketSearchService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TicketSearchController {
    private final TicketSearchService ticketSearchService;
    public TicketSearchController(TicketSearchService ticketSearchService) {
        this.ticketSearchService = ticketSearchService;
    }

    @PostMapping("/user/tickets")
    public ResponseEntity<List<TicketSearch>> getAllTickets(@RequestParam int userid){
        List<TicketSearch> tickets =  ticketSearchService.getAllTickets(userid);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }
}
