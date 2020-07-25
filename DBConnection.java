/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author WillA
 */
public class DBConnection {
    private static final String databaseName = "U06dWx";
    private static final String DB_URL = "jdbc:mysql://3.227.166.251/" + databaseName;
    private static final String username = "U06dWx";
    private static final String password = "53688737209";
    private static final String driver = "com.mysql.jdbc.Driver";
    private static User loggedInUser;
    static Connection conn;

    public static void makeConnection() throws ClassNotFoundException, SQLException{
        Class.forName(driver);
        conn = (Connection) DriverManager.getConnection(DB_URL, username, password);
    }
    
    public static void closeConnection() throws SQLException{
        conn.close();
    }
    

    public static Connection getConn() {
        return conn;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User loggedInUser) {
        DBConnection.loggedInUser = loggedInUser;
    }

    
}
