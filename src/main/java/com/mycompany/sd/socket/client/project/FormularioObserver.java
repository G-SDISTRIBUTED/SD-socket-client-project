/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.client.project;

import com.mycompany.paquete.Sala;
import com.mycompany.paquete.Usuario;
import java.util.List;

/**
 *
 * @author Hp
 */
abstract class FormularioObserver extends javax.swing.JFrame {
    public void updateStatus(String message){}
    public void sendMessage(String message){}    
    public void goRoom(){}
    public void hadARequestFrom(Usuario usuario){}
    void actualizarRooms(List<Sala> rooms) {}
    public void setRoom(Sala room){}
}
