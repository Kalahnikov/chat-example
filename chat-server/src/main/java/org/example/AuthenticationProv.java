package org.example;

import java.sql.*;
import java.util.*;

public class AuthenticationProv implements AuthenticationProvider {
    private final List<User> users;
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String SELECT_USERS =
            "select u.id as id, u.login as login, u.pass as pass, u.user_name as user_name, r.role_name as role_name  from user_to_roles utr\n" +
                    "\tjoin users u on user_id=u.id \n" +
                    "\tjoin roles r on role_id=r.id";
    private static final String SELECT_USER_WITH_ROLE = "select u.id, u.login, u.pass from user_to_roles utr" +
            "join users u on user_id=u.id" +
            "join roles r on role_id=r.id" +
            "where r.role_name = ?";

    public AuthenticationProv() {
        this.users = new ArrayList<>();
    }

    public List<User> getSelectUsers() {
        List<User> users1 = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "kalashnikov", "kalashnikov")) {
            try (PreparedStatement ps = connection.prepareStatement(SELECT_USERS)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String log = rs.getString(2);
                        String pass = rs.getString(3);
                        String userName = rs.getString(4);
                        String role = rs.getString(5);
                        User user = new User(log, pass, userName, role);
                        users1.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users1;
    }

    public List<User> getSelectUsersWithRole() {
        List<User> users1 = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "kalashnikov", "kalashnikov")) {
            try (PreparedStatement ps = connection.prepareStatement(SELECT_USER_WITH_ROLE)) {
                ps.setString(1, "Admin1");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String log = rs.getString(2);
                        String pass = rs.getString(3);
                        String userName = rs.getString(4);
                        String role = rs.getString(5);
                        User user = new User(log, pass, userName, role);
                        users1.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users1;
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        List<User> users2 = getSelectUsers();
        for (User user : users2) {
            if (Objects.equals(user.getPassword(), password) && Objects.equals(user.getLogin(), login)) {
                return user.getUsername();
            }
        }
        return null;
    }

    public boolean getRoleByUserRole(String login, String password, String role) {
        List<User> users3 = getSelectUsersWithRole();
        for (User user : users3) {
            System.out.println(Arrays.toString((users3.toArray())));
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

