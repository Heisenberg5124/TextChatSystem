package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectDB implements DatabaseInfo {
    public static Connection getConnection() {
        Connection connection = null;

        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(dbURL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }
}
