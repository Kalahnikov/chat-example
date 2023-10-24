package org.example;

import java.util.*;

public class Roles {
    private List<String> adminLogin;
    private List<String> adminPassword;

    public List<String> getAdminLogin() {
        return adminLogin;
    }

    public List<String> getAdminPassword() {
        return adminPassword;
    }

    public Roles() {
        this.adminLogin = new ArrayList<>(Arrays.asList("Admin1", "Admin2", "Admin3"));
        this.adminPassword = new ArrayList<>(Arrays.asList("qqq", "www", "eee"));
    }

    public void addAdminIndicator(String login, String password) {
        adminLogin.add(login);
        adminPassword.add(password);
    }
}