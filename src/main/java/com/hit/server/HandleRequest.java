package com.hit.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hit.controller.TicketController;
import com.hit.dm.Ticket;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles a single client connection.
 * Reads Request -> Invokes Controller -> Writes Response.
 */
public class HandleRequest implements Runnable {

    private Socket socket;
    private TicketController controller;
    private Gson gson;

    public HandleRequest(Socket socket, TicketController controller) {
        this.socket = socket;
        this.controller = controller;
        this.gson = new Gson();
    }

    @Override
    public void run() {
        // Using Reader and Writer
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            // Read the request
            String jsonRequest = reader.readLine();
            if (jsonRequest == null) return;

            // Convert from Json to generic request type
            Type requestType = new TypeToken<Request<Object>>(){}.getType();
            Request<Object> request = gson.fromJson(jsonRequest, requestType);

            // get the action from headers but dont know yet what the data in body(Ticket, ID ...)
            String action = request.getHeaders().get("action");

            // Convert the body again to Json and then know on switch case to which type need to convert
            String bodyJson = gson.toJson(request.getBody());

            Object responseBody = null;
            String status = "OK";

            try {
                // Navigate to the correct action
                switch (action) {
                    case "ticket/save": {
                        Ticket ticket = gson.fromJson(bodyJson, Ticket.class);
                        boolean success = controller.saveTicket(ticket);
                        responseBody = success ? "Success" : "Failed";
                        break;
                    }
                    case "ticket/delete": {
                        Ticket ticket = gson.fromJson(bodyJson, Ticket.class);
                        controller.deleteTicket(ticket);
                        responseBody = "Deleted";
                        break;
                    }
                    case "ticket/get": {
                        Map<String, Object> bodyMap = gson.fromJson(bodyJson, Map.class); // Convert to map beacuse body is not full object just id
                        Number idNum = (Number) bodyMap.get("id"); // Convert from deafault double to Number
                        responseBody = controller.getTicket(idNum.longValue()); // Update the response in long type correct parameter
                        break;
                    }
                    case "ticket/search": {
                        Map<String, String> bodyMap = gson.fromJson(bodyJson, Map.class); // Convert to map beacuse body is not full object its string
                        String query = bodyMap.get("searchQuery");  // Convert to string
                        responseBody = controller.searchTickets(query); // Update the response in string type correct parameter
                        break;
                    }
                    default:  // if not valid action
                        status = "Error";
                        responseBody = "Unknown Action";
                }
            } catch (Exception e) {
                status = "Error";   // status ok until invalid action
                responseBody = e.getMessage();
                e.printStackTrace();
            }

            // Build response Headers hashmap
            Map<String, String> responseHeaders = new HashMap<>();
            responseHeaders.put("action", action);
            responseHeaders.put("status", status);

            // Build all the response (have to hold Object because responseBody)
            Response<Object> response = new Response<>(responseHeaders, responseBody);

            // Send the response on string
            String jsonResponse = gson.toJson(response);
            writer.println(jsonResponse);

        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}