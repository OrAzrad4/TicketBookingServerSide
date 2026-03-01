package com.hit.service;

import com.hit.algorithm.IAlgoLongestCommonSubsequence;
import com.hit.dao.IDao;
import com.hit.dm.Ticket;
import java.util.ArrayList;
import java.util.List;

public class SearchService {

    // hold's the algorithm interface
    private IAlgoLongestCommonSubsequence algo;
    private IDao<Long, Ticket> ticketDao;

    public SearchService(IAlgoLongestCommonSubsequence algo, IDao<Long, Ticket> ticketDao) {
        this.algo = algo;
        this.ticketDao = ticketDao;
    }

    public List<Ticket> findSimilarEvents(String searchName) {
        List<Ticket> results = new ArrayList<>();

        try {
            // Retrieve all tickets from file
            List<Ticket> allTickets = ticketDao.findAll();

            // If search query is empty, return the entire list --> connect to Client side to show all Tickets on first screen
            if (searchName == null || searchName.trim().isEmpty()) { // null if have mistake on json - protect from bug
                return allTickets;
            }

            // for loop on Tickets and skip unnamed tickets
            for (Ticket ticket : allTickets) {
                String eventName = ticket.getEventName();

                if (eventName == null) continue;


                // Calculate Length of Algorithm result and length of search
                int lcsLength = algo.calculateLCS(eventName, searchName);
                int searchLen = searchName.length();

                // If this is short word we want accurate matching
                if (searchLen <= 2) {
                    if (lcsLength == searchLen) {
                        results.add(ticket);
                    }
                }
                // If this is long word we can "forgive" 1 letter for effective search
                else {
                    if (lcsLength >= searchLen - 1) {
                        results.add(ticket);
                    }
                }
            }

            return results;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}