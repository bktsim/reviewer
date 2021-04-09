package model;

import exceptions.ExceedThresholdException;
import exceptions.InvalidCardException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest extends BaseCardTestSetup {
    private Deck d1;
    private Deck d2;

    @BeforeEach @Override
    void setup() {
        d1 = new Deck("Random Questions #1");
        d2 = new Deck("Cool Trivia");
        super.setup();
        d1.addCard(c1);
        d1.addCard(c2);
        d1.addCard(c3);

        d2.addCard(c4);
        d2.addCard(c5);
        d2.addCard(c1);
        d2.addCard(c3);
        d2.addCard(c4);
    }

    @Test
    void testConstructor() {
        assertEquals("Random Questions #1", d1.getName());
        assertEquals("Cool Trivia", d2.getName());
    }

    @Test
    void testListCardNormal() {
        // windows
        String s1 = "1: " + c1.getFront() + "\n"
                + "2: " + c2.getFront() + "\n"
                + "3: " + c3.getFront() + "\n";

        assertEquals(s1, d1.listCards());
    }

    @Test
    void testListCardEmpty() {
        Deck d3 = new Deck("Aaa");
        assertEquals(Deck.ERROR_MESSAGE, d3.listCards());
    }

    @Test
    void testAddCard() {
        assertEquals(3, d1.getNumOfCards());
        assertEquals(5, d2.getNumOfCards());

        assertEquals(c4, d1.addCard(c4));
        assertEquals(4, d1.getNumOfCards());
        assertEquals(c4, d1.getNthCard(4));

        assertEquals(c5, d1.addCard(c5));
        assertEquals(5, d1.getNumOfCards());
        assertEquals(c5, d1.getNthCard(5));

        assertEquals(c1, d1.addCard(c1));
        assertEquals(6, d1.getNumOfCards());
        assertEquals(c1, d1.getNthCard(6));
    }

    @Test
    void testRemoveNthCardFirst() {
        Card c1 = d1.getNthCard(1);
        assertEquals(c1, d1.removeNthCard(1));
        assertEquals(2, d1.getNumOfCards());
    }

    @Test
    void testRemoveNthCardMiddle() {
        // Remove middle
        Card c3 = d2.getNthCard(3);
        assertEquals(c3, d2.removeNthCard(3));
        assertEquals(4, d2.getNumOfCards());
    }

    @Test
    void testRemoveNthCardLast() {
        Card c3 = d1.getNthCard(3);
        assertEquals(c3, d1.removeNthCard(3));
        assertEquals(2, d1.getNumOfCards());
    }

    @Test
    void testRemoveNthCardOnly() {
        // Remove only
        Deck d = new Deck("Test");
        d.addCard(c1);

        assertEquals(c1, d.removeNthCard(1));
        assertEquals(0, d.getNumOfCards());
    }

    @Test
    void testGetCards() {
        Deck d = new Deck("Test");
        d.addCard(c1);
        assertEquals(c1, d.getCards().get(0));
    }

    @Test
    void testRemoveNthCardHugeDeck() {
        Deck d3 = new Deck("Huge Deck");
        for (int i = 0; i < 20; i++) {
            try {
                d3.addCard(new Card("Test test test " + i, "Test!"));
            } catch (InvalidCardException e) {
                fail("No exception expected");
            }
        }
        assertEquals(20, d3.getNumOfCards());

        Card c19 = d3.getNthCard(19);
        Card c16 = d3.getNthCard(16);
        Card c7 = d3.getNthCard(7);

        assertEquals(c19, d3.removeNthCard(19));
        assertEquals(19, d3.getNumOfCards());
        assertEquals(c16, d3.removeNthCard(16));
        assertEquals(18, d3.getNumOfCards());
        assertEquals(c7, d3.removeNthCard(7));
        assertEquals(17, d3.getNumOfCards());
    }

    @Test
    void testGetNthCard() {
        assertEquals(c1, d1.getNthCard(1));
        assertEquals(c2, d1.getNthCard(2));
        assertEquals(c3, d1.getNthCard(3));

        assertEquals(c4, d2.getNthCard(1));
        assertEquals(c5, d2.getNthCard(2));
        assertEquals(c1, d2.getNthCard(3));
        assertEquals(c3, d2.getNthCard(4));
        assertEquals(c4, d2.getNthCard(5));
    }

    @Test
    void testGetMasteryExtremeOverHundred() {
        // mastery does not exceed 100
        assertEquals(d1.getMastery(), 0);
        for (int i = 1; i <= d1.getNumOfCards(); i++) {
            try {
                d1.getNthCard(i).changeScoreBy(Card.BEST_THRESHOLD);
            } catch (ExceedThresholdException e) {
                fail("No exception expected");
            }
        }
        assertEquals(100.0, d1.getMastery());
    }

    @Test
    void testGetMasteryExtremeNegative() {
        // mastery does not go negative
        for (int i = 1; i <= d1.getNumOfCards(); i++) {
            try {
                d1.getNthCard(i).changeScoreBy(Card.WORST_THRESHOLD);
            } catch (ExceedThresholdException e) {
                fail("No exception expected");
            }
        }
        assertEquals(0, d1.getMastery());
    }

    @Test
    void testGetMasteryDivideByZero() {
        // no "divide by zero" problems
        assertEquals((new Deck("test")).getMastery(), 100);
        assertEquals(0, d2.getMastery());
    }

    @Test
    void testMasteryMorePosThanNeg() {
        try {
            d1.getNthCard(1).changeScoreBy(-3);
            d1.getNthCard(2).changeScoreBy(2);
            d1.getNthCard(3).changeScoreBy(Card.BEST_THRESHOLD);
        } catch (ExceedThresholdException e) {
            fail("No exception expected");
        }

        double mastery = 2 + Card.BEST_THRESHOLD;
        double maxMastery = Card.BEST_THRESHOLD * d1.getNumOfCards();

        assertEquals(d1.getMastery(), Math.round(100.0*(mastery/maxMastery)));
    }

    @Test
    void testMasterySomeNegSomePos() {
        try {
            d1.getNthCard(1).changeScoreBy(-2);
            d1.getNthCard(2).changeScoreBy(0);
            d1.getNthCard(3).changeScoreBy(Card.BEST_THRESHOLD);
        } catch (ExceedThresholdException e) {
            fail("No exception expected");
        }

        double mastery = Card.BEST_THRESHOLD;
        double maxMastery = Card.BEST_THRESHOLD * d1.getNumOfCards();

        assertEquals(d1.getMastery(), Math.round((mastery/maxMastery)*100.0));
    }

    @Test
    void testMasteryMoreNegThanPos() {
        try {
            d1.getNthCard(1).changeScoreBy(Card.WORST_THRESHOLD);
            d1.getNthCard(2).changeScoreBy(Card.WORST_THRESHOLD + 1);
            d1.getNthCard(3).changeScoreBy(3);
        } catch (ExceedThresholdException e) {
            fail("No exception expected");
        }

        double mastery = 3;
        double maxMastery = Card.BEST_THRESHOLD * d1.getNumOfCards();

        assertEquals(d1.getMastery(), Math.round((mastery/maxMastery)*100.0));
    }

    @Test
    void testToString() {
        Deck d = new Deck("Test");
        assertEquals(d.getName() + " | MASTERY: " + d.getMastery() + "%", d.toString());
    }
}
