/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import client.ChatClient;
import client.MessageListener;
import client.MessageStatusListener;
import client.UserRegisterListener;
import modelclient.Message;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author ADMIN
 */
public class UserListGUI extends javax.swing.JFrame {

    private final ChatClient client;
    private final DefaultListModel<String> model;
    private final DefaultListModel<String> offlineModel = new DefaultListModel<>();
    private final String login;
    private Map<String, List<Message>> messageMap;
    private Map<String, Integer> offlineMessagesMap = new HashMap<>();
    private Map<String, Boolean> open = new HashMap<>();
    private Map<String, Boolean> otherOpen = new HashMap<>();
    private int count = 0;

    public Map<String, Boolean> getOtherOpen() {
        return otherOpen;
    }

    public void setOtherOpen(Map<String, Boolean> otherOpen) {
        this.otherOpen = otherOpen;
    }

    public String getLogin() {
        return login;
    }

    public Map<String, Integer> getOfflineMessagesMap() {
        return offlineMessagesMap;
    }

    public void setOfflineMessagesMap(Map<String, Integer> offlineMessagesMap) {
        this.offlineMessagesMap = offlineMessagesMap;
    }

    public DefaultListModel<String> getOfflineModel() {
        return offlineModel;
    }

    public JLabel getLbLogin() {
        return lbLogin;
    }

    public void setLbLogin(JLabel lbLogin) {
        this.lbLogin = lbLogin;
    }

    public Map<String, List<Message>> getMessageMap() {
        return messageMap;
    }

    public void setMessageMap(Map<String, List<Message>> messageMap) {
        this.messageMap = messageMap;
    }

    public Map<String, Boolean> getOpen() {
        return open;
    }

    public void setOpen(Map<String, Boolean> open) {
        this.open = open;
    }

    /**
     * Creates new form UserListGUI
     */
    public UserListGUI(ChatClient client, DefaultListModel<String> model, Map<String, List<Message>> allMessages, String login) {
        initComponents();
        this.model = model;
        listUser.setModel(model);
        this.client = client;
        this.messageMap = allMessages;
        this.login = login;

        for (int i = 0; i < model.getSize(); i++) {
            String key = model.getElementAt(i);
            if (key.contains(" (online)")) {
                String[] strings = key.split(" ");
                key = strings[0];
            }
            open.put(key, Boolean.FALSE);
            otherOpen.put(key, Boolean.FALSE);

            for (Message message : messageMap.get(key))
                if (!message.isRead() && message.getRecipient().equals(login) && message.getRecipient().equals(key)) {
                    String keyOff = message.getSender();
                    if (!offlineMessagesMap.containsKey(keyOff)) {
                        offlineMessagesMap.put(keyOff, 1);
                    } else {
                        int numOff = offlineMessagesMap.get(keyOff) + 1;
                        offlineMessagesMap.replace(keyOff, numOff);
                    }
                }
        }

        setTextMessage();

        client.addUserRegisterListener(new UserRegisterListener() {
            @Override
            public void registered(String login) {
                open.put(login, Boolean.FALSE);
                otherOpen.put(login, Boolean.FALSE);
            }
        });

        client.addMessageStatusListener(new MessageStatusListener() {
            @Override
            public void open(String sender, String recipient) {
                offlineMessagesMap.remove(sender);
                List<Message> messagesSender = messageMap.get(sender);
                for (Message message : messagesSender)
                    if (message.getRecipient().equals(recipient))
                        message.setRead(true);

                List<Message> messagesRecipient = messageMap.get(recipient);
                for (Message message : messagesRecipient)
                    if (message.getSender().equals(sender))
                        message.setRead(true);

                otherOpen.replace(recipient, Boolean.TRUE);
                setTextMessage();
            }

            @Override
            public void close(String sender, String recipient) {
                otherOpen.replace(recipient, Boolean.FALSE);
            }
        });

        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromLogin, String toLogin, String msgBody, String isRead) {
                if (toLogin.equals(login) && !isRead.equals("true")) {
                    String keyOff = fromLogin;
                    if (!offlineMessagesMap.containsKey(keyOff)) {
                        offlineMessagesMap.put(keyOff, 1);
                    } else {
                        int numOff = offlineMessagesMap.get(keyOff) + 1;
                        offlineMessagesMap.replace(keyOff, numOff);
                    }
                    setTextMessage();
                }
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    for (String key : open.keySet())
                        if (open.get(key)) {
                            client.closeChat(key);
                            open.replace(key, Boolean.FALSE);
                        }
                    client.logoff();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        listUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    doChat();
            }
        });
    }

    public void setTextMessage() {
        int countOfflineMessage = 0;
        for (String key : offlineMessagesMap.keySet())
            countOfflineMessage += offlineMessagesMap.get(key);
        String msg = countOfflineMessage > 0 ? "You have " + countOfflineMessage + " offline message(s)." : "You don't have offline message";
        lbMessage.setText(msg);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lbLogin = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listUser = new javax.swing.JList<>();
        jPanel4 = new javax.swing.JPanel();
        btnChat = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnOffMessage = new javax.swing.JButton();
        lbMessage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        lbLogin.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbLogin.setText("Login as: ");
        lbLogin.setMaximumSize(new java.awt.Dimension(300, 30));
        lbLogin.setPreferredSize(new java.awt.Dimension(300, 30));
        jPanel2.add(lbLogin);

        lbMessage.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbMessage.setText("You have:");
        lbMessage.setMaximumSize(new java.awt.Dimension(300, 30));
        lbMessage.setPreferredSize(new java.awt.Dimension(300, 30));
        jPanel2.add(lbMessage);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Registered Users:");
        jLabel2.setMaximumSize(new java.awt.Dimension(106, 30));
        jLabel2.setPreferredSize(new java.awt.Dimension(106, 30));
        jPanel2.add(jLabel2);

        jPanel1.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanel3.setLayout(new java.awt.BorderLayout());

        listUser.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jScrollPane1.setViewportView(listUser);

        jScrollPane1.setViewportView(listUser);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 20));

        btnChat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnChat.setText("Chat");
        btnChat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChatActionPerformed(evt);
            }
        });
        jPanel4.add(btnChat);

        btnClose.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel4.add(btnClose);

        btnOffMessage.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnOffMessage.setText("Offline Message");
        btnOffMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOffMessageActionPerformed(evt);
            }
        });
        jPanel4.add(btnOffMessage);

        jPanel1.add(jPanel4, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>

    private void btnChatActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        if (!listUser.isSelectionEmpty()) {
            doChat();
        }
    }

    private void doChat() {
        String login = listUser.getSelectedValue();

        if (login.contains(" (online)")) {
            String[] strings = login.split(" ");
            login = strings[0];
        }

        if (!open.get(login)) {
            offlineMessagesMap.remove(login);

            ChatGUI chatGUI = new ChatGUI(client, this.login, login, messageMap, this);
            chatGUI.setVisible(true);
            chatGUI.setLocation(getX() + getWidth(), getY() + getHeight());
            messageMap = chatGUI.getMessageMap();

            try {
                client.openChat(login);
            } catch (IOException e) {
                e.printStackTrace();
            }
            open.replace(login, Boolean.TRUE);
            setTextMessage();
        }
    }

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        try {
            for (String key : open.keySet())
                if (open.get(key)) {
                    client.closeChat(key);
                    open.replace(key, Boolean.FALSE);
                }
            client.logoff();
            System.exit(0);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void btnOffMessageActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        offlineModel.clear();
        for (String key : offlineMessagesMap.keySet())
            offlineModel.addElement(String.format("%s (%d)", key, offlineMessagesMap.get(key)));
        OfflineChatGUI offlineChatGUI = new OfflineChatGUI(client, this);
        offlineChatGUI.setVisible(true);
        offlineChatGUI.setLocation(getX() + getWidth(), getY());
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnChat;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnOffMessage;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbLogin;
    private javax.swing.JLabel lbMessage;
    private javax.swing.JList<String> listUser;
    // End of variables declaration
}
