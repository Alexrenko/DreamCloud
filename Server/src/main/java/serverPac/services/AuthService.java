package serverPac.services;

import utils.Command;
import utils.Message;

import java.sql.*;

public class AuthService {

    //переменные для соединения с базой данных
    private static final String driverName = "com.mysql.jdbc.Driver";
    private static final String dbUrl = "jdbc:mysql://localhost:3306/clients";
    private static final String dbUser = "root";
    private static final String dbPassword = "root";

    public boolean isRegistered(String login) {
        String query = "select * from clients.creds";
        try (Connection connection = getConnectionToDB()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String loginFromDB = rs.getString(1);
                String passwordFromDB = rs.getString(2);
                if (login.equals(loginFromDB))
                    return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkAuth(String login, String password) {
        String query = "select * from clients.creds";
        try (Connection connection = getConnectionToDB()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String loginFromDB = rs.getString(1);
                String passwordFromDB = rs.getString(2);
                if (login.equals(loginFromDB) && password.equals(passwordFromDB))
                    return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getClientRoot(String login) {
        String query = "select * from clients.directories";
        try (Connection connection = getConnectionToDB()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String loginFromDB = rs.getString(1);
                String rootFromDB = rs.getString(2);
                if (login.equals(loginFromDB))
                    return rootFromDB;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void registerNewClient(String login, String password) throws SQLException {
        //добавляем нового клиента в базу данных
        String query = String.format("INSERT creds(login, password) VALUE ('%s', '%s')", login, password);
        Connection connection = getConnectionToDB();
        Statement stmt = connection.createStatement();
        int rows = stmt.executeUpdate(query);
        connection.close();
    }

    public void registerNewDirectory(String login, String root) throws SQLException {
        String query = String.format("INSERT directories(login, root) VALUE ('%s', '%s')", login, root);
        Connection connection = getConnectionToDB();
        Statement stmt = connection.createStatement();
        int rows = stmt.executeUpdate(query);
        connection.close();
    }

    private Connection getConnectionToDB() throws SQLException {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            System.out.println("Дравер MySQL не работает");
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}
