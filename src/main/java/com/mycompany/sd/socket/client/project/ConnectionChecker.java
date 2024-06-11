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

import java.net.InetAddress;
import java.io.IOException;

public class ConnectionChecker extends Thread {
    private TCPSocketClient tcpSocketClient;
    private String host;

    public ConnectionChecker(TCPSocketClient tcpSocketClient) {
        this.tcpSocketClient = tcpSocketClient;
        this.host = tcpSocketClient.getHost();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(3000);
                InetAddress inetAddress = InetAddress.getByName(host);
                if (inetAddress.isReachable(2000)) {
                    tcpSocketClient.sendPing();
                } else {
                    tcpSocketClient.reconnect();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                tcpSocketClient.reconnect();
            }
        }
    }
}
