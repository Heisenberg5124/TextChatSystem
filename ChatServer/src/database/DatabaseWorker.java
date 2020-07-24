package database;

import model.Message;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseWorker {
    public boolean isOnline(String username) {
        try {
            Connection connection = ConnectDB.getConnection();
            String sql = "select online from [User] where [User].username = '" + username + "'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next())
                return resultSet.getBoolean(1);

            statement.close();
            resultSet.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public void setOnline(String username, boolean online) {
        try {
            Connection connection = ConnectDB.getConnection();
            String sql = "update [User] set online = ? where username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBoolean(1, online);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public HashMap<String, String> getAllUsers() {
        HashMap<String, String> users = new HashMap<>();
        try {
            Connection connection = ConnectDB.getConnection();
            String sql = "SELECT * FROM [User]";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String username = resultSet.getString(1);
                String password = resultSet.getString(2);

                users.put(username, password);
            }

            statement.close();
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public boolean insertUser(User user) {
        boolean result = true;

        try {
            Connection connection = ConnectDB.getConnection();
            String sql = "insert into [User] (username, password, fullname) values (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getFullname());
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException throwables) {
            if (isConstraintViolation(throwables))
                System.err.println("Username " + user.getUsername() + " exists");
            result = false;
        }

        return result;
    }

    public boolean isConstraintViolation(SQLException exception) {
        return exception.getSQLState().startsWith("23");
    }

    public void insertMessage(Message message) {
        try {
            String sql = "exec InsertMessage ?, ?, ?, ?";
            Connection connection = ConnectDB.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);
            callableStatement.setString(1, message.getSender());
            callableStatement.setString(2, message.getRecipient());
            callableStatement.setString(3, message.getBody());
            callableStatement.setBoolean(4, message.isRead());
            callableStatement.executeUpdate();

            callableStatement.close();
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public ArrayList<String> getAllMessages(String sender) {
        ArrayList<String> allMessages = new ArrayList<>();
        try {
            Connection connection = ConnectDB.getConnection();
            String sql = "SELECT * FROM [Message]\n" +
                    "where (sender = ? or recipient = ?)\n" +
                    "order by create_time";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, sender);
            statement.setString(2, sender);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String send = resultSet.getString(2);
                String receive = resultSet.getString(3);
                String body = resultSet.getString(4);
                String createTime = resultSet.getString(5);
                boolean isRead = resultSet.getBoolean(6);
                Message message = new Message(send, receive, body, createTime, isRead);

                allMessages.add(message.toString());
            }

            statement.close();
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allMessages;
    }

    public void updateMessages(String sender, String recipient) {
        try {
            Connection connection = ConnectDB.getConnection();
            String sql = "update Message set is_read = 1 where sender = ? and recipient = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, recipient);
            preparedStatement.setString(2, sender);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
