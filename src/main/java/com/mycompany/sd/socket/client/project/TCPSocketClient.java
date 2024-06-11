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
    private FormularioObserver form;

    public TCPSocketClient(String host, int port, LoginForm form) {
        this.host = host;
        this.port = port;
        this.form = form;
    }

    public void changeForm(FormularioObserver form) {
        this.form = form;
    }
    
    public void connect() {
        try {
            socket = new Socket(host, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            new Thread(this::listenForMessages).start();
            new ConnectionChecker(this).start();

            form.updateStatus("Conectado al servidor.");
        } catch (IOException e) {
            form.updateStatus("No se pudo conectar al servidor.");
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
                handleServerMessage(message);
            }
        } catch (IOException e) {
            form.updateStatus("Conexión perdida.");
            reconnect();
        }
    }
    
    private void handleServerMessage(String message) {
        if (message.startsWith("LOGIN_SUCCESS")) {
            form.updateStatus("Inicio de sesión exitoso.");
            form.sendMessage("SUCCESS");
        } else if (message.startsWith("LOGIN_FAILURE")) {
            form.updateStatus("Inicio de sesión fallido.");
        } else if (message.startsWith("REGISTER_SUCCESS")) {
            form.updateStatus("Registro exitoso. Inicie sesión.");
            form.sendMessage("SUCCESS");
        } else if (message.startsWith("REGISTER_FAILURE")) {
            form.updateStatus("Registro fallido.");
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
