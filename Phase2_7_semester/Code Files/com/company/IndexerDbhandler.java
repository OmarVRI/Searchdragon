package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class IndexerDbhandler {
    private static String jdbcDriver = "com.mysql.jdbc.Driver";
    private static String dbAddress = "jdbc:mysql://localhost:3306/";
    private static String dbName = "SearchEngine";
    private static String userName = "root";
    private static String password = "root";

    private static Connection con;

    IndexerDbhandler() {

        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection(dbAddress + "?useSSL=false", userName, password);
            Statement s = con.createStatement();
            s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        String myTableName = "CREATE TABLE Indexer ("
                + "word VARCHAR(250) NOT NULL , "
                + "positions TEXT NOT NULL , "
                + "updated bool default false , "
                + "docId INT NOT NULL, "
                + "PRIMARY KEY( word , docId ) )";
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            Statement s = con.createStatement();
            s.executeUpdate(myTableName);
            System.out.println("Table Created");
            con.close();

        } catch (SQLException e) {
            System.out.println("An error has occured on Table Creation, It was created before");
        } catch (ClassNotFoundException e) {
        }
    }


    public void addContent(String word, int docId, String pos) {
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = " insert into Indexer (word , docId , positions , updated )"
                    + " values (? , ? , ? , ? )";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, word);
            preparedStmt.setInt(2, docId);
            preparedStmt.setString(3, pos);
            preparedStmt.setBoolean(4, true);


            try {
                preparedStmt.executeUpdate();
                preparedStmt.close();
                con.close();
            } catch (SQLException e) {
                e.getStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public String isFound(String word, int docId) {


        String positionPath = "";

        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "SELECT positions FROM Indexer where word = ? and docId = ? ";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, word);
            preparedStmt.setInt(2, docId);

            ResultSet rs = preparedStmt.executeQuery();

            while (rs.next()) {
                positionPath = rs.getString("positions");
            }

            preparedStmt.close();
            con.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return positionPath;
    }

    public void deleteNotUpdated() {

        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "delete from Indexer where updated=false";
            PreparedStatement preparedStmt = con.prepareStatement(query);


            try {
                preparedStmt.executeUpdate();

            } catch (SQLException e) {
                e.getStackTrace();
            }
            preparedStmt.close();
            con.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void setUpdated(String word, int docId) {
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "update Indexer set  updated = TRUE where word = ? and docId = ? ";

            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, word);
            preparedStmt.setInt(2, docId);
            try {
                preparedStmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            con.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void update(String word, int docId, String positions) {
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "update Indexer set  positions = ? , updated = ? where word = ? and docId = ? ";

            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, positions);
            preparedStmt.setBoolean(2, true);
            preparedStmt.setString(3, word);
            preparedStmt.setInt(4, docId);

            try {
                preparedStmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            con.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void setNotUpdated() {

        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "update Indexer set  updated = False ";

            PreparedStatement preparedStmt = con.prepareStatement(query);

            try {
                preparedStmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            con.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


}
