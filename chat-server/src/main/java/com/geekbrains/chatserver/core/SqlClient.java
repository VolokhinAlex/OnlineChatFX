package com.geekbrains.chatserver.core;

import org.sqlite.JDBC;

import java.sql.*;

public class SqlClient {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(JDBC.PREFIX + "chat-server/clients.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getNickname(String login, String password) {
        String query = String.format("select nickname from clients where login='%s' and password='%s'",
                login, password);
        try (ResultSet set = statement.executeQuery(query)) {
            if (set.next()) {
                return set.getString("nickname");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static String registration(String login, String password, String nickname) {
        String query = String.format("INSERT INTO clients (login, password, nickname) VALUES(\"%s\", \"%s\", \"%s\")", login, password, nickname);
        try {
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Integer getId(String login, String password, String nickname) {
        try {
            preparedStatement = connection.prepareStatement("select id from clients where login=? and password=? and nickname=?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, nickname);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static boolean changeNickname(String oldNickname, String newNickname) {
        try {
            preparedStatement = connection.prepareStatement("update clients SET nickname=? where nickname=?");
            preparedStatement.setString(1, newNickname);
            preparedStatement.setString(2, oldNickname);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void disconnect() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
