package ru.endrysan.java.task_tracker_server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import ru.endrysan.java.task_tracker_client.ServerAnswer;
import ru.endrysan.java.task_tracker_client.ServerCommand;
import ru.endrysan.java.task_tracker_client.Session;
import ru.endrysan.java.task_tracker_client.model.User;
import ru.endrysan.java.task_tracker_server.dao.SessionDAO;
import ru.endrysan.java.task_tracker_server.dao.UserDAO;

public class EchoServer implements Runnable {

    private static final Logger LOG = Logger.getLogger(EchoServer.class);

    private Socket socket;
    private UserDAO userDAO;
    private SessionDAO sessionDAO;

    private Session currentSession;

    public EchoServer(Socket socket, UserDAO userDAO, SessionDAO sessionDAO) {
        this.socket = socket;
        this.userDAO = userDAO;
        this.sessionDAO = sessionDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        LOG.info("Connection with client established");
        BufferedInputStream bis = null;
        ObjectInputStream ois = null;
        BufferedOutputStream bos = null;
        ObjectOutputStream oos = null;

        try {
            bis = new BufferedInputStream(socket.getInputStream());
            ois = new ObjectInputStream(bis);

            bos = new BufferedOutputStream(socket.getOutputStream());
            oos = new ObjectOutputStream(bos);

            while (true) {
                try {
                    Object readed = ois.readObject();
                    if (readed instanceof ServerCommand) {
                        ServerCommand command = (ServerCommand) readed;
                        switch (command.getAction()) {
                            case SIGNIN:
                                User user = (User) command.getObject();
                                User existing = userDAO.getUser(user.getLogin());
                                if (existing != null) {
                                    String expectedPass = existing.getPassword();
                                    String actualPass = user.getPassword();
                                    if ((expectedPass == null && actualPass == null) || expectedPass.equals(actualPass)) {
                                        currentSession = new Session(user);
                                        oos.writeObject(ServerAnswer.success(""));
                                        oos.flush();
                                        break;
                                    } else {
                                        oos.writeObject(ServerAnswer.failure("Password are not match"));
                                        oos.flush();
                                        break;
                                    }
                                }
                                oos.writeObject(ServerAnswer.failure("User not found"));
                                oos.flush();
                                break;

                            case SIGNUP:
                                User userToSave = (User) command.getObject();
                                if (userDAO.save(userToSave)) {
                                    oos.writeObject(ServerAnswer.success(""));
                                    oos.flush();
                                    currentSession = new Session(userToSave);
                                    break;
                                }
                                oos.writeObject(ServerAnswer.failure("Couldn't create a user"));
                                oos.flush();
                                break;
                            case SIGNOUT:
                                oos.writeObject(ServerAnswer.success(""));
                                oos.flush();
                                break;
                        }
                    } else {
                        oos.writeObject(ServerAnswer.exception("Server doesn't know command"));
                        oos.flush();
                    }
                } catch (ClassNotFoundException e) {
                    oos.writeObject(ServerAnswer.exception("Fatal server exception"));
                    oos.flush();
                }
            }
        } catch (IOException e) {
            LOG.error(e, e);
        } finally {
            IOUtils.closeQuietly(ois);
            IOUtils.closeQuietly(oos);
            LOG.info("Connection with client was closed");
            if (currentSession != null) {
                currentSession.setEndDate(new Date());
                sessionDAO.save(currentSession);
                currentSession = null;
            }
        }
    }
}
