/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.client.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Hp
 */
public class GameClient {
    private FormularioObserver form;
    private TCPSocketClient tcpSocketClient;

    public GameClient(LoginForm form) {
        this.form = form;
        this.tcpSocketClient = new TCPSocketClient("localhost", 12345,this);
    }

    public void changeForm(FormularioObserver form) {
        this.form = form;
    }
      
    public void connect() {
        tcpSocketClient.connect();
    }

    public void sendMessage(String message) {
        tcpSocketClient.sendMessage(message);
    }

    public boolean isConnected() {
        return tcpSocketClient.isConnected();
    }

    public String getHost() {
        return tcpSocketClient.getHost();
    }
    
    public void updateStatus(String message){
        form.updateStatus(message);
    }
    
    public void handleServerMessage(String message) {
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

}
