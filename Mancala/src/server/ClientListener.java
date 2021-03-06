package server;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import common.Utils;

public class ClientListener implements Runnable {

    private String connection_info;
    private Socket connection;
    private Server server;
    private boolean running;

    public ClientListener(String connection_info, Socket connection, Server server) {
        this.connection_info = connection_info;
        this.connection = connection;
        this.server = server;
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRuning(boolean running) {
        this.running = running;
    }

    public void run() {
        running = true;
        String message;
        while (running) {
            message = Utils.receiveMessage(connection);
            if (message.equals("QUIT")) {
                server.getClients().remove(connection_info);
                try {
                    connection.close();
                } catch (IOException e) {
                    System.err.println("[ClientListener:Run] -> " + e.getMessage());
                }
                running = false; 
            } else if(message.equals("GET_CONNECTED_USERS")){
                System.out.println("Atualizando lista de contatos...");
                String response = "";
                for(Map.Entry<String, ClientListener> pair: server.getClients().entrySet()){
                    response += (pair.getKey() + ";");
                }
                Utils.sendMessage(connection, response);
            }else{
                System.out.println("Recebido: " + message);
            }            
        }
    }
}
