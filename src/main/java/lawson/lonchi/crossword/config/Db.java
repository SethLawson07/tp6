package lawson.lonchi.crossword.config;

import java.sql.*;
import java.util.*;

public class Db {
    
    private Connection connexion;

    public Db(){
        try {
            connexion = connecterBD();
            if (connexion != null){
                System.out.println("Successfully connected to the database!");
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            e.printStackTrace();
        } 
    
    }
    

    public static Connection connecterBD() throws SQLException {

        String url = "jdbc:mysql://127.0.0.1:3306/base_llawsonhetch"; 
        // String user = "user_llawsonhetch"; 
        String user = "root"; 
        // String password = "N[Srr(42*7Cwy7";
        String password = "";

        Connection connect = null;

        connect = DriverManager.getConnection(url, user, password);

        return connect;
    }

    public void closeConnection(){
        if (connexion != null) {
            try {
                connexion.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing the connection: " + e.getMessage());
            }
        }
        connexion = null;
    }


public Map<Integer, String> availableGrids() throws SQLException { 

    Map<Integer, String> gridMap = new HashMap<>(); 

    try (Statement statement = connexion.createStatement();  
         ResultSet resultSet = statement.executeQuery("SELECT * FROM grid")) {

        while (resultSet.next()) {
            int numero_grille = resultSet.getInt("numero_grille"); 
            String nom_grille = resultSet.getString("nom_grille"); 
            int width = resultSet.getInt("largeur");
            int height = resultSet.getInt("hauteur");
                 
            String value = nom_grille+" ("+height+"x"+width+") ";
            gridMap.put(numero_grille, value); 
            
        }

    }  catch (SQLException e) {
        System.out.println("Error"+e.getMessage());
    } 

    return gridMap; 
}
    // public Crossword extractGrid(int numGrille)  throws SQLException{
    //     try (Statement statement = connexion.createStatement();  
    //      ResultSet resultSet = statement.executeQuery("SELECT * FROM crossword")) {

    //     while (resultSet.next()) {
    //         int solution = resultSet.getInt("solution"); 
    //         String proposition = resultSet.getString("proposition"); 
    //         int ligne = resultSet.getInt("ligne");
    //         int colonne = resultSet.getInt("colonne");
                 
    //         String value = nom_grille+" ("+height+"x"+width+") ";
    //         gridMap.put(numero_grille, value); 
            
    //     }

    // }  catch (SQLException e) {
    //     System.out.println("Error"+e.getMessage());
    // } 
    // }
}
