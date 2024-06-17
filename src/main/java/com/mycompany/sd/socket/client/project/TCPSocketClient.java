/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sd.socket.client.project;

/**
 *
 * @author Pc
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPSocketClient {
    private String host;
    private int port;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private final GameClient gameClient;

    public TCPSocketClient(String host, int port, GameClient gameClient) {
        this.host = host;
        this.port = port;
        this.gameClient = gameClient;
    }
    
    public void connect() {
        try {
            socket = new Socket(host, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            new Thread(this::listenForMessages).start();
            //new ConnectionChecker(this).start();

            gameClient.updateStatus("Conectado al servidor.");
        } catch (IOException e) {
            gameClient.updateStatus("No se pudo conectar al servidor.");
            reconnect();
        }
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = input.readLine()) != null) {
                gameClient.handleServerMessage(message);
            }
        } catch (IOException e) {
            gameClient.updateStatus("Conexión perdida.");
            reconnect();
        }
    }
    
    public void sendPing() {
        output.println("PING hacia el servidor...");
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void reconnect() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        connect();
    }
    
    public String getHost() {
        return host;
    }
}
