package com.hit.server;


public class ServerDriver {

    public static void main(String[] args) {
        // Instantiate server on port 34567
        Server server = new Server(34567);

        // Run server in a new thread
        new Thread(server).start();
    }
}