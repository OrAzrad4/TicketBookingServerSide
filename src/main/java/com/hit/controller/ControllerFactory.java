package com.hit.controller;

import com.hit.service.SearchService;
import com.hit.service.TicketService;

public class ControllerFactory {

    private TicketController ticketController;

    public ControllerFactory(TicketService ticketService, SearchService searchService) {
        // Create the controller instance once
        this.ticketController = new TicketController(ticketService, searchService);
    }

    public TicketController getTicketController() {
        return ticketController;
    }
     //If in the future we will add more controllers so we add here
}