package lawson.lonchi.crossword.controller;

import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import lawson.lonchi.crossword.model.Clue;
import lawson.lonchi.crossword.model.Crossword;

public class CrosswordController {
    @FXML
    private GridPane gridPane; 
    @FXML
    private ListView<String> horizontalClues;
    @FXML
    private ListView<String> verticalClues;
    
    private Crossword crossword;
    private int currentRow = 0; 
    private int currentColumn = 0;
    private boolean isHorizontal = true;
    private boolean isUpdatingSelection = false;
    private boolean[][] correctCells;
    
    /**
    * Définit la grille de mots croisés pour ce contrôleur.
    *
    * @param crossword La grille de mots croisés.
    */
    public void setCrossword(Crossword crossword) {
        this.crossword = crossword;
        initializeGrid();
        initializeClues();
        updateCurrentCell();
        printBlackSquares();
    }
    
    /**
    * Initialise la grille de mots croisés.
    */
   

    private void initializeGrid() {
        for (int i = 0; i < crossword.getHeight(); i++) {
            for (int j = 0; j < crossword.getWidth(); j++) {
                Label label = new Label();
                label.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
                label.setPrefSize(30, 30);
                label.setStyle("-fx-border-color: black; -fx-alignment: center;");
    
                char solution = crossword.getSolution(i, j);
    
                // Vérifier si la case doit être noire
                if (crossword.isBlackSquare(i, j)) {
                    label.setStyle("-fx-background-color: black; -fx-border-color: black;");
                    label.setText(""); // Pas de texte dans les cases noires
                    System.out.println("Case noire : Ligne = " + i + ", Colonne = " + j);
                } else {
                    // Case blanche : afficher la proposition en majuscules
                    StringProperty propositionProperty = crossword.propositionProperty(i, j);
                    label.textProperty().bind(Bindings.createStringBinding(
                        () -> propositionProperty.get().toUpperCase(),
                        propositionProperty
                    ));
    
                    // Ajouter un écouteur pour garder les cases correctes en vert
                    propositionProperty.addListener((obs, oldVal, newVal) -> {
                        if (!newVal.isEmpty() && newVal.charAt(0) == solution) {
                            label.setStyle("-fx-background-color: green; -fx-border-color: black; -fx-alignment: center;");
                        } else {
                            label.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-alignment: center;");
                        }
                    });
    
                    // Ajouter un écouteur de clic pour la sélection
                    final int row = i;
                    final int column = j;
                    label.setOnMouseClicked(event -> handleMouseClick(event, row, column));
                }
    
                // Ajouter la case à la grille
                gridPane.add(label, j, i);
            }
        }

    
        gridPane.setOnKeyPressed(this::handleKeyPress);
        gridPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.W) {
                ((Stage) gridPane.getScene().getWindow()).close();
            }
        });
    
        gridPane.setFocusTraversable(true);
        gridPane.requestFocus();
    }

    
    
    /**
    * Initialise les indices horizontaux et verticaux.
    */
    private void initializeClues() {
        // Charger les indices horizontaux
        ObservableList<String> horizontalCluesList = FXCollections.observableArrayList();
        for (Clue clue : crossword.getHorizontalClues()) {
            horizontalCluesList.add(clue.getClue());
        }
        horizontalClues.setItems(horizontalCluesList);
        
        // Charger les indices verticaux
        ObservableList<String> verticalCluesList = FXCollections.observableArrayList();
        for (Clue clue : crossword.getVerticalClues()) {
            verticalCluesList.add(clue.getClue());
        }
        verticalClues.setItems(verticalCluesList);
        
        // Ajouter les écouteurs pour la sélection des indices
        horizontalClues.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !isUpdatingSelection) {
                selectClue(newVal, true);
            }
        });
        
        verticalClues.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !isUpdatingSelection) {
                selectClue(newVal, false);
            }
        });
    }
    
    /**
    * Gère le clic de la souris sur une case de la grille.
    *
    * @param event L'événement souris.
    * @param row   La ligne de la case cliquée.
    * @param column La colonne de la case cliquée.
    */
    public void handleMouseClick(MouseEvent event, int row, int column) {
        if (!crossword.isBlackSquare(row, column)) {
            currentRow = row;
            currentColumn = column;
            updateCurrentCell();
            updateClueSelection();
        }
    }
    
    /**
    * Gère l'appui sur une touche du clavier.
    *
    * @param event L'événement clavier.
    */
    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode().isLetterKey()) {
            // Saisie d'une lettre : convertir en minuscule
            char letter = event.getText().toLowerCase().charAt(0); // Convertir en minuscule
            crossword.setProposition(currentRow, currentColumn, letter); // Stocker en minuscule
            moveToNextCell();
            animateLabel((Label) gridPane.getChildren().get(currentRow * crossword.getWidth() + currentColumn));
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            // Effacer une lettre
            crossword.setProposition(currentRow, currentColumn, ' ');
            moveToPreviousCell();
        } else if (event.getCode() == KeyCode.ENTER) {
            // Vérifier les réponses
            checkAnswers();
        } else if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT ||
        event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
            // Navigation avec les flèches
            handleArrowKey(event.getCode());
        }
    }
    /**
    * Déplace la case courante vers la cellule suivante.
    */
    private void moveToNextCell() {
        if (isHorizontal) {
            moveToCell(currentRow, currentColumn + 1);
        } else {
            moveToCell(currentRow + 1, currentColumn);
        }
    }
    
    /**
    * Déplace la case courante vers la cellule précédente.
    */
    private void moveToPreviousCell() {
        if (isHorizontal) {
            moveToCell(currentRow, currentColumn - 1);
        } else {
            moveToCell(currentRow - 1, currentColumn);
        }
    }
    
    /**
    * Déplace la case courante à une cellule spécifique.
    *
    * @param row    La ligne de la cellule cible.
    * @param column La colonne de la cellule cible.
    */
    private void moveToCell(int row, int column) {
        if (row >= 0 && row < crossword.getHeight() && column >= 0 && column < crossword.getWidth()) {
            if (!crossword.isBlackSquare(row, column)) {
                currentRow = row;
                currentColumn = column;
                updateCurrentCell();
            }
        }
    }
    
    /**
    * Met à jour la case courante dans la vue.
    */
    private void updateCurrentCell() {
        for (int i = 0; i < crossword.getHeight(); i++) {
            for (int j = 0; j < crossword.getWidth(); j++) {
                Label label = (Label) gridPane.getChildren().get(i * crossword.getWidth() + j);
                if (i == currentRow && j == currentColumn) {
                    // Case courante
                    label.setStyle("-fx-border-color: blue; -fx-alignment: center;");
                    label.requestFocus(); // Donner le focus à la case courante
                } else if (!crossword.isBlackSquare(i, j)) {
                    // Case blanche
                    label.setStyle("-fx-border-color: black; -fx-alignment: center;");
                }
            }
        }
    }
    
    /**
    * Anime le label lors de la saisie d'une lettre.
    *
    * @param label Le label à animer.
    */
    private void animateLabel(Label label) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), label);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.play();
    }
    
    /**
    * Vérifie les réponses et met en surbrillance les cases correctes.
    */
    private void checkAnswers() {
        boolean allCorrect = true; // Flag pour vérifier si toutes les cases sont correctes
        
        for (int i = 0; i < crossword.getHeight(); i++) {
            for (int j = 0; j < crossword.getWidth(); j++) {
                Label label = (Label) gridPane.getChildren().get(i * crossword.getWidth() + j);
                
                char proposition = crossword.getProposition(i, j); // Proposition de l'utilisateur
                char solution = crossword.getSolution(i, j);
                
                // Vérifier si la case est noire ou vide
                if (crossword.isBlackSquare(i, j) || solution == ' ') {
                    // Case noire ou vide : ne pas vérifier la réponse
                    label.setStyle("-fx-background-color: black; -fx-border-color: black;");
                } else {
                    // Case blanche : vérifier la réponse
                    if (proposition == solution) {
                        // Case correcte : colorer en vert
                        label.setStyle("-fx-background-color: green; -fx-border-color: black; -fx-alignment: center;");
                    } else {
                        // Case incorrecte : colorer en rouge (ou une autre couleur)
                        label.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-alignment: center;");
                        allCorrect = false; // Au moins une case est incorrecte
                    }
                }
            }
        }
        
        // Afficher un message de succès ou d'échec dans la console
        if (allCorrect) {
            System.out.println("Félicitations ! Toutes les réponses sont correctes.");
        } else {
            System.out.println("Il y a des erreurs. Vérifie tes réponses.");
        }
    }
    
    /**
    * Sélectionne un indice et met à jour la case courante.
    *
    * @param clueText     L'indice sélectionné.
    * @param isHorizontal Si l'indice est horizontal.
    */
    private void selectClue(String clueText, boolean isHorizontal) {
        if (isUpdatingSelection) {
            return; // Éviter les appels récursifs
        }
        
        isUpdatingSelection = true;
        
        for (Clue clue : isHorizontal ? crossword.getHorizontalClues() : crossword.getVerticalClues()) {
            if (clue.getClue().equals(clueText)) {
                currentRow = clue.getRow();
                currentColumn = clue.getColumn();
                updateCurrentCell();
                updateClueSelection();
                break;
            }
        }
        
        isUpdatingSelection = false;
    }
    
    /**
    * Met à jour la sélection des indices dans les ListView.
    */
    private void updateClueSelection() {
        if (isUpdatingSelection) {
            return; // Éviter les appels récursifs
        }
        
        isUpdatingSelection = true;
        
        horizontalClues.getSelectionModel().clearSelection();
        verticalClues.getSelectionModel().clearSelection();
        
        for (Clue clue : crossword.getHorizontalClues()) {
            if (clue.getRow() == currentRow && clue.getColumn() <= currentColumn &&
            clue.getColumn() + clue.getClue().length() > currentColumn) {
                horizontalClues.getSelectionModel().select(clue.getClue());
                horizontalClues.setStyle("-fx-text-fill: red;"); // Mettre en rouge
                break;
            }
        }
        
        for (Clue clue : crossword.getVerticalClues()) {
            if (clue.getColumn() == currentColumn && clue.getRow() <= currentRow &&
            clue.getRow() + clue.getClue().length() > currentRow) {
                verticalClues.getSelectionModel().select(clue.getClue());
                verticalClues.setStyle("-fx-text-fill: black;"); // Remettre en noir
                break;
            }
        }
        
        isUpdatingSelection = false;
    }
    /**
    * Gère les touches de direction (flèches).
    *
    * @param keyCode Le code de la touche de direction.
    */
    private void handleArrowKey(KeyCode keyCode) {
        switch (keyCode) {
            case LEFT:
            moveToCell(currentRow, currentColumn - 1);
            isHorizontal = true;
            break;
            case RIGHT:
            moveToCell(currentRow, currentColumn + 1);
            isHorizontal = true;
            break;
            case UP:
            moveToCell(currentRow - 1, currentColumn);
            isHorizontal = false;
            break;
            case DOWN:
            moveToCell(currentRow + 1, currentColumn);
            isHorizontal = false;
            break;
        }
    }
    
    public void printBlackSquares() {
        System.out.println("Cases noires :");
        for (int i = 0; i < crossword.getHeight(); i++) {
            for (int j = 0; j < crossword.getWidth(); j++) {
                if (crossword.isBlackSquare(i, j)) {
                    System.out.println("Case (" + i + ", " + j + ") est noire.");
                }
            }
        }
    }
    
    private void updateCellStyle(int row, int column) {
        Label label = (Label) gridPane.getChildren().get(row * crossword.getWidth() + column);
        char solution = crossword.getSolution(row, column);
        char proposition = crossword.getProposition(row, column);
        
        // Vérifier si la case est noire ou vide
        if (crossword.isBlackSquare(row, column) || solution == ' ') {
            // Case noire ou vide : fond noir
            label.setStyle("-fx-background-color: black; -fx-border-color: black;");
        } else {
            // Case blanche : vérifier si elle est correcte
            if (proposition == solution) {
                // Case correcte : fond vert
                label.setStyle("-fx-background-color: green; -fx-border-color: black; -fx-alignment: center;");
            } else if (proposition != ' ') {
                // Case incorrecte : fond rouge
                label.setStyle("-fx-background-color: red; -fx-border-color: black; -fx-alignment: center;");
            } else {
                // Case vide : fond blanc
                label.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-alignment: center;");
            }
        }
    }
}