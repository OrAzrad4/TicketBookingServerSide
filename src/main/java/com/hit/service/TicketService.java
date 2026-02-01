package com.hit.service;

import com.hit.dao.IDao;
import com.hit.dm.Ticket;
import java.io.IOException;

public class TicketService {

    // hold dao
    private IDao<Long, Ticket> ticketDao;

    // constructor get Dao from outside
    public TicketService(IDao<Long, Ticket> ticketDao) {
        this.ticketDao = ticketDao;
    }

    // Add new Ticket
    public boolean addNewTicket(Ticket ticket) throws IOException {
        return ticketDao.save(ticket);
    }

    // Get Ticket with id input
    public Ticket getTicket(Long id) throws IOException {
        return ticketDao.find(id);
    }

    // Delete Ticket
    public void removeTicket(Ticket ticket) throws IOException {
        ticketDao.delete(ticket);
    }
}