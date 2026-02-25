package com.hit.controller;

import com.hit.dm.Ticket;
import com.hit.service.SearchService;
import com.hit.service.TicketService;
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

    //  Exposed API methods - now throwing exceptions upwards!

    public boolean saveTicket(Ticket ticket) throws Exception {
        return ticketService.addNewTicket(ticket);
    }

    public void deleteTicket(Ticket ticket) throws Exception {
        ticketService.removeTicket(ticket);
    }

    public Ticket getTicket(Long id) throws Exception {
        return ticketService.getTicket(id);
    }

    public List<Ticket> searchTickets(String query) throws Exception {
        return searchService.findSimilarEvents(query);
    }
}