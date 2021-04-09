package model;

import exceptions.ExceedThresholdException;
import exceptions.InvalidCardException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest extends BaseCardTestSetup {
    @Test
    void testConstructor() {
        assertEquals("What is the powerhouse of the cell?", c1.getFront());
        assertEquals("Mitochondria", c1.getBack());

        assertEquals("What is 1+1?", c2.getFront());
        assertEquals("2", c2.getBack());

        assertEquals("Where is UBC located?", c3.getFront());
        assertEquals("Vancouver, BC", c3.getBack());

        assertEquals("What is the name of this application?", c4.getFront());
        assertEquals("Flashcards", c4.getBack());

        assertEquals("Consider f(x) = x^2 + 2x + 1. What are the roots of this function?", c5.getFront());
        assertEquals("x=-1", c5.getBack());
    }

    @Test
    void testCreateInvalidCardFront() {
        try {
            new Card("", "test");
        } catch (InvalidCardException e) {
            // pass, exception expected
        }
    }

    @Test
    void testCreateInvalidCardBack() {
        try {
            new Card("test", "");
        } catch (InvalidCardException e) {
            // pass, exception expected
        }
    }

    @Test
    void testCreateInvalidBoth() {
        try {
            new Card("", "");
        } catch (InvalidCardException e) {
            // pass, exception expected
        }
    }
    
    @Test
    void testChangeScoreAddSubtract() {
        try {
            assertEquals(0, c1.getScore());
            c1.changeScoreBy(1);
            assertEquals(1, c1.getScore());
            c1.changeScoreBy(2);
            assertEquals(3, c1.getScore());
            c1.changeScoreBy(Card.BEST_THRESHOLD - 3);
            assertEquals(Card.BEST_THRESHOLD, c1.getScore());
            c1.changeScoreBy(-3);
            assertEquals(Card.BEST_THRESHOLD - 3, c1.getScore());
        } catch (ExceedThresholdException e) {
            fail("No exception expected");
        }
    }

    @Test
    void testChangeScoreDrasticSubtract() {
        try {
            assertEquals(0, c2.getScore());
            c2.changeScoreBy(-5000);
            fail("Exception should have been thrown for reaching minimum");
        } catch (ExceedThresholdException e) {
            assertEquals(0, c2.getScore());
        }
    }

    @Test
    void testChangeScoreDrasticAdd() {
        try {
            assertEquals(0, c2.getScore());
            c2.changeScoreBy(+400);
            fail("Exception should have been thrown for reaching maximum");
        } catch (ExceedThresholdException e) {
            assertEquals(0, c2.getScore());
        }
    }

    @Test
    void testChangeScoreNoPoints() {
        try {
            assertEquals(0, c2.getScore());
            c2.changeScoreBy(0);
            assertEquals(0, c2.getScore());
        } catch (ExceedThresholdException e) {
            fail("No exception expected");
        }
    }

    @Test
    void testToString() {
        assertEquals(c1.toString(), c1.getFront());
        assertEquals(c2.toString(), c2.getFront());
    }

}
