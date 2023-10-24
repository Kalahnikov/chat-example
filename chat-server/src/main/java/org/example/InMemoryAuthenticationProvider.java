package org.example;

import java.util.*;

public class InMemoryAuthenticationProvider implements AuthenticationProvider {
    private final List<User> users;

    public InMemoryAuthenticationProvider() {
        this.users = new ArrayList<>();
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (Objects.equals(user.getPassword(), password) && Objects.equals(user.getLogin(), login)) {
                return user.getUsername();
            }
        }
        return null;
    }

    public boolean getRoleByUserRole(String login, String password, String role) {
        for (User user : users) {
            if (Objects.equals(user.getPassword(), password) && Objects.equals(user.getLogin(), login) && Objects.equals(user.getRole(), role)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean register(String login, String password, String username) {
        Roles roles = new Roles();
        String role = null;
        for (User user : users) {
            if (Objects.equals(user.getUsername(), username) && Objects.equals(user.getLogin(), login)) {
                return false;
            }
        }
        if (roles.getAdminLogin().contains(login) && roles.getAdminPassword().contains(password)) {
            role = ROLE.ADMIN.toString();
        } else {
            role = ROLE.USER.toString();
        }
        users.add(new User(login, password, username, role));
        return true;
    }
}

