/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.GUI;
import common.Utils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author Dinho
 */
public class Chat extends GUI {

    private JLabel jl_title;
    private JEditorPane messages;
    private JTextField jt_message;
    private JButton jb_message;
    private JPanel panel;
    private JScrollPane scroll;

    private ArrayList<String> message_list;
    private Home home;
    private Socket connection;
    private String connection_info;

    public Chat(Home home, Socket connection, String connection_info, String title) {
        super("Chat " + title);
        this.home = home;
        this.connection_info = connection_info;
        message_list = new ArrayList<String>();
        this.connection = connection;
        this.jl_title.setText(connection_info.split(":")[0]);
        this.jl_title.setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void initComponents() {
        jl_title = new JLabel();
        messages = new JEditorPane();
        scroll = new JScrollPane(messages);
        jt_message = new JTextField();
        jb_message = new JButton("Enviar");
        panel = new JPanel(new BorderLayout());
    }

    @Override
    protected void configComponents() {
        this.setMinimumSize(new Dimension(480, 720));
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        messages.setContentType("text/html");
        messages.setEditable(false);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jb_message.setSize(100, 40);
    }

    @Override
    protected void insertComponents() {
        this.add(jl_title, BorderLayout.NORTH);
        this.add(scroll, BorderLayout.CENTER);
        this.add(panel, BorderLayout.SOUTH);
        panel.add(jt_message, BorderLayout.CENTER);
        panel.add(jb_message, BorderLayout.EAST);
    }

    @Override
    protected void insertActions() {
        jt_message.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    send();
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

        });
        jb_message.addActionListener(event -> send());
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                Utils.sendMessage(connection, "CHAT_CLOSE");
                home.getOpened_chats().remove(connection_info);
                home.getConnected_listeners().get(connection_info).setChatOpen(false);
                home.getConnected_listeners().get(connection_info).setRunning(false);
                home.getConnected_listeners().remove(connection_info);
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }

        });
    }

    public void append_message(String received) {
        message_list.add(received);
        String message = "";
        for (String str : message_list) {
            message += str;
        }
        messages.setText(message);
    }

    @Override
    protected void start() {
        this.pack();
        this.setVisible(true);
    }

    private void send() {
        DateFormat df = new SimpleDateFormat("hh:mm:ss");
        message_list.add("<b>[" + df.format(new Date()) + "] Eu: </b><i>" + jt_message.getText() + "</i><br>");
        Utils.sendMessage(connection, "MESSAGE;<b>[" + df.format(new Date()) + "] " + home.getConnection_info().split(":")[0] + ": </b><i>" + jt_message.getText() + "</i><br>");
        String message = "";
        for (String str : message_list) {
            message += str;
        }
        messages.setText(message);
        jt_message.setText("");

    }

}
