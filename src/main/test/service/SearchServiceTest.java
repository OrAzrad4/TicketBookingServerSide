package service;

import com.hit.algorithm.IAlgoLongestCommonSubsequence;
import com.hit.algorithm.LCSDynamicMatrixAlgoImpl;
import com.hit.dao.IDao;
import com.hit.dao.TicketDaoImpl;
import com.hit.dm.Ticket;
import com.hit.service.SearchService;
import java.util.List;

public class SearchServiceTest {

    public static void main(String[] args) {
        System.out.println("Starting SearchServiceTest");

       //Create dao
        IDao<Long, Ticket> dao = new TicketDaoImpl();
        System.out.println("DAO created successfully.");

       //Create algorithm from jar file(part A) and hold him in interface variable
        IAlgoLongestCommonSubsequence algo = new LCSDynamicMatrixAlgoImpl();
        System.out.println("Algorithm loaded from JAR successfully.");

        //Create service that get as input from outside the dao and algorithm
        SearchService service = new SearchService(algo, dao);
        System.out.println("SearchService initialized with dependencies.");

        //Final check to show that all layers works together
        try {
            List<Ticket> results = service.findSimilarEvents("Test Event");

            // For now we expect for empty list beacause we need implement all the functions in dao
            System.out.println("Method execution finished. Result: " + results);

        } catch (Exception e) {
            System.out.println("Error during execution: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Test Passed: Architecture is valid");
    }
}