package lawson.lonchi.crossword;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import lawson.lonchi.crossword.model.Crossword;
import lawson.lonchi.crossword.model.CrosswordSquare;

import static org.junit.jupiter.api.Assertions.*;

class CrosswordTestV2 {

    private Crossword crossword;

    @BeforeEach
    void setUp() {
        crossword = new Crossword(5, 5); // Grille de 5x5
    }

    @Test
    void testSetAndGetSolution() {
        crossword.setSolution(0, 0, 'A');
        assertEquals('A', crossword.getSolution(0, 0)); // La solution doit être 'A'
    }

    @Test
    void testSetAndGetProposition() {
        crossword.setProposition(0, 0, 'B');
        assertEquals('b', crossword.getProposition(0, 0)); // La proposition doit être 'b' (en minuscule)
    }

    @Test
    void testSetDefinition() {
        crossword.setDefinition(0, 0, true, "Horizontal clue");
        assertEquals("Horizontal clue", crossword.getDefinition(0, 0, true)); // L'indice doit être correct
    }

    @Test
    void testGetHorizontalClues() {
        crossword.setDefinition(0, 0, true, "Horizontal clue");
        assertEquals(1, crossword.getHorizontalClues().size()); // Il doit y avoir un indice horizontal
    }

    @Test
    void testGetVerticalClues() {
        crossword.setDefinition(0, 0, false, "Vertical clue");
        assertEquals(1, crossword.getVerticalClues().size()); // Il doit y avoir un indice vertical
    }

    @Test
    void testPropositionProperty() {
        StringProperty propositionProperty = crossword.propositionProperty(0, 0);
        assertNotNull(propositionProperty); // La propriété ne doit pas être nulle
        propositionProperty.set("C");
        assertEquals('C', crossword.getProposition(0, 0)); // La proposition doit être mise à jour
    }

    @Test
    void testSetBlackSquare() {
        crossword.setBlackSquare(0, 0, true);
        assertTrue(crossword.isBlackSquare(0, 0)); // La case doit être noire
    }

    @Test
    void testGetCell() {
        CrosswordSquare cell = crossword.getCell(0, 0);
        assertNotNull(cell); // La cellule ne doit pas être nulle
    }

    @Test
    void testPrintProposition() {
        crossword.setProposition(0, 0, 'A');
        crossword.setProposition(1, 1, 'B');
        crossword.printProposition(); // Affiche les propositions dans la console
    }

    @Test
    void testPrintSolution() {
        crossword.setSolution(0, 0, 'A');
        crossword.setSolution(1, 1, 'B');
        crossword.printSolution(); // Affiche les solutions dans la console
    }

    @Test
    void testGetHorizontalCluesAsStrings() {
        crossword.setDefinition(0, 0, true, "Horizontal clue");
        ObservableList<String> clues = crossword.getHorizontalCluesAsStrings();
        assertEquals(1, clues.size()); // Il doit y avoir un indice horizontal
        assertEquals("Horizontal clue", clues.get(0)); // L'indice doit être correct
    }

    @Test
    void testGetVerticalCluesAsStrings() {
        crossword.setDefinition(0, 0, false, "Vertical clue");
        ObservableList<String> clues = crossword.getVerticalCluesAsStrings();
        assertEquals(1, clues.size()); // Il doit y avoir un indice vertical
        assertEquals("Vertical clue", clues.get(0)); // L'indice doit être correct
    }
}