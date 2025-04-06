package lawson.lonchi.crossword.model;

/**
 * Représente une grille générique à deux dimensions.
 *
 * @param <T> Le type des éléments contenus dans la grille.
 */
public class Grid<T> {
    protected T[][] grid;
    private int height;
    private int width;

    /**
     * Crée une grille vide de dimensions spécifiées.
     *
     * @param height Nombre de lignes.
     * @param width  Nombre de colonnes.
     */
    @SuppressWarnings("unchecked")
    public Grid(int height, int width) {
        this.height = height;
        this.width = width;
        this.grid = (T[][]) new Object[height][width];
    }

    /**
     * Retourne la cellule à la position spécifiée.
     *
     * @param row    Ligne de la cellule.
     * @param column Colonne de la cellule.
     * @return Élément de type {@code T} à la position donnée.
     */
    public T getCell(int row, int column) {
        return grid[row][column];
    }

    /**
     * Définit une cellule à la position donnée.
     *
     * @param row    Ligne de la cellule.
     * @param column Colonne de la cellule.
     * @param cell   Élément à placer dans la grille.
     */
    public void setCell(int row, int column, T cell) {
        grid[row][column] = cell;
    }

    /**
     * Retourne la hauteur de la grille (nombre de lignes).
     *
     * @return Hauteur de la grille.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retourne la largeur de la grille (nombre de colonnes).
     *
     * @return Largeur de la grille.
     */
    public int getWidth() {
        return width;
    }
}
