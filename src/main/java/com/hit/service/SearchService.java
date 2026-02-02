package com.hit.service;

import com.hit.algorithm.IAlgoLongestCommonSubsequence;
import com.hit.dao.IDao;
import com.hit.dm.Ticket;
import java.util.ArrayList;
import java.util.List;

public class SearchService {

    // מחזיק את הממשק של האלגוריתם (LCS)
    private IAlgoLongestCommonSubsequence algo;
    private IDao<Long, Ticket> ticketDao;

    public SearchService(IAlgoLongestCommonSubsequence algo, IDao<Long, Ticket> ticketDao) {
        this.algo = algo;
        this.ticketDao = ticketDao;
    }

    public List<Ticket> findSimilarEvents(String searchName) {
        List<Ticket> results = new ArrayList<>();

        try {
            // 1. שליפת כל הכרטיסים מהקובץ
            List<Ticket> allTickets = ticketDao.findAll();

            // אם החיפוש ריק, החזר את כל הרשימה
            if (searchName == null || searchName.trim().isEmpty()) {
                return allTickets;
            }

            // 2. מעבר על הכרטיסים וסינון בעזרת האלגוריתם
            for (Ticket ticket : allTickets) {
                String eventName = ticket.getEventName();

                // בדיקת תקינות (למנוע קריסה על null)
                if (eventName == null) continue;

                // הפעלת האלגוריתם: חישוב אורך תת-המחרוזת המשותפת
                int lcsLength = algo.calculateLCS(eventName, searchName);

                // התנאי: אם אורך הרצף המשותף שווה לאורך מילת החיפוש,
                // זה אומר שכל האותיות בחיפוש קיימים בשם האירוע (לפי הסדר)
                if (lcsLength == searchName.length()) {
                    results.add(ticket);
                }
            }

            return results;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}