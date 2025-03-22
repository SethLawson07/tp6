package lawson.lonchi.crossword;

import static org.junit.jupiter.api.Assertions.*;

import lawson.lonchi.crossword.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CrosswordTest {
    private Crossword crossword;

    @BeforeEach
    void setUp() {
        crossword = new Crossword(5, 5);
    }

    @Test
    void testClueCreation() {
        Clue clue = new Clue("Test Clue", 1, 2, true);
        assertEquals("Test Clue", clue.getClue());
        assertEquals(1, clue.getRow());
        assertEquals(2, clue.getColumn());
        assertTrue(clue.isHorizontal());
    }

    @Test
    void testCrosswordSquare() {
        CrosswordSquare square = new CrosswordSquare('A', false);
        assertEquals('A', square.getSolution());
        assertFalse(square.isBlackSquare());

        square.setBlackSquare(true);
        assertTrue(square.isBlackSquare());
    }

    @Test
    void testSetSolution() {
        crossword.setSolution(2, 3, 'B');
        assertEquals('B', crossword.getSolution(2, 3));
    }

    @Test
    void testSetProposition() {
        crossword.setProposition(1, 1, 'C');
        assertEquals('c', crossword.getProposition(1, 1)); // Vérification en minuscule
    }

    @Test
    void testSetAndRetrieveClues() {
        crossword.setDefinition(0, 0, true, "Horizontal Clue");
        crossword.setDefinition(0, 1, false, "Vertical Clue");

        assertEquals("Horizontal Clue", crossword.getDefinition(0, 0, true));
        assertEquals("Vertical Clue", crossword.getDefinition(0, 1, false));
    }

    @Test
    void testIsBlackSquare() {
        crossword.setDefinition(2, 2, true, "word");
        assertFalse(crossword.isBlackSquare(2, 2));
    }
}
