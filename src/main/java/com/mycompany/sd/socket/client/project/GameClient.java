/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.client.project;
import com.mycompany.paquete.*;
import com.mycompany.paquete.Paquete;
import com.google.gson.Gson;
/**
 *
 * @author Hp
 */
public class GameClient {
    private FormularioObserver form;
    private TCPSocketClient tcpSocketClient;
    private Usuario usuario;
    
    public GameClient(LoginForm form) {
        this.form = form;
        this.tcpSocketClient = new TCPSocketClient("localhost", 12345, this);
    }

    public void changeForm(FormularioObserver form) {
        this.form = form;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
        }else if (message.startsWith("SALA_CREATED")){
            form.sendMessage("SUCCESS");
        }
    }
    
    public void handleLogin(String username,String password) {
        if (isConnected()) {
            usuario = new Usuario(username, password);
            Paquete paquete = new Paquete(usuario, "login");
            Gson gson = new Gson();
            String jsonString = gson.toJson(paquete);
            sendMessage(jsonString);
        } else {
            updateStatus("No hay conexión con el servidor.");
        }
    }
    
    public void handleRegister(String username,String password) {
        if (isConnected()) {
            usuario = new Usuario(username, password);
            Paquete paquete = new Paquete(usuario, "register");
            
            Gson gson = new Gson();
            String loginMessage = gson.toJson(paquete);
            System.out.println("message: "+loginMessage);
            sendMessage(loginMessage);
        } else {
            updateStatus("No hay conexión con el servidor.");
        }
    }
    
    public void handleCreateSala(String name){
        if (isConnected()) {
            Sala sala = new Sala(name, usuario);
            Paquete paquete = new Paquete(sala, "create sala");
            
            Gson gson = new Gson();
            String mensaje = gson.toJson(paquete);
            sendMessage(mensaje);
        } else {
            updateStatus("No hay conexión con el servidor.");
        }
    }
    
    public void handleJoinSala(){
        
    }
}
