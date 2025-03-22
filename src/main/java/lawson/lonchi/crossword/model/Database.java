package lawson.lonchi.crossword.model;

import java.sql.*;

public class Database {
    private Connection connection;

    public Database(String url, String user, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}