package lawson.lonchi.crossword.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import lawson.lonchi.crossword.App;
import lawson.lonchi.crossword.model.Crossword;
import lawson.lonchi.crossword.model.Database;

public class MainMenuController {
    private Stage primaryStage;
    
    @FXML
    private FlowPane gridButtonsPane;
    
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadGridButtons();
    }
    
    private void loadGridButtons() {
        try {
            Database database = new Database("jdbc:mysql://localhost:3306/base_llawsonhetch", "root", "");
            ResultSet gridsResult = database.executeQuery("SELECT * FROM GRID");
            
            while (gridsResult.next()) {
                int gridNumber = gridsResult.getInt("numero_grille");
                String gridName = gridsResult.getString("nom_grille");
                
                Button gridButton = new Button(gridName);
                gridButton.setPrefWidth(200);
                gridButton.setOnAction(event -> {
                    try {
                        Crossword crossword = loadGrid(gridNumber);
                        try {
                            App.loadCrosswordView(primaryStage, crossword);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showError("Erreur lors du chargement de la grille.");
                    }
                });
                
                gridButtonsPane.getChildren().add(gridButton);
            }
            
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des grilles disponibles.");
        }
    }
    
    @FXML
    private void handleRandomGrid() {
        try {
            Database database = new Database("jdbc:mysql://localhost:3306/base_llawsonhetch", "root", "");
            ResultSet gridsResult = database.executeQuery("SELECT * FROM GRID");
            List<Integer> gridNumbers = new ArrayList<>();
            while (gridsResult.next()) {
                gridNumbers.add(gridsResult.getInt("numero_grille"));
            }
            if (!gridNumbers.isEmpty()) {
                int randomGridNumber = gridNumbers.get(new Random().nextInt(gridNumbers.size()));
                Crossword crossword = loadGrid(randomGridNumber);
                try {
                    App.loadCrosswordView(primaryStage, crossword);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showError("Aucune grille disponible.");
            }
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des grilles disponibles.");
        }
    }
    
    @FXML
    private void handleQuit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir quitter ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                primaryStage.close();
            }
        });
    }
    
    public Crossword loadGrid(int gridNumber) throws SQLException {
        Database database = new Database("jdbc:mysql://localhost:3306/base_llawsonhetch", "root", "");
        ResultSet gridResult = database.executeQuery("SELECT * FROM GRID WHERE numero_grille = " + gridNumber);
        
        if (gridResult.next()) {
            int width = gridResult.getInt("largeur");
            int height = gridResult.getInt("hauteur");
            
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Taille de grille invalide : " + width + "x" + height);
            }
            
            Crossword crossword = new Crossword(height, width);
            loadWords(database, crossword, gridNumber);
            
            database.close();
            return crossword;
        } else {
            database.close();
            throw new IllegalArgumentException("Grille non trouvée : " + gridNumber);
        }
    }
    
    public void loadWords(Database database, Crossword crossword, int gridNumber) throws SQLException {
        ResultSet wordsResult = database.executeQuery("SELECT * FROM CROSSWORD WHERE numero_grille = " + gridNumber);
        
        while (wordsResult.next()) {
            String definition = wordsResult.getString("definition");
            boolean horizontal = wordsResult.getBoolean("horizontal");
            int row = wordsResult.getInt("ligne") - 1;
            int column = wordsResult.getInt("colonne") - 1;
            String solution = wordsResult.getString("solution");
            
            if (row >= 0 && row < crossword.getHeight() && column >= 0 && column < crossword.getWidth()) {
                crossword.setDefinition(row, column, horizontal, definition);
                
                for (int i = 0; i < solution.length(); i++) {
                    int currentRow = horizontal ? row : row + i;
                    int currentColumn = horizontal ? column + i : column;
                    
                    if (currentRow < crossword.getHeight() && currentColumn < crossword.getWidth()) {
                        char currentSolution = solution.charAt(i);
                        char existingSolution = crossword.getSolution(currentRow, currentColumn);
                        
                        if (existingSolution != ' ' && existingSolution != currentSolution) {
                            System.err.println("Conflit de solution : Ligne = " + currentRow + ", Colonne = " + currentColumn +
                            ", Solution existante = '" + existingSolution + "', Nouvelle solution = '" + currentSolution + "'");
                        }
                        
                        crossword.setSolution(currentRow, currentColumn, currentSolution);
                        System.out.println("Chargement de la solution : Ligne = " + currentRow + ", Colonne = " + currentColumn + ", Solution = '" + currentSolution + "'");
                    }
                }
            } else {
                System.err.println("Coordonnées invalides pour le mot : " + definition + " (" + row + ", " + column + ")");
            }
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
