package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientHandler {
    private Socket socket;

    private Server server;
    private DataInputStream in;
    private DataOutputStream out;

    private String username;

    public String getUsername() {
        return username;
    }

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                authenticateUser(server);
                communicateWithUser(server);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                disconnect(this);
            }
        }).start();
    }

    public void communicateWithUser(Server server) throws IOException {
        while (true) {
            // /exit -> disconnect()
            // /w user message -> user
            // /list -> getUserList
            // /kick user -> delete user

            String message = in.readUTF();
            if (message.startsWith("/")) {
                if (message.equals("/exit")) {
                    break;
                } else if (message.startsWith("/w")) {
                    server.broadcastMessageToUser(message);
                } else if (message.equals("/list")) {
                    List<String> userList = server.getUserList();
                    String joinedUsers =
                            String.join(", ", userList);
//                            userList.stream().collect(Collectors.joining(","));
                    sendMessage("Server: " + joinedUsers);
                } else if (message.startsWith("/kick")) {
                    sendMessage("Введите свои логин, пароль и роль в чате");
                    String examination = in.readUTF();
                    String[] args = examination.split(" ");
                    if (server.getAuthenticationProvider().getRoleByUserRole(args[0], args[1], args[2])) {
                        server.kickUser(message);
                    } else {
                        sendMessage("Server: Вы не являетесь админом. Запрос не доступен");
                    }
                }
            } else {
                server.broadcastMessage("Server: " + message);
            }
        }
    }

    public void authenticateUser(Server server) throws IOException {
        boolean isAuthenticated = false;
        while (!isAuthenticated) {
            String message = in.readUTF();
//            /auth login password
//            /register login nick password
            String[] args = message.split(" ");
            String command = args[0];
            Roles roles = new Roles();
            switch (command) {
                case "/auth": {
                    String login = args[1];
                    String password = args[2];
                    String username = server.getAuthenticationProvider().getUsernameByLoginAndPassword(login, password);
                    if (username == null || username.isBlank()) {
                        sendMessage("Указан неверный логин/пароль");
                    } else if (roles.getAdminLogin().contains(args[1]) && roles.getAdminPassword().contains(args[2])) {
                        this.username = username;
                        sendMessage(ROLE.ADMIN.toString() + " " + username + ", добро пожаловать в чат!");
                        server.subscribe(this);
                        isAuthenticated = true;
                    } else {
                        this.username = username;
                        sendMessage(ROLE.USER.toString() + " " + username + ", добро пожаловать в чат!");
                        server.subscribe(this);
                        isAuthenticated = true;
                    }
                    break;
                }
                case "/register": {
                    String login = args[1];
                    String nick = args[2];
                    String password = args[3];
                    boolean isRegistred = server.getAuthenticationProvider().register(login, password, nick);
                    if (!isRegistred) {
                        sendMessage("Указанный логин/никнейм уже заняты");
                    } else if (roles.getAdminLogin().contains(args[1]) && roles.getAdminPassword().contains(args[3])) {
                        this.username = nick;
                        sendMessage(ROLE.ADMIN.toString() + " " + username + ", добро пожаловать в чат!");
                        server.subscribe(this);
                        isAuthenticated = true;
                    } else {
                        this.username = nick;
                        sendMessage(ROLE.USER.toString() + " " + nick + ", добро пожаловать в чат!");
                        server.subscribe(this);
                        isAuthenticated = true;
                    }
                    break;
                }
                default: {
                    sendMessage("Сначала авторизуйтесь");
                }
            }
        }
    }

    public void disconnect(ClientHandler clientHandler) {
        server.unsubscribe(clientHandler);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
            disconnect(this);
        }
    }
}
