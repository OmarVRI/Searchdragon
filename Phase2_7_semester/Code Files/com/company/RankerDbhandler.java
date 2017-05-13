package com.company;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by maverick on 07/05/17.
 */

public class RankerDbhandler {
    private static String jdbcDriver = "com.mysql.jdbc.Driver";
    private static String dbAddress = "jdbc:mysql://localhost:3306/";
    private static String dbName = "testdata";
    private static String userName = "root";
    private static String password = "root";

    private static Connection con;
    private double[] result;
    private int N = 5010;
    private double K = 100;

    public RankerDbhandler() {
        result = new double[N];

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

    public void createTableTF() {
        String query = "create table TF("
                + "word varchar(250) not null, "
                + "docId INT not null, "
                + "val double not null, "
                + "primary key (word, docId))";
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            Statement s = con.createStatement();
            s.executeUpdate(query);
            System.out.println("Table Created");
            con.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
        }
    }

    public void createTableIDF() {
        String query = "create table IDF("
                + "word varchar(250) not null, "
                + "val double not null, "
                + "primary key (word))";
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            Statement s = con.createStatement();
            s.executeUpdate(query);
            System.out.println("Table Created");
            con.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
        }
    }

    public void calcTF(String str) {
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "SELECT docId FROM Indexer where word = ? ";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, str);

            ResultSet rs = preparedStmt.executeQuery();

            while (rs.next()) {
                int docId = rs.getInt("docId");
                insertIntoTableTF(str, docId);
                updateResult(str, docId, getTFVal(docId, str));
            }

            preparedStmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private double getTFVal(int docId, String str) {
        double ret = 0;
        try {
            String query = "SELECT val from TF where docId = ? and word = ?";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setInt(1, docId);
            preparedStmt.setString(2, str);

            ResultSet rs = preparedStmt.executeQuery();
            rs.next();
            ret = rs.getDouble("val");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ret;
    }

    private int nTermsinD(int docId) {
        int ret = 0;
        try {
            String query = "SELECT count(*) AS cnt from Indexer where docId = ?";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setInt(1, docId);

            ResultSet rs = preparedStmt.executeQuery();
            rs.next();
            ret = rs.getInt("cnt");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ret;
    }

    private int tFreqinD(String str, int docId) {
        int frq = 0;
        try {
            String query = "SELECT positions from Indexer where word = ? and docId = ?";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, str);
            preparedStmt.setInt(2, docId);

            ResultSet rs = preparedStmt.executeQuery();
            rs.next();

            String pos = rs.getString("positions");
            frq = str.length() - str.replace(" ", "").length() + 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return frq;
    }

    private void insertIntoTableTF(String str, int docId) {
        try {

            String query = "SELECT count(*) AS cnt from TF where word = ? and docId = ? ";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, str);
            preparedStmt.setInt(2, docId);

            ResultSet rs = preparedStmt.executeQuery();
            rs.next();
            int cnt = rs.getInt("cnt");
            if (cnt != 0) return;
            int nTD = nTermsinD(docId);
            int frq = tFreqinD(str, docId);

            query = "insert into TF(word, docId, val)"
                    + "values(?, ?, ?)";

            preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, str);
            preparedStmt.setInt(2, docId);
            preparedStmt.setDouble(3, (double) (frq) / (double) (nTD));

            preparedStmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateResult(String str, int docId, double val) {
        try {
            String query = "SELECT val from IDF where word = ?";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, str);

            ResultSet rs = preparedStmt.executeQuery();
            rs.next();
            double IDF = rs.getDouble("val");
            result[docId] += IDF * val;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void calcIDF(String str) {
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "SELECT count(*) AS cnt FROM Indexer where word = ? ";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, str);

            ResultSet rs = preparedStmt.executeQuery();
            rs.next();
            int cnt = rs.getInt("cnt");
            insertIntoTableIDF(str, cnt);

            preparedStmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private double log(double X) {
        return Math.log(X);
    }

    private void insertIntoTableIDF(String str, int cnt) {
        double val = log((double) (N) / (double) (1 + cnt));
        try {
            String query = "INSERT into IDF (word, val)"
                    + "values(?, ?)";

            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, str);
            preparedStmt.setDouble(2, val);

            preparedStmt.execute();
        } catch (SQLException e) {
            try {
                String query = "UPDATE IDF SET val = ? where word = ?";

                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setDouble(1, val);
                preparedStmt.setString(2, str);

                preparedStmt.execute();
            } catch (SQLException e2) {
                System.out.println(e2.getMessage());
            }
        }
    }

    public void addPopularity() {
        for (int i = 1; i < N; ++i) {
            int inDeg = getInDeg(i);
            result[i] = K * result[i] + log(1 + inDeg);
        }
    }

    private int getInDeg(int docId) {
        int ret = 0;
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "SELECT InDegree FROM Sites where id = ? ";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setInt(1, docId);

            ResultSet rs = preparedStmt.executeQuery();
            if (rs.next())
                ret = rs.getInt("InDegree");
            preparedStmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int[] getDocs(String str) {
        int[] ret = new int[1];
        int cnt = 0;
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "SELECT count(*) AS cnt FROM Indexer where word = ? ";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, str);

            ResultSet rs = preparedStmt.executeQuery();
            rs.next();
            cnt = rs.getInt("cnt");
            ret = new int[cnt];
            cnt = 0;

            query = "SELECT docId FROM Indexer where word = ?";
            preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, str);

            rs = preparedStmt.executeQuery();
            while (rs.next())
                ret[cnt++] = rs.getInt("docId");
            preparedStmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public String getPositionsString(String str, int docId) {
        String ret = "";

        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "SELECT positions FROM Indexer where word = ? and docId = ?";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, str);
            preparedStmt.setInt(2, docId);

            ResultSet rs = preparedStmt.executeQuery();
            rs.next();

            ret = rs.getString("positions");

            preparedStmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void updateResult(int docId) {
        ++result[docId];
    }

    public void divideResult(int docId) {
        double x = rowCnt(docId);
        if (x != 0)
            result[docId] /= x;
    }

    private int rowCnt(int docId) {
        int ret = 0;

        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "SELECT count(*) AS cnt FROM Indexer where docId = ?";
            PreparedStatement preparedStmt = con.prepareStatement(query);

            preparedStmt.setInt(1, docId);

            ResultSet rs = preparedStmt.executeQuery();
            rs.next();

            ret = rs.getInt("cnt");

            preparedStmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public double[] getResult() {
        return result;
    }

}
