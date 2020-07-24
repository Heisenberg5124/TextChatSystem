package client;

import modelclient.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private ObjectInputStream objectInputStream;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();
    private ArrayList<UserRegisterListener> userRegisterListeners = new ArrayList<>();
    private ArrayList<MessageStatusListener> messageStatusListeners = new ArrayList<>();

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    private void unknownCmd() throws IOException, ClassNotFoundException {
        String cmd = "asdd\n";
        serverOut.write(cmd.getBytes());

        String response = (String) objectInputStream.readObject();
        System.out.println("Response Line: " + response);
    }

    public Map<String, List<Message>> getAllMessages() throws IOException, ClassNotFoundException {
        Map<String, List<Message>> allMessages = new HashMap<>();
        String cmd = "getallmessages\n";
        serverOut.write(cmd.getBytes());

        Map<String, List<String>> map = (Map<String, List<String>>) objectInputStream.readObject();

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            List<Message> messages = handleGetAllMessages(values);
            allMessages.put(key, messages);
        }

        return allMessages;
    }

    private List<Message> handleGetAllMessages(List<String> list) {
        List<Message> messages = new ArrayList<>();
        for (String message : list) {
            String[] tokenMessage = message.split(",");
            Message msg = new Message(tokenMessage[0], tokenMessage[1], tokenMessage[2], tokenMessage[3], tokenMessage[4].equalsIgnoreCase("true"));
            messages.add(msg);
        }
        return messages;
    }

    public List<String> getAllRegisters() throws IOException, ClassNotFoundException {
        String cmd = "getallregisters\n";
        serverOut.write(cmd.getBytes());

        List<String> registerList = (List<String>) objectInputStream.readObject();
        return registerList;
    }

    public void quit() throws IOException {
        String cmd = "quit\n";
        serverOut.write(cmd.getBytes());
    }

    public boolean register(String login, String password, String fullname) throws IOException, ClassNotFoundException {
        String cmd = "register " + login + " " + password + " " + fullname + "\n";
        serverOut.write(cmd.getBytes());

        String response = (String) objectInputStream.readObject();
        System.out.println("Response Line: " + response);

        return "ok register".equalsIgnoreCase(response);
    }

    public void closeChat(String recipient) throws IOException {
        String cmd = "close " + recipient + "\n";
        serverOut.write(cmd.getBytes());
    }

    public void openChat(String recipient) throws IOException {
        String cmd = "open " + recipient + "\n";
        serverOut.write(cmd.getBytes());
    }

    public void msg(String from, String sendTo, String msgBody, boolean isRead) throws IOException {
        String read = isRead ? "true" : "false";
        String cmd = "msg " + from + " " + sendTo + " " + read + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }

    public void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
    }

    public int login(String login, String password) throws IOException, ClassNotFoundException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        String response = (String) objectInputStream.readObject();
        System.out.println("Response Line: " + response);
        if ("login ok".equalsIgnoreCase(response)) {
            startMessageReader();
            return 1;
        }
        if ("login error: username not exist".equalsIgnoreCase(response))
            return 0;
        if ("login error: wrong password".equalsIgnoreCase(response))
            return -1;
        return -2;
    }

    private void startMessageReader() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        thread.start();
    }

    private void readMessageLoop() {
        try {
            Object object;
            while ((object = objectInputStream.readObject()) != null) {
                if (object instanceof String) {
                    String line = (String) object;
                    String[] tokens = line.split(" ");
                    if (tokens.length > 0) {
                        String cmd = tokens[0];
                        switch (cmd) {
                            case "online":
                                handleOnline(tokens);
                                break;
                            case "offline":
                                handleOffline(tokens);
                                break;
                            case "msg":
                                String[] tokensMsg = line.split(" ", 5);
                                handleMessage(tokensMsg);
                                break;
                            case "registered":
                                handleRegistered(tokens);
                                break;
                            case "opened":
                                String[] tokensOpen = line.split(" ", 3);
                                handleOpenChat(tokensOpen);
                                break;
                            case "closed":
                                String[] tokensClose = line.split(" ", 3);
                                handleCloseChat(tokensClose);
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            try {
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void handleCloseChat(String[] tokensClose) {
        String sender = tokensClose[1];
        String recipient = tokensClose[2];
        for (MessageStatusListener listener : messageStatusListeners)
            listener.close(sender, recipient);
    }

    private void handleOpenChat(String[] tokensOpen) {
        String sender = tokensOpen[1];
        String recipient = tokensOpen[2];
        for (MessageStatusListener listener : messageStatusListeners)
            listener.open(sender , recipient);
    }

    private void handleRegistered(String[] tokens) {
        String login = tokens[1];
        for (UserRegisterListener listener : userRegisterListeners)
            listener.registered(login);
    }

    private void handleMessage(String[] tokensMsg) {
        String from = tokensMsg[1];
        String sendTo = tokensMsg[2];
        String msgBody = tokensMsg[4];
        String isRead = tokensMsg[3];

        for (MessageListener listener : messageListeners)
            listener.onMessage(from, sendTo, msgBody, isRead);
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners)
            listener.offline(login);
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners)
            listener.online(login);
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            this.serverOut = socket.getOutputStream();
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

    public void addUserRegisterListener(UserRegisterListener listener) {
        userRegisterListeners.add(listener);
    }

    public void removeUserRegisterListener(UserRegisterListener listener) {
        userRegisterListeners.remove(listener);
    }

    public void addMessageStatusListener(MessageStatusListener listener) {
        messageStatusListeners.add(listener);
    }

    public void removeMessageStatusListener(MessageStatusListener listener) {
        messageStatusListeners.remove(listener);
    }
}
