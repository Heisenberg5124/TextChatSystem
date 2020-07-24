package server;

import database.DatabaseWorker;
import model.Message;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private ObjectOutputStream objectOutputStream;
    private DatabaseWorker database = new DatabaseWorker();

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException {
        this.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        InputStream inputStream = clientSocket.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        label:
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(" ");
            if (tokens.length > 0) {
                String cmd = tokens[0];
                switch (cmd) {
                    case "logoff":
                        handleLogoff();
                        break label;
                    case "quit":
                        clientSocket.close();
                        break label;
                    case "login":
                        handleLogin(objectOutputStream, tokens);
                        break;
                    case "msg":
                        String[] tokensMsg = line.split(" ", 5);
                        handleMessage(tokensMsg);
                        break;
                    case "register":
                        String[] tokensRegister = line.split(" ", 4);
                        handleRegister(objectOutputStream, tokensRegister);
                        break;
                    case "getallregisters":
                        getAllRegisters(objectOutputStream);
                        break;
                    case "getallmessages":
                        getAllMessages(objectOutputStream);
                        break;
                    case "open":
                        String[] tokensOpen = line.split(" ", 2);
                        handleOpenChat(tokensOpen);
                        break;
                    case "close":
                        String[] tokensClose = line.split(" ", 2);
                        handleCloseChat(tokensClose);
                    default:
                        String msg = "unknown " + cmd;
                        objectOutputStream.writeObject(msg);
                        break;
                }
            }
        }

        clientSocket.close();
    }

    private void handleCloseChat(String[] tokensClose) throws IOException {
        String recipient = tokensClose[1];

        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker : workerList)
            if (recipient.equalsIgnoreCase(worker.getLogin())) {
                String outMsg = String.format("closed %s %s", login, recipient);
                worker.send(outMsg);
            }
    }

    private void handleOpenChat(String[] tokensOpen) throws IOException {
        String recipient = tokensOpen[1];
        database.updateMessages(login, recipient);

        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker : workerList)
            if (recipient.equalsIgnoreCase(worker.getLogin())) {
                String outMsg = String.format("opened %s %s", login, recipient);
                worker.send(outMsg);
            }
    }

    private void getAllMessages(ObjectOutputStream objectOutputStream) throws IOException {
        Map<String, List<String>> allMessage = new HashMap<>();
        List<String> registeredList = new ArrayList<>(database.getAllUsers().keySet());

        for (String username : registeredList) {
            List<String> messages = database.getAllMessages(username);
            allMessage.put(username, messages);
        }

        objectOutputStream.writeObject(allMessage);
    }

    private void getAllRegisters(ObjectOutputStream objectOutputStream) throws IOException {
        List<String> registeredList = new ArrayList<>(database.getAllUsers().keySet());
        objectOutputStream.writeObject(registeredList);
    }

    private void handleRegister(ObjectOutputStream objectOutputStream, String[] tokens) throws IOException {
        String login = tokens[1];
        String password = tokens[2];
        String fullname = tokens[3];

        if (!database.insertUser(new User(login, password, fullname)))
            objectOutputStream.writeObject(("error register"));
        else {
            List<ServerWorker> workerList = server.getWorkerList();
            for (ServerWorker worker : workerList)
                if (worker.getLogin() != null && !login.equals(worker.getLogin())) {
                    String msg2 = "registered " + worker.getLogin();
                    send(msg2);
                }

            String registeredMsg = "registered " + login;
            for (ServerWorker worker : workerList)
                if (!login.equals(worker.getLogin()))
                    worker.send(registeredMsg);
            objectOutputStream.writeObject(("ok register"));
            System.out.println("User registered successfully: " + login);
        }
    }

    private void handleMessage(String[] tokens) throws IOException {
        String from =tokens[1];
        String sendTo = tokens[2];
        String isRead = tokens[3];
        String body = tokens[4];
        database.insertMessage(new Message(login, sendTo, body, isRead.equalsIgnoreCase("true")));

        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker : workerList) {
            if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                String outMsg = "msg " + login + " " + sendTo + " " + isRead + " " + body;
                worker.send(outMsg);
            }
        }
    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();
        database.setOnline(login, false);

        String offlineMsg = "offline " + login;
        for (ServerWorker worker : workerList)
            if (!login.equals(worker.getLogin())) {
                worker.send(offlineMsg);
            }
        System.out.println("User logged off successfully: " + login);
        clientSocket.close();
    }

    public String getLogin() {
        return login;
    }

    private void handleLogin(ObjectOutputStream objectOutputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];
            HashMap<String, String> allUsers = database.getAllUsers();

            int check = checkLogin(login, password, allUsers);
            if (check == 1) {
                String msg = "login ok";
                objectOutputStream.writeObject(msg);
                database.setOnline(login, true);
                this.login = login;
                System.out.println("User logged in successfully: " + login);

                List<ServerWorker> workerList = server.getWorkerList();
                for (ServerWorker worker : workerList)
                    if (worker.getLogin() != null && !login.equals(worker.getLogin())) {
                        String msg2 = "online " + worker.getLogin();
                        send(msg2);
                    }

                String onlineMsg = "online " + login;
                for (ServerWorker worker : workerList)
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
            } else {
                String msg = "";
                switch (check) {
                    case 0:
                        msg = "login error: username not exist";
                        break;
                    case -1:
                        msg = "login error: wrong password";
                        break;
                    case -2:
                        msg = "login error: logged in";
                        break;
                }
                objectOutputStream.writeObject(msg);
                System.err.println("Login failed for " + login);
            }
        }
    }

    /*
        Login success: 1
        Username not exist: 0
        Wrong password: -1
        Logged in: -2
     */
    private int checkLogin(String login, String password, HashMap<String, String> allUsers) {
        if (!allUsers.containsKey(login))
            return 0;
        if (!password.equals(allUsers.get(login)))
            return -1;
        if (database.isOnline(login))
            return -2;
        return 1;
    }

    private void send(String msg) throws IOException {
        if (login != null)
            objectOutputStream.writeObject(msg);
    }
}
