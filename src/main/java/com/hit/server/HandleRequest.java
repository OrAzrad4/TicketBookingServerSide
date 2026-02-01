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
import java.util.Scanner;

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
        try (Scanner reader = new Scanner(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            // 1. Read JSON Request
            String jsonRequest = reader.nextLine();

            // 2. Parse into Generic Request<Object>
            // We use generic Object because body type is unknown yet (Map, Ticket, etc.)
            Type requestType = new TypeToken<Request<Object>>(){}.getType();
            Request<Object> request = gson.fromJson(jsonRequest, requestType);

            // 3. Extract Headers
            String action = request.getHeaders().get("action");

            // 4. Convert Body to JSON string for specific parsing inside switch
            String bodyJson = gson.toJson(request.getBody());

            // Prepare Response parts
            Object responseBody = null;
            String status = "OK";

            // 5. Route Action to Controller
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
                    // Extract ID from map (Gson parses numbers as Doubles)
                    Map<String, Object> bodyMap = gson.fromJson(bodyJson, Map.class);
                    Number idNum = (Number) bodyMap.get("id");
                    responseBody = controller.getTicket(idNum.longValue());
                    break;
                }
                case "ticket/search": {
                    // Extract search query
                    Map<String, String> bodyMap = gson.fromJson(bodyJson, Map.class);
                    String query = bodyMap.get("searchQuery");
                    responseBody = controller.searchTickets(query);
                    break;
                }
                default:
                    status = "Error";
                    responseBody = "Unknown Action";
            }

            // 6. Construct Response Object
            Map<String, String> responseHeaders = new HashMap<>();
            responseHeaders.put("action", action);
            responseHeaders.put("status", status);

            Response<Object> response = new Response<>(responseHeaders, responseBody);

            // 7. Send JSON Response back
            String jsonResponse = gson.toJson(response);
            writer.println(jsonResponse);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}