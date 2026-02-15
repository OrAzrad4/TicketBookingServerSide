package com.hit.dao;

import com.hit.dm.Ticket;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDaoImpl implements IDao<Long, Ticket> {

    private String fileName;

    // Default constructor
    // Sets a default path if none is provided
    public TicketDaoImpl() {
        this.fileName = "src/main/resources/datasource.txt";
    }

    // Constructor with parameter - used by the Server to set specific file path
    public TicketDaoImpl(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public synchronized boolean save(Ticket entity) throws IllegalArgumentException, IOException { //Synchronized for race condition
        List<Ticket> allTickets = findAll();
        boolean found = false;

        // Iterate to update existing ticket
        for (int i = 0; i < allTickets.size(); i++) {
            if (allTickets.get(i).getId().equals(entity.getId())) {
                allTickets.set(i, entity); // Update
                found = true;
                break;
            }
        }

        // If not found, add new ticket
        if (!found) {
            allTickets.add(entity);
        }

        // Write changes to file
        writeAll(allTickets);
        return true;
    }

    @Override
    public synchronized void delete(Ticket entity) throws IllegalArgumentException, IOException {
        List<Ticket> allTickets = findAll();
        boolean removed = false;

        // Find and remove the ticket
        for (int i = 0; i < allTickets.size(); i++) {
            if (allTickets.get(i).getId().equals(entity.getId())) {
                allTickets.remove(i);
                removed = true;
                break;
            }
        }

        if (removed) {
            writeAll(allTickets);
        } else {
            throw new IllegalArgumentException("Ticket with id " + entity.getId() + " not found.");
        }
    }

    @Override
    public synchronized Ticket find(Long id) throws IllegalArgumentException, IOException {
        List<Ticket> allTickets = findAll();

        // Search for ticket by ID
        for (Ticket ticket : allTickets) {
            if (ticket.getId().equals(id)) {
                return ticket;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")    // I am sure that this file contain only Tickets so I dont need warning from compiler
    @Override
    public synchronized List<Ticket> findAll() throws IOException {
        List<Ticket> tickets = new ArrayList<>();
        File file = new File(fileName);

        // If file doesn't exist, return empty list (don't crash)
        if (!file.exists()) {
            return tickets;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            Object obj = in.readObject();
            if (obj instanceof List) {
                tickets = (List<Ticket>) obj;
            }
        } catch (EOFException e) {
            // File is empty, valid state
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    // Helper method to rewrite the entire list to the file
    private void writeAll(List<Ticket> tickets) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(tickets);
        }
    }
}