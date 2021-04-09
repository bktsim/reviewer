package persistence;

import exceptions.InvalidCardException;
import model.BaseCardTestSetup;
import model.Card;
import model.Deck;
import exceptions.ExceedThresholdException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/* Made with reference: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo */
public class JsonWriterTest extends BaseCardTestSetup {
    @Test
    void testWriterInvalidFile() {
        try {
            List<Deck> decks = new ArrayList<>();
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyDeck() {
        try {
            List<Deck> decks = new ArrayList<>();
            JsonWriter writer = new JsonWriter("./data/testWriterEmpty.json");
            writer.open();
            writer.write(decks);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmpty.json");
            decks = reader.read();
            assertEquals(0, decks.size());
        } catch (IOException | ExceedThresholdException | InvalidCardException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralDeck() {
        try {
            List<Deck> decks = new ArrayList<>();
            Deck d1 = new Deck("Deck A");
            Deck d2 = new Deck("Deck B (Empty)");
            Deck d3 = new Deck("Deck C");

            decks.add(d1);
            decks.add(d2);
            decks.add(d3);

            d1.addCard(c1);
            d1.addCard(c3);
            d1.addCard(c3);
            d3.addCard(c2);
            d3.addCard(c1);
            d3.addCard(c4);
            d3.addCard(c5);
            d3.addCard(c2);

            JsonWriter writer = new JsonWriter("./data/testWriterNormal.json");
            writer.open();
            writer.write(decks);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterNormal.json");
            decks = reader.read();
            assertEquals(3, decks.size());

            assertTrue(sameDeck(d1, decks.get(0)));
            assertTrue(sameDeck(d2, decks.get(1)));
            assertTrue(sameDeck(d3, decks.get(2)));
        } catch (IOException | ExceedThresholdException | InvalidCardException e) {
            fail("Exception should not have been thrown");
        }
    }

    // EFFECTS: returns true if the contents of d1 is the same as the contents of d2, otherwise returns false
    private boolean sameDeck(Deck d1, Deck d2) {
        boolean sameNoOfCards = d1.getNumOfCards() == d2.getNumOfCards();
        boolean sameName = d1.getName().equals(d2.getName());
        boolean result = false;
        if (sameNoOfCards && sameName) {
            result = true;
            for (int i = 1; i <= d1.getNumOfCards(); i++) {
                Card c1 = d1.getNthCard(i);
                Card c2 = d2.getNthCard(i);
                if (!sameCard(c1, c2)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    // EFFECTS: returns true if the contents of c1 is the same as the contents of c2, otherwise returns false
    private boolean sameCard(Card c1, Card c2) {
        return c1.getFront().equals(c2.getFront()) &&
                c1.getBack().equals(c2.getBack()) &&
                c1.getScore() == c2.getScore();
    }

}
