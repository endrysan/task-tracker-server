package ru.endrysan.java.task_tracker_server;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ru.endrysan.java.task_tracker_client.model.User;
import ru.endrysan.java.task_tracker_server.dao.UserDAO;

import org.apache.commons.io.IOUtils;

public class Server {
    
    private static final int PORT = 3078;
    
    public Server() {
        
        ServerSocket server = null;
        BufferedInputStream bis = null;
        ObjectInputStream ois = null;
        List<User> listUser = new ArrayList<User>();
        
        try{
            server = new ServerSocket(PORT);
            Socket client = server.accept();
            bis = new BufferedInputStream(client.getInputStream());
            ois = new ObjectInputStream(bis);
            User user = (User) ois.readObject();
//            System.out.println(user.getLogin());
//            System.out.println(user.getPassword());
            UserDAO userdao = new UserDAO();
            listUser.addAll(userdao.getAll());
            for(User u: listUser) {
                if(!u.getLogin().equals(user.getLogin())) {
                    userdao.save(user);
                }
                else {
                    // logged and open workplace
                }
            }
            
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(ois);
            IOUtils.closeQuietly(server);
        }
    }
    public static void main(String[] args) {
        new Server();
    }
}
