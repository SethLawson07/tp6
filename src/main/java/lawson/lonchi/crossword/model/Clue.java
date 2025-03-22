package lawson.lonchi.crossword.model;

public class Clue {
    private String clue;
    private int row;
    private int column;
    private boolean horizontal;

    public Clue(String clue, int row, int column, boolean horizontal) {
        this.clue = clue;
        this.row = row;
        this.column = column;
        this.horizontal = horizontal;
    }

    public String getClue() {
        return clue;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    @Override
    public String toString() {
        return clue;
    }
}