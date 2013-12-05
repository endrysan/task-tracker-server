package ru.endrysan.java.task_tracker_server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.dbutils.DbUtils;

public class WorkTimeDAO {

    private String user = "sa";
    private String password = "";
    private String url = "jdbc:h2:~/test";
    private String driver = "org.h2.Driver";
    
    public WorkTimeDAO() {
        try {
            Class.forName(driver);
       } catch (ClassNotFoundException e) {
            e.printStackTrace();
       }
    }
    
    public void toStart() {
        Connection connection = null;
        PreparedStatement statement = null;
        try{
             connection = DriverManager.getConnection(url, user, password);
             statement = connection.prepareStatement("insert into timelog values(?, now(), default)");
             statement.setInt(1, 1); // id user
             statement.executeUpdate();
        } catch(Exception e){
             e.printStackTrace();
        }
        finally{
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(statement);
        }
    }
    
    public void toEnd() {
        Connection connection = null;
        PreparedStatement statement = null;
        try{
             connection = DriverManager.getConnection(url, user, password);
             statement = connection.prepareStatement("insert into timelog values(?, default, now())");
             statement.setInt(1, 1); // id user
             statement.executeQuery();
        } catch(Exception e){
             e.printStackTrace();
        }
        finally{
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(statement);
        }
    }
    public static void main(String[] args) {
        new WorkTimeDAO().toStart();
    }
}
