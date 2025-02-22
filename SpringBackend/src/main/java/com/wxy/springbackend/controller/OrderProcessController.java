package com.wxy.springbackend.controller;

import com.wxy.springbackend.service.OrderProcessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrderProcessController {
    private final OrderProcessService orderProcessService;
    public OrderProcessController(OrderProcessService orderProcessService) {
        this.orderProcessService = orderProcessService;
    }

    @PostMapping("/tickets/payment")
    public String payment(@RequestParam int ticketid){
        return orderProcessService.payment(ticketid);
    }

    @PostMapping("/ticket/cancel")
    public String cancel(@RequestParam int ticketid){
        return orderProcessService.cancel(ticketid);
    }
}
