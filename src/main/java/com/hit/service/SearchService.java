package com.hit.service;


import com.hit.algorithm.IAlgoLongestCommonSubsequence;
import com.hit.dao.IDao;
import com.hit.dm.Ticket;
import java.util.ArrayList;
import java.util.List;

public class SearchService {

    // hold interfaces and not specific implementation, API only
    private IAlgoLongestCommonSubsequence algo;
    private IDao<Long, Ticket> ticketDao;

    public SearchService(IAlgoLongestCommonSubsequence algo, IDao<Long, Ticket> ticketDao) {
        this.algo = algo;
        this.ticketDao = ticketDao;
    }

    public List<Ticket> findSimilarEvents(String searchName) {
        List<Ticket> results = new ArrayList<>();

        try {
            // get all tickets with dao function findAll
            List<Ticket> allTickets = ticketDao.findAll();

            // implementation next part

            return allTickets;

        } catch (Exception e) {
            e.printStackTrace();
        return new ArrayList<>();
         }
    }
}