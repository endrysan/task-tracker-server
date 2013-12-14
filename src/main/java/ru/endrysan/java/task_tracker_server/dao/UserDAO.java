package ru.endrysan.java.task_tracker_server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import ru.endrysan.java.task_tracker_client.model.User;

public class UserDAO {

    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final String URL = "jdbc:h2:~/test";
    private static final String DRIVER = "org.h2.Driver";

    private static final Logger LOG = Logger.getLogger(UserDAO.class);

    public UserDAO() {
       try {
            Class.forName(DRIVER);
            init();
       } catch (ClassNotFoundException e) {
            LOG.fatal(e);
       }
    }

    public synchronized boolean save(User newUser) {
       Connection connection = null;
       PreparedStatement statement = null;
       try{
            connection = getConnection();
            statement = connection.prepareStatement("insert into user values(default, ?, ?)");
            statement.setString(1, newUser.getLogin());
            statement.setString(2, newUser.getPassword());
            statement.executeUpdate();
            return true;
       } catch(Exception e){
            LOG.error(e, e);
       }
       finally{
           DbUtils.closeQuietly(connection);
           DbUtils.closeQuietly(statement);
       }
       return false;
    }

    public synchronized User getUser(String login) {
        Connection con = null;
        PreparedStatement prst = null;
        ResultSet resultSet = null;
        try {
            con = getConnection();
            prst = con.prepareStatement("select * from user where login = ?");
            prst.setString(1, login);
            resultSet = prst.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setLogin(resultSet.getString("login"));
                user.setPassword(resultSet.getString("pass"));
                return user;
            }
        } catch (SQLException e) {
            LOG.error(e);
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(prst);
            DbUtils.closeQuietly(con);
        }
        return null;
    }

    private void init() {
        Connection con = null;
        Statement st = null;
        try {
            con = getConnection();
            st = con.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS USER ("
                     + " ID INT PRIMARY KEY AUTO_INCREMENT,"
                     + " LOGIN VARCHAR(255) NOT NULL,"
                     + " PASS VARCHAR(255))");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(st);
            DbUtils.closeQuietly(con);
        }
    }

    private synchronized Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
