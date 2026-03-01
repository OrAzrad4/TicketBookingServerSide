package com.hit.dao;

import com.hit.dm.Ticket;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<Long, Ticket> map = findAllAsMap();
        // put handles Add new ticket and Update existing ticket automatically because the hashmap
        map.put(entity.getId(), entity);
        writeAll(map);
        return true;
    }

    @Override
    public synchronized void delete(Ticket entity) throws IllegalArgumentException, IOException {
        Map<Long, Ticket> map = findAllAsMap();

        // Find and remove the ticket. If remove returns null, the ID didn't exist.
        if (map.remove(entity.getId()) != null) {
            writeAll(map);
        } else {
            throw new IllegalArgumentException("Ticket with id " + entity.getId() + " not found."); // Cant delete unknown Ticket
        }


    }

    @Override
    public synchronized Ticket find(Long id) throws IllegalArgumentException, IOException {
        return findAllAsMap().get(id); // O(1) because hashmap
    }


    @Override                         // This function convert the data from map to list for SearchService use LCS
    public synchronized List<Ticket> findAll() throws IOException {
        Map<Long, Ticket> map = findAllAsMap();
        return new ArrayList<>(map.values());
    }


      // Helper method to bring all data as map
    private Map<Long, Ticket> findAllAsMap() throws IOException {
        File file = new File(fileName);

        // If file doesn't exist or is empty, return empty map (don't crash)
        if (!file.exists() || file.length() == 0) {
            return new HashMap<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Map<Long, Ticket>) in.readObject();
        } catch (Exception e) {
            return new HashMap<>();   // dont crash just make a new map
        }
    }


    // Helper method to rewrite the entire map to the file
    private void writeAll(Map<Long, Ticket> map) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(map);
        }
    }
}