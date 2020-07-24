package database;

public interface DatabaseInfo {
    String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    String dbURL = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=TextChatSystem;integratedSecurity=true";
}
