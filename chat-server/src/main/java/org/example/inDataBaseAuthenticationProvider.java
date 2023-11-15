package org.example;

import java.util.*;

public class inDataBaseAuthenticationProvider implements AuthenticationProvider {
    private final List<User> users;


    public inDataBaseAuthenticationProvider() {
        this.users = new ArrayList<>();
    }


    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        dataBase dataBase = new dataBase();
        List<User> users1 = dataBase.selectUsers();
        for (User user : users1) {
            if (Objects.equals(user.getPassword(), password) && Objects.equals(user.getLogin(), login)) {
                return user.getUsername();
            }
        }
        return null;
    }

    public String getRoleByUserRole(String login, String password, String role) {
        dataBase dataBase = new dataBase();
        List<User> users1 = dataBase.selectUsersWithRole(role);
        for (User user : users1) {
            if (Objects.equals(user.getPassword(), password) && Objects.equals(user.getLogin(), login) && Objects.equals(user.getRole(), role)) {
                return user.getRole();
            }
        }
        return null;
    }

    @Override
    public synchronized boolean register(String login, String password, String userName) {
        dataBase dataBase = new dataBase();
        int id = dataBase.insertUser(login, password, userName);
        for (User user : dataBase.selectUsers()) {
            if (Objects.equals(user.getUsername(), userName) && Objects.equals(user.getLogin(), login)) {
                return false;
            }
        }
        String role = "User1";
        users.add(new User(id, login, password, userName, role));
        return true;
    }
}

/*
   if ((server.getAuthenticationProvider().getRoleByUserRole(args[1], args[2], "Admin1")).equals(ROLE.ADMIN.toString()) ){
           sendMessage("Доступные операции чата для админа:" +
           "личные сообщения: /w + nick" +
           "просмотр активных пользователей: /list" +
           "смена имени: /changenick" +
           "выйти из чата: /exit" +
           "отключение пользователя: /kick + userName" +
           "бан: /ban + userName" +
           "остановка сервера: /shutdown");
           } else {
           sendMessage("Доступные операции чата для пользователя:" +
           "личные сообщения: /w + nick" +
           "просмотр активных пользователей: /list" +
           "смена имени: /changenick" +
           "выйти из чата: /exit");
           }*/
