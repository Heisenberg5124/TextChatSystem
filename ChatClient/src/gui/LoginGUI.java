package gui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import client.*;
import modelclient.Message;
import modelclient.Status;
import validation.Validation;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public class LoginGUI extends javax.swing.JFrame {

    private final ChatClient client;
    private final Validation validation = new Validation();
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private Map<String, List<Message>> allMessages = new HashMap<>();
    private Map<String, List<Status>> openMap = new HashMap<>();
    private List<String> allRegisters = new ArrayList<>();

    public Map<String, List<Message>> getAllMessages() {
        return allMessages;
    }

    public void setAllMessages(Map<String, List<Message>> allMessages) {
        this.allMessages = allMessages;
    }

    public List<String> getAllRegisters() {
        return allRegisters;
    }

    public void setAllRegisters(List<String> allRegisters) {
        this.allRegisters = allRegisters;
    }

    public Map<String, List<Status>> getOpenMap() {
        return openMap;
    }

    public void setOpenMap(Map<String, List<Status>> openMap) {
        this.openMap = openMap;
    }

    /**
     * Creates new form LoginGUI
     */
    public LoginGUI() {
        initComponents();

        this.client = new ChatClient("localhost", 9999);
        client.connect();


        try {
            allRegisters = client.getAllRegisters();

            for (String register : allRegisters)
                    model.addElement(register);

            allMessages = client.getAllMessages();

            for (String recipient : allMessages.keySet()) {
                List<Status> statuses = new ArrayList<>();
                for (String sender : allRegisters) {
                    if (!sender.equals(recipient)) {
                        Status status = new Status(sender, false);
                        statuses.add(status);
                    }
                }
                openMap.put(recipient, statuses);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                int index = model.indexOf(login);
                model.setElementAt(login + " (online)", index);
            }

            @Override
            public void offline(String login) {
                int index = model.indexOf(login + " (online)");
                model.setElementAt(login, index);
            }
        });

        client.addUserRegisterListener(new UserRegisterListener() {
            @Override
            public void registered(String login) {
                model.addElement(login);
                addNewRegister(login);
            }
        });

        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromLogin, String toLogin, String msgBody, String isRead) {
                List<Message> messages = allMessages.get(fromLogin);
                messages.add(new Message(fromLogin, toLogin, msgBody, false));
                allMessages.replace(fromLogin, messages);

                List<Message> messageList = allMessages.get(toLogin);
                messageList.add(new Message(fromLogin, toLogin, msgBody, false));
                allMessages.replace(toLogin, messageList);
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    client.quit();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        txtLogin.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!validation.checkKeyTyped(c))
                    e.consume();
            }
        });

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!validation.checkKeyTyped(c))
                    e.consume();
            }
        });

        txtLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });

        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });
    }

    public void addNewRegister(String login) {
        List<Message> messages = new ArrayList<>();
        List<Status> statuses = new ArrayList<>();
        allRegisters.add(login);
        allMessages.put(login, messages);
        for (String string : allRegisters)
            statuses.add(new Status(string));
        openMap.put(login, statuses);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel7 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnLogin = new javax.swing.JButton();
        btnRegister = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtLogin = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Text Chat System");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(jLabel1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jPanel1.setPreferredSize(new java.awt.Dimension(412, 60));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 50, 5));

        btnLogin.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnLogin.setText("Login");
        btnLogin.setPreferredSize(new java.awt.Dimension(81, 30));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        jPanel1.add(btnLogin);

        btnRegister.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnRegister.setText("Register");
        btnRegister.setPreferredSize(new java.awt.Dimension(81, 30));
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });
        jPanel1.add(btnRegister);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel6.setPreferredSize(new java.awt.Dimension(50, 153));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 50, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 103, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel6, java.awt.BorderLayout.EAST);

        jPanel4.setPreferredSize(new java.awt.Dimension(50, 153));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 50, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 103, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel4, java.awt.BorderLayout.LINE_START);

        java.awt.GridBagLayout jPanel5Layout = new java.awt.GridBagLayout();
        jPanel5Layout.columnWidths = new int[] {0, 20, 0};
        jPanel5Layout.rowHeights = new int[] {0, 20, 0};
        jPanel5.setLayout(jPanel5Layout);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Login");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel5.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel5.add(jLabel4, gridBagConstraints);

        txtLogin.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtLogin.setPreferredSize(new java.awt.Dimension(150, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel5.add(txtLogin, gridBagConstraints);

        txtPassword.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPassword.setPreferredSize(new java.awt.Dimension(150, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        jPanel5.add(txtPassword, gridBagConstraints);

        jPanel2.add(jPanel5, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        doLogin();
    }

    private void doLogin() {
        String login = txtLogin.getText();
        String password = txtPassword.getText();

        if (validation.checkLogin(login) && validation.checkPassword(password))
            try {
                int check = client.login(login, password);
                if (check == 1) {
                    UserListGUI userListGUI = new UserListGUI(client, model, allMessages, login);
                    userListGUI.setVisible(true);
                    userListGUI.setLocation(getX(), getY());
                    userListGUI.getLbLogin().setText("Login as: " + login);
                    setVisible(false);
                    model.removeElement(login);
                } else {
                    String msg = check == 0 ? "Username not exist" : check == -1 ? "Invalid password" : "Logged in";
                    JOptionPane.showMessageDialog(this, msg);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        else
            JOptionPane.showMessageDialog(this, "Invalid input");
    }

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        RegisterGUI registerGUI = new RegisterGUI(client, this);
        registerGUI.setVisible(true);
        registerGUI.setLocation(getX(), getY());
        setVisible(false);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginGUI().setVisible(true);
            }
        });
    }



    // Variables declaration - do not modify
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnRegister;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JTextField txtLogin;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration
}
