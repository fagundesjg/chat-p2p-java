/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import common.Utils;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Server;

/**
 *
 * @author Dinho
 */
public class ClientListener implements Runnable {

    private boolean running;
    private Socket socket;
    private String nickname;
    private Server server;

    public ClientListener(String nickname, Socket socket, Server server) {
        this.server = server;
        running = false;
        this.socket = socket;
        this.nickname = nickname;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        running = true;
        String message;
        while (running) {
            message = Utils.receiveMessage(socket);
            if (message.toLowerCase().equals("quit")) {
                server.getClientes().remove(nickname);
                try {
                    socket.close();
                } catch (IOException ex) {
                    System.err.println("[ClientListener:Run] -> " + ex.getMessage());
                }
                running = false;
            } else if (message.equals("GET_CONNECTED_USERS")) {
                System.out.println("Solicitação de atualizar lista de contatos...");
                String response = "";
                for (Map.Entry<String, ClientListener> pair : server.getClientes().entrySet()) {
                    response += (pair.getKey() + ";");
                }
                Utils.sendMessage(socket, response);
            }
            System.out.println(" >> Mensagem: " + message);
        }
    }

}
