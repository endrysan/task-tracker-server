package ru.endrysan.java.task_tracker_server.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import ru.endrysan.java.task_tracker_client.Session;

public class SessionDAO {

    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final String URL = "jdbc:h2:~/test";
    private static final String DRIVER = "org.h2.Driver";

    private static final Logger LOG = Logger.getLogger(SessionDAO.class);

    public SessionDAO() {
       try {
            Class.forName(DRIVER);
            init();
       } catch (ClassNotFoundException e) {
            LOG.fatal(e);
       }
    }

    public boolean save(Session session) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try{
             connection = getConnection();
             statement = connection.prepareStatement("insert into session values(?, ?, ?)");
             statement.setString(1, session.getUser().getLogin());
             statement.setTimestamp(2, new Timestamp(session.getStartDate().getTime()));
             statement.setTimestamp(3, new Timestamp(session.getEndDate().getTime()));
             statement.executeUpdate();
             return true;
        } catch(Exception e) {
             LOG.error(e);
        } finally {
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(resultSet);
        }
        return false;
    }

    private void init() {
        Connection con = null;
        Statement st = null;
        try {
            con = getConnection();
            st = con.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS SESSION ("
                     + " LOGIN VARCHAR(255),"
                     + " START_DATE TIMESTAMP NOT NULL,"
                     + " END_DATE TIMESTAMP NOT NULL,"
                     + " FOREIGN KEY (LOGIN) REFERENCES USER(LOGIN))");
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
