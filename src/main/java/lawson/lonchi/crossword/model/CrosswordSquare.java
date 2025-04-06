package lawson.lonchi.crossword.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class CrosswordSquare {
    private StringProperty proposition = new SimpleStringProperty("");
    private char solution;
    private boolean isBlack;

   
    public CrosswordSquare(char solution, boolean isBlack) {
        this.solution = solution;
        this.isBlack = isBlack;
    }

    
    public StringProperty propositionProperty() {
        return proposition;
    }

   
    public char getSolution() {
        return solution;
    }

    
    public void setSolution(char solution) {
        this.solution = solution;
    }

   
    public boolean isBlackSquare() {
        return isBlack;
    }

    public void setBlackSquare(boolean black) {
        isBlack = black;
    }

   
    public char getProposition() {
        if (isBlackSquare()) {
            return '\0';
        }
        String prop = proposition.get();
        return prop.isEmpty() ? ' ' : prop.charAt(0);
    }

   
    public void setProposition(char proposition) {
        this.proposition.set(String.valueOf(proposition).toLowerCase());
    }
}
