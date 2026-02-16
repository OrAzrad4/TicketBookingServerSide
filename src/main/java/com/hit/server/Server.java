package com.hit.server;

import com.hit.algorithm.IAlgoLongestCommonSubsequence;
import com.hit.algorithm.LCSDynamicMatrixAlgoImpl;
import com.hit.controller.ControllerFactory;
import com.hit.controller.TicketController;
import com.hit.dao.IDao;
import com.hit.dao.TicketDaoImpl;
import com.hit.dm.Ticket;
import com.hit.service.SearchService;
import com.hit.service.TicketService;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The main server class implementing Runnable - in the future we can do more things at the same time with run the server.
 * Responsible for initializing the layers and accepting clients.
 */
public class Server implements Runnable {

    private int port;
    private boolean serverUp = true;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        //  Initialize DAO
        IDao<Long, Ticket> dao = new TicketDaoImpl("src/main/resources/datasource.txt");

        // Initialize Algorithm (from JAR)
        IAlgoLongestCommonSubsequence algo = new LCSDynamicMatrixAlgoImpl();

        // Initialize Services (Business Logic)
        TicketService ticketService = new TicketService(dao);
        SearchService searchService = new SearchService(algo, dao);

        //  Initialize Controller
        ControllerFactory factory = new ControllerFactory(ticketService, searchService);
        TicketController controller = factory.getTicketController();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is starting on port " + port);

            while (serverUp) {
                // Wait for client connection
                Socket clientSocket = serverSocket.accept();

                // Handle request in a separate thread
                new Thread(new HandleRequest(clientSocket, controller)).start();
            }

        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}