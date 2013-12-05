package ru.endrysan.java.task_tracker_server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;

import ru.endrysan.java.task_tracker_client.model.User;

public class UserDAO {
    
    private String user = "sa";
    private String password = "";
    private String url = "jdbc:h2:~/test";
    private String driver = "org.h2.Driver";
    
    public UserDAO() {
       try {
            Class.forName(driver);
       } catch (ClassNotFoundException e) {
            e.printStackTrace();
       }
    }
    
    public void save(User newUser) {
        
       Connection connection = null;
       PreparedStatement statement = null;
       ResultSet resultSet = null;
       try{
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.prepareStatement("insert into user values(default, '?', '?')");
            statement.setString(1, newUser.getLogin());
            statement.setString(2, newUser.getPassword());
            resultSet = statement.executeQuery();
       } catch(Exception e){
            e.printStackTrace();
       }
       finally{
           DbUtils.closeQuietly(connection);
           DbUtils.closeQuietly(statement);
           DbUtils.closeQuietly(resultSet);
       }
    }
    
    public List<User> getAll() {
        
        List<User> listUser = new ArrayList<User>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try{
             connection = DriverManager.getConnection(url, user, password);
             statement = connection.createStatement();
             resultSet = statement.executeQuery("select * from user");
             while(resultSet.next()){
                 User newUser = new User();
                 newUser.setId(resultSet.getInt("id"));
                 newUser.setLogin(resultSet.getString("Login"));
                 newUser.setPassword(resultSet.getString("Password"));
                 listUser.add(newUser);
             }
        } catch(Exception e){
             e.printStackTrace();
        }
        finally{
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(resultSet);
        }
        return listUser;
    }
    public static void main(String[] args) {
        UserDAO ud = new UserDAO();
        List<User> list = new ArrayList<User>();
        list.addAll(ud.getAll());
        for (User u: list) {
            System.out.println(u.getId() + " " + u.getLogin() + " " + u.getPassword());
        }
    }
    

}
