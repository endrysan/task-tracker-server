package ru.endrysan.java.task_tracker_server.services;

import java.util.ArrayList;
import java.util.List;

import ru.endrysan.java.task_tracker_client.model.User;
import ru.endrysan.java.task_tracker_server.dao.UserDAO;

public class DataBaseService {

    UserDAO userdao = new UserDAO();
    public DataBaseService() {
    }
    
    public void save(User user) {
        userdao.save(user);
    }
    
    public List<User> getAll() {
        List<User> listUser = new ArrayList<User>();
        listUser.addAll(userdao.getAll());
        return listUser;
    }
}
