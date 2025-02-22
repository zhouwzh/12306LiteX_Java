package com.wxy.springbackend.service;

import com.wxy.springbackend.model.TicketSearch;
import com.wxy.springbackend.repository.TicketSearchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketSearchService {
    private final TicketSearchRepository ticketSearchRepository;
    public TicketSearchService(TicketSearchRepository ticketSearchRepository) {
        this.ticketSearchRepository = ticketSearchRepository;
    }

    public List<TicketSearch> getAllTickets(int userid){
        return ticketSearchRepository.findAllTicket(userid);
    }
}
