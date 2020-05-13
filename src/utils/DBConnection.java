/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Fred
 */
public class DBConnection {
    
    //Connection URL info
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//3.227.166.251/U03HGt";
    
    private static final String URL = protocol + vendorName + ipAddress;
    
    private static final String MYSQLDriver = "com.mysql.jdbc.Driver";
    private static Connection conn = null;
    
    private static final String username = "U03HGt";
    private static final String password = "53687977393";
    
    public static Connection startConnection()
    {
        try{
           
            Class.forName(MYSQLDriver);
            conn = (Connection)DriverManager.getConnection(URL, username, password);
            System.out.println("Connection Successful");
         }
        catch(ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        
        return conn;
        
    }
    
    public static void closeConnection()
    {
        try
        {
            conn.close();
            System.out.println("connection closed");
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
}
