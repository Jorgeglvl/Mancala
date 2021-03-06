package client;

import java.io.IOException;
import java.net.Socket;

import javax.print.DocFlavor.STRING;

import common.Utils;

public class ClientListener implements Runnable {

    private boolean running;
    private boolean isOpened;
    private Home home;
    private Socket connection;
    private String connection_info;
    private Chat chat;
    private Game game;

    public ClientListener(Home home, Socket connection) {
        this.running = false;
        this.isOpened = false;
        this.home = home;
        this.connection = connection;
        this.connection_info = null;
        this.chat = null;
        this.game = null;

    }

    @Override
    public void run() {
        running = true;
        String message;
        while (running) {
            message = Utils.receiveMessage(connection);
            if (message == null || message.equals("GAME_CLOSE")) {
                if (isOpened) {
                    home.getOpened_games().remove(connection_info);
                    home.getConnected_listeners().remove(connection_info);
                    isOpened = false;
                    try {
                        connection.close();
                    } catch (IOException e) {
                        System.err.println("[ClientListener:Run] -> " + e.getMessage());
                    }
                    chat.dispose();
                    game.dispose();
                }
                running = false;
            } else {
                String[] fields = message.split(";");
                if(fields.length > 1){
                    if(fields[0].equals("OPEN_GAME")){
                        String[] splited = fields[1].split(":");
                        connection_info = fields[1];
                        if(!isOpened){
                            home.getOpened_games().add(connection_info);
                            home.getConnected_listeners().put(connection_info, this);
                            isOpened = true;
                            chat = new Chat(home, connection, connection_info, home.getConnection_info().split(":")[0]);
                            game =new Game(home, connection, connection_info, home.getConnection_info().split(":")[0], false);
                        }
                    }
                    else if(fields[0].equals("MESSAGE")){
                        String msg = "";
                        for(int i=1;i<fields.length;i++){
                            msg += fields[i];
                            if(i>1) msg += ";";
                        }
                        chat.append_message(msg);
                    }                    
                    else if(fields[0].equals("GAME_COMMAND_ATT_PLAYER")){
                        String msg = "";
                        for(int i=1;i<fields.length;i++){
                            msg += fields[i];
                            if(i>1) msg += ";";
                        }
                        game.setPlayer_board(Utils.stringToBoard(msg));
                        game.refreshButtons(true);
                    }
                    else if(fields[0].equals("GAME_COMMAND_ATT_ENEMY")){
                        String msg = "";
                        for(int i=1;i<fields.length;i++){
                            msg += fields[i];
                            if(i>1) msg += ";";
                        }
                        game.setEnemy_board(Utils.stringToBoard(msg));
                        game.refreshButtons(true);
                    }
                    else if(fields[0].equals("GAME_COMMAND_ATT_TURN")){
                        String msg = "";
                        for(int i=1;i<fields.length;i++){
                            msg += fields[i];
                            if(i>1) msg += ";";
                        }
                        game.setCurrentTurn(Integer.parseInt(msg));
                    }
                    else if(fields[0].equals("GAME_COMMAND_ATT_LABELS")){
                        String msg = "";
                        for(int i=1;i<fields.length;i++){
                            msg += fields[i];
                            if(i>1) msg += ";";
                        }
                        game.getJl_currentTurn().setText(msg);
                    }
                }
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean isOpened) {
        this.isOpened = isOpened;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
    
}
