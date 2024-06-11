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
    private LoginForm loginForm;

    public TCPSocketClient(String host, int port, LoginForm loginForm) {
        this.host = host;
        this.port = port;
        this.loginForm = loginForm;
    }

    public void connect() {
        try {
            socket = new Socket(host, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            new Thread(this::listenForMessages).start();
            new ConnectionChecker(this).start();

            loginForm.updateStatus("Conectado al servidor.");
        } catch (IOException e) {
            loginForm.updateStatus("No se pudo conectar al servidor.");
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
            loginForm.updateStatus("Conexión perdida.");
            reconnect();
        }
    }
    
    private void handleServerMessage(String message) {
        if (message.startsWith("LOGIN_SUCCESS")) {
            loginForm.updateStatus("Inicio de sesión exitoso.");
            MainForm mainForm = new MainForm();
            mainForm.setVisible(true);
            loginForm.dispose();
        } else if (message.startsWith("LOGIN_FAILURE")) {
            loginForm.updateStatus("Inicio de sesión fallido.");
        } else if (message.startsWith("REGISTER_SUCCESS")) {
            loginForm.updateStatus("Registro exitoso. Inicie sesión.");
            MainForm mainForm = new MainForm();
            mainForm.setVisible(true);
            loginForm.dispose();
        } else if (message.startsWith("REGISTER_FAILURE")) {
            loginForm.updateStatus("Registro fallido.");
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
