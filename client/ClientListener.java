/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.Utils;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dinho
 */
public class ClientListener implements Runnable {

    private boolean running;
    private Socket socket;
    private Home home;
    private boolean chatOpen;
    private String connection_info;
    private Chat chat;

    public ClientListener(Home home, Socket socket) {
        chatOpen = false;
        this.home = home;
        running = false;
        this.socket = socket;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isChatOpen() {
        return chatOpen;
    }

    public void setChatOpen(boolean chatOpen) {
        this.chatOpen = chatOpen;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @Override
    public void run() {
        running = true;
        String message;
        while (running) {
            message = Utils.receiveMessage(socket);
            if (message == null || message.equals("CHAT_CLOSE")) {
                if (chatOpen) {
                    home.getOpened_chats().remove(connection_info);
                    home.getConnected_listeners().remove(connection_info);
                    chatOpen = false;
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        System.err.println("[ClientListener:run] -> " + ex.getMessage());
                    }
                    chat.dispose();
                }
                running = false;
            } else {
                String[] fields = message.split(";");
                if (fields.length > 1) {
                    if (fields[0].equals("OPEN_CHAT")) {
                        String[] splited = fields[1].split(":");
                        connection_info = fields[1];
                        if (!chatOpen) {
                            home.getOpened_chats().add(connection_info);
                            home.getConnected_listeners().put(connection_info, this);
                            chatOpen = true;
                            chat = new Chat(home, socket, connection_info, home.getConnection_info().split(":")[0]);
                        }
                    } else if (fields[0].equals("MESSAGE")) {
                        chat.append_message(fields[1]);
                    }
                }
            }
            System.out.println(" >> Mensagem: " + message);
        }
    }
}
