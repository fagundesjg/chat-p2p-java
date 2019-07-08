package server;

import common.Utils;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 4444;

    private ServerSocket server;
    private Map<String, ClientListener> clientes;

    public Server() {
        try {
            String request;
            clientes = new HashMap<String, ClientListener>();
            server = new ServerSocket(PORT);
            System.out.println("Servidor iniciado...");
            while (true) {
                Socket client = server.accept();
                request = Utils.receiveMessage(client);
                if (checkLogin(request)) {
                    ClientListener listener = new ClientListener(request, client, this);
                    clientes.put(request, listener);
                    Utils.sendMessage(client, "SUCESS");
                    new Thread(listener).start();
                }else {
                    Utils.sendMessage(client, "ERROR");
                }
            }
        } catch (IOException ex) {
            System.err.println("[ERROR:Server] -> " + ex.getMessage());
        }
    }

    public Map<String, ClientListener> getClientes() {
        return clientes;
    }

    private boolean checkLogin(String request) {
        String[] splited = request.split(":");
        for (Map.Entry<String, ClientListener> p : clientes.entrySet()) {
            String[] parts = p.getKey().split(":");
            if (parts[0].toLowerCase().equals(splited[0].toLowerCase())) {
                return false;
            } else if ((parts[1] + parts[2]).toLowerCase().equals((splited[1] + splited[2]).toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Server server = new Server();
    }
}
