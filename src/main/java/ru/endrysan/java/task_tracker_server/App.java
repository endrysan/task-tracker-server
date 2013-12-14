package ru.endrysan.java.task_tracker_server;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import ru.endrysan.java.task_tracker_server.dao.SessionDAO;
import ru.endrysan.java.task_tracker_server.dao.UserDAO;

public class App {

    private static final int PORT = 3078;
    private static final Logger LOG = Logger.getLogger(App.class);

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        UserDAO userDAO = new UserDAO();
        SessionDAO sessionDAO = new SessionDAO();

        try {
            serverSocket = new ServerSocket(PORT);
            LOG.info("Connection Socket Created");
            try {
                while (true) {
                    LOG.info("Waiting for Connection");
                    new Thread(new EchoServer(serverSocket.accept(), userDAO, sessionDAO)).start();
                }
            } catch (IOException e) {
                LOG.error(e);
            }
        } catch (IOException e) {
            LOG.error(e);
        } finally {
            IOUtils.closeQuietly(serverSocket);
        }
    }

}
