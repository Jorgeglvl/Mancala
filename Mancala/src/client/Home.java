package client;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.*;

import common.Utils;

public class Home extends JFrame {

    private ArrayList<String> connected_users;
    private String connection_info;
    private Socket connection;

    private JLabel jl_title;
    private JButton jb_get_connected, jb_challange;
    private JList jlist;
    private JScrollPane scroll;

    public Home(Socket connection, String connection_info) {
        super("Home");
        this.connection_info = connection_info;
        this.connection = connection;
        initComponents();
        configComponents();
        insertComponents();
        insertActions();
        start();
    }

    private void initComponents() {
        connected_users = new ArrayList<String>();
        jl_title = new JLabel("<Usuario: " + connection_info.split(":")[0] + ">", SwingConstants.CENTER);
        jb_get_connected = new JButton("Atualizar Lista");
        jb_challange = new JButton("Desafiar");
        jlist = new JList();
        scroll = new JScrollPane(jlist);
    }

    private void configComponents() {
        this.setLayout(null);
        this.setMinimumSize(new Dimension(600, 480));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.WHITE);

        jl_title.setBounds(5, 10, 370, 40);
        jl_title.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        jb_get_connected.setBounds(400, 10, 180, 40);
        jb_get_connected.setFocusable(false);

        jb_challange.setBounds(5, 400, 575, 40);
        jb_challange.setFocusable(false);

        jlist.setBorder(BorderFactory.createTitledBorder("Usuarios Online"));
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        scroll.setBounds(5, 60, 575, 335);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
    }

    private void insertComponents() {
        this.add(jl_title);
        this.add(jb_get_connected);
        this.add(jb_challange);
        this.add(scroll);
    }

    private void insertActions() {
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowClosing(WindowEvent e) {
                Utils.sendMessage(connection, "QUIT");
                System.out.println("> Conexão encerrada");
            }

            @Override
            public void windowClosed(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowIconified(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowActivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }
            
        });

        jb_get_connected.addActionListener(event -> getConnectedUsers());
    }

    private void start(){
        this.pack();
        this.setVisible(true);
    }

    private void getConnectedUsers(){
        Utils.sendMessage(connection, "GET_CONNECTED_USERS");
        String response = Utils.receiveMessage(connection);
        jlist.removeAll();
        connected_users.clear();
        for(String info : response.split(";")){
            if(!info.equals(connection_info)){
                connected_users.add(info);
            }
        }
        jlist.setListData(connected_users.toArray());
    }
}
