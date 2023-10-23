package org.example;

public enum ROLE {
    USER ("Пользователь"),
    ADMIN ("Админ");

    private String role;

    ROLE(String role) {
        this.role = role;
    }
    public String getRole(){
        return role;
    }

    @Override
    public String toString() {
        return role;
    }
}
