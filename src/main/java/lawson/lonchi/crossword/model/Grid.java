package lawson.lonchi.crossword.model;


public class Grid<T> {
    protected T[][] grid;
    private int height;
    private int width;

    @SuppressWarnings("unchecked")
    public Grid(int height, int width) {
        this.height = height;
        this.width = width;
        this.grid = (T[][]) new Object[height][width];
    }

    public T getCell(int row, int column) {
        return grid[row][column];
    }

    public void setCell(int row, int column, T cell) {
        grid[row][column] = cell;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
