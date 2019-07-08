package client;

import common.GUI;
import common.Utils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import server.Server;

/**
 *
 * @author Dinho
 */
public class Home extends GUI {

    private JLabel title;
    private ServerSocket server;
    private final Socket connection;
    private final String connection_info;
    private JButton jb_get_connected, jb_start_talk;
    private JList jlist;
    private JScrollPane scroll;

    private ArrayList<String> connected_users;
    private ArrayList<String> opened_chats;
    private Map<String, ClientListener> connected_listeners;

    public Home(Socket connection, String connection_info) {
        super("Chat - Home");
        title.setText("< Usuário : " + connection_info.split(":")[0] + " >");
        this.connection = connection;
        this.setTitle("Home - " + connection_info.split(":")[0]);
        this.connection_info = connection_info;
        connected_users = new ArrayList<String>();
        opened_chats = new ArrayList<String>();
        connected_listeners = new HashMap<String, ClientListener>();
        startServer(this, Integer.parseInt(connection_info.split(":")[2]));
    }

    @Override
    protected void initComponents() {
        title = new JLabel();
        jb_get_connected = new JButton("Atualizar contatos");
        jlist = new JList();
        scroll = new JScrollPane(jlist);
        jb_start_talk = new JButton("Abrir Conversa");
    }

    @Override
    protected void configComponents() {
        this.setLayout(null);
        this.setMinimumSize(new Dimension(600, 480));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.WHITE);

        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(10, 10, 370, 40);
        title.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        jb_get_connected.setBounds(400, 10, 180, 40);
        jb_get_connected.setFocusable(false);

        jb_start_talk.setBounds(10, 400, 575, 40);
        jb_start_talk.setFocusable(false);

        jlist.setBorder(BorderFactory.createTitledBorder("Usuários online"));
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        scroll.setBounds(10, 60, 575, 335);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
    }

    @Override
    protected void insertComponents() {
        this.add(title);
        this.add(jb_get_connected);
        this.add(scroll);
        this.add(jb_start_talk);
    }

    @Override
    protected void insertActions() {
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Conexão encerrada...");
                Utils.sendMessage(connection, "QUIT");
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
        jb_get_connected.addActionListener(event -> getConnectedUsers());
        jb_start_talk.addActionListener(event -> openChat());
    }

    @Override
    protected void start() {
        this.pack();
        this.setVisible(true);
    }

    private void getConnectedUsers() {
        Utils.sendMessage(connection, "GET_CONNECTED_USERS");
        String response = Utils.receiveMessage(connection);
        jlist.removeAll();
        connected_users.clear();
        for (String user : response.split(";")) {
            if (!user.equals(connection_info)) {
                connected_users.add(user);
            }

        }
        jlist.setListData(connected_users.toArray());
    }

    private void openChat() {
        int index = jlist.getSelectedIndex();
        if (index != -1) {
            String value = jlist.getSelectedValue().toString();
            String[] splited = value.split(":");
            if (!opened_chats.contains(value)) {
                try {
                    Socket socket = new Socket(splited[1], Integer.parseInt(splited[2]));
                    Utils.sendMessage(socket, "OPEN_CHAT;" + connection_info); // manda a mensagem para o outro lado da conversa abrir minha janela
                    ClientListener cl = new ClientListener(this, socket);
                    cl.setChat(new Chat(this, socket, value, this.connection_info.split(":")[0]));
                    cl.setChatOpen(true);
                    connected_listeners.put(value, cl);
                    opened_chats.add(value);
                    new Thread(cl).start();

                } catch (IOException ex) {
                }
            }

        }
    }

    private void startServer(Home home, int port) {
        new Thread() {
            @Override
            public void run() {
                try {
                    server = new ServerSocket(port);
                    System.out.println("Servidor cliente iniciado na porta " + port + " ...");
                    while (true) {
                        Socket client = server.accept();
                        ClientListener cl = new ClientListener(home, client);
                        new Thread(cl).start();
                    }
                } catch (IOException ex) {
                    System.err.println("[ERROR:startServer] -> " + ex.getMessage());
                }
            }
        }.start();
    }
    
    

    public ArrayList<String> getOpened_chats() {
        return opened_chats;
    }

    public String getConnection_info() {
        return connection_info;
    }

    public Map<String, ClientListener> getConnected_listeners() {
        return connected_listeners;
    }

}
