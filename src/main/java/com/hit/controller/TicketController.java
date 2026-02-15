package com.hit.controller;

import com.hit.dm.Ticket;
import com.hit.service.SearchService;
import com.hit.service.TicketService;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller Layer.
 * Acts as a facade/bridge between the Networking layer (HandleRequest)
 * and the Business Logic layer (Services).
 */
public class TicketController {

    private TicketService ticketService;
    private SearchService searchService;

    public TicketController(TicketService ticketService, SearchService searchService) {
        this.ticketService = ticketService;
        this.searchService = searchService;
    }

    // --- Exposed API methods with Exception Handling ---

    public boolean saveTicket(Ticket ticket) {
        try {
            return ticketService.addNewTicket(ticket);
        } catch (Exception e) {
            // Log the error and return false indicating failure
            e.printStackTrace();
            return false;
        }
    }

    public void deleteTicket(Ticket ticket) {
        try {
            ticketService.removeTicket(ticket);
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
        }
    }

    public Ticket getTicket(Long id) {
        try {
            return ticketService.getTicket(id);
        } catch (Exception e) {
            // Log the error and return null if not found or error occurred
            e.printStackTrace();
            return null;
        }
    }

    public List<Ticket> searchTickets(String query) {
        try {
            return searchService.findSimilarEvents(query);
        } catch (Exception e) {
            // Log the error and return an empty list
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}