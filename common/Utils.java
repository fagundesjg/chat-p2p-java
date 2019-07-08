/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dinho
 */
public class Utils {
    public static boolean sendMessage(Socket sock, String message)
    {     
        try {
            ObjectOutputStream output = new ObjectOutputStream(sock.getOutputStream());
            output.flush();
            output.writeObject(message);
            return true;
        } catch (IOException ex) {
            System.err.println("[ERROR:sendMessage] -> " + ex.getMessage());
        }
        return false;
    }
    
    public static String receiveMessage(Socket sock)
    {
        String response = null;
        try {
            ObjectInputStream input = new ObjectInputStream(sock.getInputStream());
            response = (String) input.readObject();
        } catch (IOException ex) {
            System.err.println("[ERROR:receiveMessage] -> " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.err.println("[ERROR:receiveMessage] -> " + ex.getMessage());
        }
         return response;
    }
}
