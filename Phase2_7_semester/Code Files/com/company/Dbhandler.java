package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;


public class Dbhandler {
    private static String jdbcDriver = "com.mysql.jdbc.Driver";
    private static String dbAddress = "jdbc:mysql://localhost:3306/";
    private static String dbName = "testdata";
    private static String userName = "root";
    private static String password = "root";

    private static Connection con;

    public Dbhandler() {
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

    public void createTable() {   //ds
        String myTableName = "CREATE TABLE Sites ("
                + "id INT(64) NOT NULL AUTO_INCREMENT,"
                + "link varchar(2500) NOT NULL,"
                + "downloaded BOOL,"
                + "contentFile VARCHAR(2500) DEFAULT NULL,"
                + "indexed bool default false , "
                + "inDegree INT(64) NOT NULL default 0,"
                + "PRIMARY KEY(id),"
                + "UNIQUE (link))";
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            Statement s = con.createStatement();
            s.executeUpdate(myTableName);
            System.out.println("Table Created");
            con.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
        }
    }

    public void addSite(String urlString, int notSeed) throws ClassNotFoundException, SQLException {
        Class.forName(jdbcDriver);
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
        String query = " insert into Sites (link, downloaded, inDegree)"
                + " values (?, ?, ?)";

        // create the mysql insert preparedstatement
        PreparedStatement preparedStmt = con.prepareStatement(query);
        preparedStmt.setString(1, urlString);
        preparedStmt.setBoolean(2, false);
        preparedStmt.setLong(3, notSeed);
        try {
            preparedStmt.execute();
        } catch (SQLException e) {
            //   e.printStackTrace();
            //  System.out.println("The URL is already here.");
            //////////////////////////////////////////////////////////////////////////////
            try {
                String q2 = " select inDegree from Sites where link = ? ";
                preparedStmt = con.prepareStatement(q2);
                preparedStmt.setString(1, urlString);
                ResultSet rs = preparedStmt.executeQuery();
                long indeg = 0;
                while (rs.next()) {
                    indeg = rs.getLong("inDegree");
                }
                q2 = "update Sites set inDegree = ? where link = ?";
                preparedStmt = con.prepareStatement(q2);
                preparedStmt.setLong(1, indeg + 1);
                preparedStmt.setString(2, urlString);
                preparedStmt.execute();
                System.out.println("URL UPDATED. (DBHANDLER LINE 89)");
            } catch (SQLException e2) {
                System.out.println("The URL doesn't exist. (DBHANDLER LINE 89)");
            }
            ////////////////////////////////////////////////////////////////////////////////
        }
        con.close();
    }

    public ArrayList<String> getNotDownloaded() throws SQLException, ClassNotFoundException {
        ArrayList<String> notDown = new ArrayList<String>();
        Class.forName(jdbcDriver);
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
        String query = "SELECT * FROM Sites WHERE downloaded = FALSE ORDER BY id ASC";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            String url = rs.getString("link");
            notDown.add(url);
        }
        st.close();
        con.close();
        return notDown;
    }

    public ArrayList<String> getAll() throws SQLException, ClassNotFoundException {
        ArrayList<String> all = new ArrayList<String>();
        Class.forName(jdbcDriver);
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
        String query = "SELECT * FROM Sites ORDER BY id ASC";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            String url = rs.getString("link");
            all.add(url);
        }
        st.close();
        con.close();
        return all;
    }

    public long getID(String url) {
        long id = -1;
        try {
            Class.forName(jdbcDriver);
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
                PreparedStatement statement = con.prepareStatement("select id from Sites where link = ?");
                statement.setString(1, url);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    id = rs.getLong("id");
                }
                statement.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        return id;
    }

    public void addContent(String urlString, String content) throws ClassNotFoundException, SQLException {
        Class.forName(jdbcDriver);
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
        String query = "update Sites set contentFile = ? , downloaded = TRUE where link = ?";
        //   String query = "update Sites set contentFile = ?  where link = ?";
        PreparedStatement preparedStmt = con.prepareStatement(query);
        preparedStmt.setString(1, content);
        preparedStmt.setString(2, urlString);
        try {
            preparedStmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("There is no such Url in db");
        }
        con.close();

    }

    public int getDownloadedCount() throws SQLException, ClassNotFoundException {
        Class.forName(jdbcDriver);
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
        String query = "SELECT COUNT(*) FROM Sites WHERE downloaded = True";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);
        rs.next();
        int x = rs.getInt(1);
        st.close();

        return x;
    }

    public void deleteLink(String url) {


        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "Delete from Sites where link = ? ";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, url);

            preparedStmt.executeUpdate();
            ;
            preparedStmt.close();
            con.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getCount() {
        long count = 0;
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "select count(*) from Sites";
            PreparedStatement preparedStmt = con.prepareStatement(query);


            ResultSet rs = preparedStmt.executeQuery();
            while (rs.next()) {
                count = rs.getLong(1);
            }
            preparedStmt.close();
            con.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return count;
    }

    public Vector<pair<Integer, String>> getNotIndexed() {
        Vector<pair<Integer, String>> all = new Vector<pair<Integer, String>>();

        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            Statement stmt = con.createStatement();
            ResultSet result = stmt.executeQuery("SELECT id , contentFile FROM Sites where indexed = false ");

            while (result.next()) {
                all.add(new pair(result.getInt("id"), result.getString("contentFile")));
            }
            con.close();
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return all;

    }

    public void setIndexed(int docId) {
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "update Sites set  indexed = True where id = ? ";

            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setInt(1, docId);
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

    public void setNotIndexed() {
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "update Sites set  indexed = false ";

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

    public void setNotDownloaded() //
    {
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "update Sites set  downloaded = false ";

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

    public String getURL(int x) {
        String ret = "";
        try {
            Class.forName(jdbcDriver);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdata?useSSL=false", userName, password);
            String query = "SELECT link FROM Sites where id = ? ";

            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setInt(1, x);
            try {
                ResultSet R = preparedStmt.executeQuery();
                R.next();
                ret = R.getString("link");

            } catch (SQLException e) {
                e.printStackTrace();
            }
            con.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
