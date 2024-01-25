package practice;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;  
import java.sql.SQLException;
import java.sql.Statement;  

//contains operations to connect and create database 
public class DB {
	//database url, change path to change the database, or database will be created at path named
    private static String url = "jdbc:sqlite:C://MillerLegal/db/test.db";

    public static Connection getConnection() {  
        Connection conn = null;  
        try {  
            // create a connection to the database  
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        } finally {  
            try {  
                if (conn == null) {  
                    conn.close();  
                }  
            } catch (SQLException ex) {  
                System.out.println(ex.getMessage());  
            }  
        }  
        return conn;
    }  

    public static void createNewCaseTable() {        
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS CASE_FILE (\n"
        		+ "CASE_ID VARCHAR(255) NOT NULL UNIQUE PRIMARY KEY,\n"
        		+ "CASE_NAME VARCHAR(255) NOT NULL UNIQUE,\n"
        		+ "CASE_TYPE VARCHAR(255) NOT NULL,\n"
        		+ "CASE_CREATION_DATE DATE);";
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            System.out.println("Case table created.");  
        } catch (SQLException e) {
            System.out.println("Closed");
        }
    }
    
    public static void createNewDepositionTable() {        
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS EXHIBIT (\n"
        		+ "CASE_ID VARCHAR(255) NOT NULL,\n"
        		+ "EXHIBIT_NAME VARCHAR(255) NOT NULL UNIQUE PRIMARY KEY,\n"
        		+ "FILE_BLOB BLOB NOT NULL,\n"
        		+ "CONSTRAINT FK_CASE_ID FOREIGN KEY (CASE_ID) REFERENCES CASE_FILE(CASE_ID) ON DELETE CASCADE);";
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Deposition table created.");  
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createNewExhibitTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS DEPOSITION (\n"
        		+ "CASE_ID VARCHAR(255) NOT NULL,\n"
        		+ "DEPO_NAME VARCHAR(255) NOT NULL UNIQUE PRIMARY KEY,\n"
        		+ "FILE_BLOB BLOB NOT NULL,\n"
        		+ "CONSTRAINT FK_CASE_ID FOREIGN KEY (CASE_ID) REFERENCES CASE_FILE(CASE_ID) ON DELETE CASCADE);";
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Exhibit table created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
   
    public static void main(String[] args) {  
        getConnection();
        createNewCaseTable();
        createNewDepositionTable();
        createNewExhibitTable();
    }  
}