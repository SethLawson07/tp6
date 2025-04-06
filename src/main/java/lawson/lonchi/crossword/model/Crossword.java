package lawson.lonchi.crossword.model;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.StringProperty;


public class Crossword extends Grid<CrosswordSquare> {
    private ObservableList<Clue> verticalClues = FXCollections.observableArrayList();
    private ObservableList<Clue> horizontalClues = FXCollections.observableArrayList();

    private boolean[][] correctCells;

    public Crossword(int height, int width) {
        super(height, width);
        grid = new CrosswordSquare[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = new CrosswordSquare(' ', false);
            }
        }
    }

    /**
     * Crée une grille de mots croisés à partir de la base de données.
     *
     * @param database Base de données contenant les définitions et solutions.
     * @param height   Hauteur de la grille.
     * @param width    Largeur de la grille.
     * @return Grille initialisée avec les données.
     * @throws SQLException En cas d'erreur lors de la requête.
     */
    public static Crossword createPuzzle(Database database, int height, int width) throws SQLException {
        Crossword crossword = new Crossword(height, width);
        ResultSet resultSet = database.executeQuery("SELECT * FROM CROSSWORD WHERE numero_grille = 1");

        while (resultSet.next()) {
            String definition = resultSet.getString("definition");
            boolean horizontal = resultSet.getBoolean("horizontal");
            int row = resultSet.getInt("ligne");
            int column = resultSet.getInt("colonne");
            String solution = resultSet.getString("solution");

            if (row < height && column < width) {
                crossword.setDefinition(row, column, horizontal, definition);
                crossword.setSolution(row, column, solution.charAt(0));
            }
        }

        return crossword;
    }

    public void setDefinition(int row, int column, boolean horizontal, String definition) {
        Clue clue = new Clue(definition, row, column, horizontal);
        if (horizontal) {
            horizontalClues.add(clue);
        } else {
            verticalClues.add(clue);
        }
    }


    public void setSolution(int row, int column, char solution) {
        if (row >= 0 && row < getHeight() && column >= 0 && column < getWidth()) {
            grid[row][column].setSolution(solution);
        }
    }


    public ObservableList<Clue> getHorizontalClues() {
        return horizontalClues;
    }


    public ObservableList<Clue> getVerticalClues() {
        return verticalClues;
    }


    public CrosswordSquare getCell(int row, int column) {
        return grid[row][column];
    }

    /**
     * Retourne la propriété observable liée à la proposition d'une case.
     *
     * @param row    Ligne.
     * @param column Colonne.
     * @return Propriété de proposition.
     */
    public StringProperty propositionProperty(int row, int column) {
        return getCell(row, column).propositionProperty();
    }

    public boolean isBlackSquare(int row, int column) {
        if (row < 0 || row >= getHeight() || column < 0 || column >= getWidth()) {
            return false;
        }

        if (getSolution(row, column) == ' ') {
            return true;
        }

        for (Clue clue : horizontalClues) {
            if (clue.getRow() == row && column >= clue.getColumn() &&
                column < clue.getColumn() + clue.getClue().length()) {
                return false;
            }
        }

        for (Clue clue : verticalClues) {
            if (clue.getColumn() == column && row >= clue.getRow() &&
                row < clue.getRow() + clue.getClue().length()) {
                return false;
            }
        }

        return true;
    }

    
    public void setBlackSquare(int row, int column, boolean black) {
        getCell(row, column).setBlackSquare(black);
    }

    
    public char getSolution(int row, int column) {
        return getCell(row, column).getSolution();
    }


    public char getProposition(int row, int column) {
        return getCell(row, column).getProposition();
    }

    
    public void setProposition(int row, int column, char proposition) {
        if (row >= 0 && row < getHeight() && column >= 0 && column < getWidth()) {
            grid[row][column].setProposition(proposition);
        }
    }


    public String getDefinition(int row, int column, boolean horizontal) {
        for (Clue clue : horizontal ? horizontalClues : verticalClues) {
            if (clue.getRow() == row && clue.getColumn() == column) {
                return clue.getClue();
            }
        }
        return null;
    }

   
    public void printProposition() {
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                System.out.print(getCell(i, j).getProposition() + " ");
            }
            System.out.println();
        }
    }

   
    public void printSolution() {
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                System.out.print(getCell(i, j).getSolution() + " ");
            }
            System.out.println();
        }
    }

    /**
     * Retourne les définitions horizontales sous forme de chaînes.
     *
     * @return Liste observable des définitions.
     */
    public ObservableList<String> getHorizontalCluesAsStrings() {
        ObservableList<String> clues = FXCollections.observableArrayList();
        for (Clue clue : horizontalClues) {
            clues.add(clue.getClue());
        }
        return clues;
    }

    /**
     * Retourne les définitions verticales sous forme de chaînes.
     *
     * @return Liste observable des définitions.
     */
    public ObservableList<String> getVerticalCluesAsStrings() {
        ObservableList<String> clues = FXCollections.observableArrayList();
        for (Clue clue : verticalClues) {
            clues.add(clue.getClue());
        }
        return clues;
    }
}
