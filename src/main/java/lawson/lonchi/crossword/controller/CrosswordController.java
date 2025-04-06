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


    public void setCrossword(Crossword crossword) {
        this.crossword = crossword;
        initializeGrid();
        initializeClues();
        updateCurrentCell();
        printBlackSquares();
    }

    private void initializeGrid() {
        for (int i = 0; i < crossword.getHeight(); i++) {
            for (int j = 0; j < crossword.getWidth(); j++) {
                Label label = new Label();
                label.setFocusTraversable(true);
                label.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
                label.setPrefSize(30, 30);
                label.setStyle("-fx-border-color: black; -fx-alignment: center;");

                char solution = crossword.getSolution(i, j);

                if (crossword.isBlackSquare(i, j)) {
                    label.setStyle("-fx-background-color: black; -fx-border-color: black;");
                    label.setText("");
                } else {
                    StringProperty propositionProperty = crossword.propositionProperty(i, j);
                    label.textProperty().bind(Bindings.createStringBinding(
                        () -> propositionProperty.get().toUpperCase(),
                        propositionProperty
                    ));

                    propositionProperty.addListener((obs, oldVal, newVal) -> {
                        if (!newVal.isEmpty() && newVal.charAt(0) == solution) {
                            label.setStyle("-fx-background-color: green; -fx-border-color: black; -fx-alignment: center;");
                        } else {
                            label.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-alignment: center;");
                        }
                    });

                    final int row = i;
                    final int column = j;
                    label.setOnMouseClicked(event -> handleMouseClick(event, row, column));
                }

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

    private void initializeClues() {
        ObservableList<String> horizontalCluesList = FXCollections.observableArrayList();
        for (Clue clue : crossword.getHorizontalClues()) {
            horizontalCluesList.add(clue.getClue());
        }
        horizontalClues.setItems(horizontalCluesList);

        ObservableList<String> verticalCluesList = FXCollections.observableArrayList();
        for (Clue clue : crossword.getVerticalClues()) {
            verticalCluesList.add(clue.getClue());
        }
        verticalClues.setItems(verticalCluesList);

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
     * Gère le clic sur une case non noire de la grille.
     *
     * @param event  L'événement souris.
     * @param row    Ligne de la case.
     * @param column Colonne de la case.
     */
    public void handleMouseClick(MouseEvent event, int row, int column) {
        if (!crossword.isBlackSquare(row, column)) {
            currentRow = row;
            currentColumn = column;
            updateCurrentCell();
            updateClueSelection();

            Label label = (Label) gridPane.getChildren().get(row * crossword.getWidth() + column);
            label.requestFocus();
        }
    }

    /**
     * Gère les événements clavier sur la grille.
     *
     * @param event L'événement clavier.
     */
    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode().isLetterKey()) {
            char letter = event.getText().toLowerCase().charAt(0);
            crossword.setProposition(currentRow, currentColumn, letter);
            if (correctCells != null) {
                correctCells[currentRow][currentColumn] = false;
            }
            moveToNextCell();
            animateLabel((Label) gridPane.getChildren().get(currentRow * crossword.getWidth() + currentColumn));
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            crossword.setProposition(currentRow, currentColumn, ' ');
            moveToPreviousCell();
        } else if (event.getCode() == KeyCode.ENTER) {
            checkAnswers();
        } else if (event.getCode().isArrowKey()) {
            handleArrowKey(event.getCode());
        }
    }

    /**
     * Anime une case lors de la saisie.
     *
     * @param label Le label à animer.
     */
    private void animateLabel(Label label) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), label);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    /**
     * Vérifie les propositions utilisateur par rapport aux solutions.
     */
    private void checkAnswers() {
        correctCells = new boolean[crossword.getHeight()][crossword.getWidth()];

        for (int i = 0; i < crossword.getHeight(); i++) {
            for (int j = 0; j < crossword.getWidth(); j++) {
                if (!crossword.isBlackSquare(i, j)) {
                    char proposition = crossword.getProposition(i, j);
                    char solution = crossword.getSolution(i, j);
                    correctCells[i][j] = (proposition == solution && proposition != ' ');
                    updateCellStyle(i, j);
                }
            }
        }
    }

    /**
     * Met à jour l'apparence d'une case en fonction de son état.
     *
     * @param row    Ligne de la case.
     * @param column Colonne de la case.
     */
    private void updateCellStyle(int row, int column) {
        Label label = (Label) gridPane.getChildren().get(row * crossword.getWidth() + column);

        if (crossword.isBlackSquare(row, column)) {
            label.setStyle("-fx-background-color: black; -fx-border-color: black;");
            return;
        }

        String style = "-fx-border-color: " + ((row == currentRow && column == currentColumn) ? "blue" : "black") +
                "; -fx-alignment: center; -fx-background-color: " +
                ((correctCells != null && correctCells[row][column]) ? "green;" : "white;");
        label.setStyle(style);
    }

    private void updateCurrentCell() {
        for (int i = 0; i < crossword.getHeight(); i++) {
            for (int j = 0; j < crossword.getWidth(); j++) {
                Label label = (Label) gridPane.getChildren().get(i * crossword.getWidth() + j);
                updateCellStyle(i, j);
                if (i == currentRow && j == currentColumn) {
                    label.requestFocus();
                }
            }
        }
    }

    private void moveToNextCell() {
        if (isHorizontal) {
            moveToCell(currentRow, currentColumn + 1);
        } else {
            moveToCell(currentRow + 1, currentColumn);
        }
    }

    private void moveToPreviousCell() {
        if (isHorizontal) {
            moveToCell(currentRow, currentColumn - 1);
        } else {
            moveToCell(currentRow - 1, currentColumn);
        }
    }

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
     * Gère la sélection d’un indice par l'utilisateur.
     *
     * @param clueText     Texte de l’indice.
     * @param isHorizontal Orientation de l’indice.
     */
    private void selectClue(String clueText, boolean isHorizontal) {
        if (isUpdatingSelection) return;
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

    private void updateClueSelection() {
        if (isUpdatingSelection) return;
        isUpdatingSelection = true;

        horizontalClues.getSelectionModel().clearSelection();
        verticalClues.getSelectionModel().clearSelection();

        for (Clue clue : crossword.getHorizontalClues()) {
            if (clue.getRow() == currentRow && clue.getColumn() <= currentColumn &&
                clue.getColumn() + clue.getClue().length() > currentColumn) {
                horizontalClues.getSelectionModel().select(clue.getClue());
                break;
            }
        }

        for (Clue clue : crossword.getVerticalClues()) {
            if (clue.getColumn() == currentColumn && clue.getRow() <= currentRow &&
                clue.getRow() + clue.getClue().length() > currentRow) {
                verticalClues.getSelectionModel().select(clue.getClue());
                break;
            }
        }

        isUpdatingSelection = false;
    }

    /**
     * Gère les touches fléchées pour naviguer dans la grille.
     *
     * @param keyCode Code de la touche directionnelle.
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
            default:
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
}
