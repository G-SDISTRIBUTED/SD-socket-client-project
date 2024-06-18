/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.client.project;
import com.mycompany.paquete.*;
import com.mycompany.paquete.Paquete;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.ProcessBuilder.Redirect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        Gson gson=new Gson();
        StringBuilder jsonString = new StringBuilder();
        jsonString.append(message);
        Paquete paquete = gson.fromJson(jsonString.toString(), Paquete.class);
        String comando = paquete.getComando();
        if (null != comando) switch (comando) {
            case "LOGIN_SUCCESS":{
                form.updateStatus("Inicio de sesión exitoso.");
                form.sendMessage("SUCCESS");
                break;
            }
            case "LOGIN_FAILURE":{
                form.updateStatus("Inicio de sesión fallido.");
                break;
            }
            case "REGISTER_SUCCESS":{
                form.updateStatus("Registro exitoso. Inicie sesión.");
                form.sendMessage("SUCCESS");
                break;
            }
            case "REGISTER_FAILURE": {
                form.updateStatus("Registro fallido.");
                break;
            } 
            case "SENDING ROOMS": {
               String x= (String)paquete.getParams().getLast();
               
               form.actualizarRooms(stringToArray(x, Sala[].class));
               break; 
            }
            case "ROOM_CREATED": {
                Sala sala = (Sala) paquete.getSala();
                form.goRoom();
                form.setRoom(sala);
                break;
            }
            case "RECIVING REQUEST TO JOIN ROOM":{
                form.hadARequestFrom(paquete.getUsuario());
                break;
            }
            case "REQUEST SENT":{
                form.sendMessage("Esperando por aceptación...");
                break;
            }
            case "REQUEST FAILED": {
                form.sendMessage("Fallo la petición (talvez ya se cerro esa sala)");
                break;
            }
            case "JOIN ROOM ACCEPTED": {
                Sala sala = (Sala) paquete.getSala();
                form.goRoom();
                form.setRoom(sala);
                break;
            }
            case "SALA_JOINED":{
                String parametros = paquete.getStringParams();
                form.sendMessage(parametros);
                break;
            }
            default:
            break;
        }
    }
    
    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
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
            Paquete paquete = new Paquete(sala, "create room");
            Gson gson = new Gson();
            String mensaje = gson.toJson(paquete);
            sendMessage(mensaje);
        } else {
            updateStatus("No hay conexión con el servidor.");
        }
    }
    
    public void handleRequestJoinRoom(Integer tokenSala){
        if (isConnected()) {
//            Paquete paquete = new Paquete(sala, "request to join room");
            Paquete paquete = new Paquete();
            paquete.setComando("request to join room");
            paquete.addParam(tokenSala.toString());
            paquete.setUsuario(usuario);
            Gson gson = new Gson();
            String mensaje = gson.toJson(paquete);
            sendMessage(mensaje);
        } else {
            updateStatus("No hay conexión con el servidor.");
        }
    }
    
    public void handleAcceptRequestJoin(Integer tokenRoom, Usuario usuario){
        if (isConnected()) {
            Paquete paquete = new Paquete();
            paquete.setComando("join request accepted");
            paquete.addParam(tokenRoom.toString());
            paquete.setUsuario(usuario);
            Gson gson = new Gson();
            String mensaje = gson.toJson(paquete);
            sendMessage(mensaje);
        } else {
            updateStatus("No hay conexión con el servidor.");
        }
    }
    
    public void handleRejectRequestJoin(Integer tokenRoom, Usuario usuario){
        if (isConnected()) {
            Paquete paquete = new Paquete();
            paquete.setComando("join request rejected");
            paquete.addParam(tokenRoom);
            paquete.addParam(usuario);
            Gson gson = new Gson();
            String mensaje = gson.toJson(paquete);
            sendMessage(mensaje);
        } else {
            updateStatus("No hay conexión con el servidor.");
        }
    }
    
    public void handleGetRooms(){
        if (isConnected()) {
            Paquete paquete = new Paquete();
            paquete.setComando("get rooms");
            Gson gson = new Gson();
            String mensaje = gson.toJson(paquete);
            sendMessage(mensaje);
        } else {
            updateStatus("No hay conexión con el servidor.");
        }
    }
    
}
