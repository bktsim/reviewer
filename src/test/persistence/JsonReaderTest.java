package persistence;

import exceptions.InvalidCardException;
import model.Card;
import model.Deck;
import exceptions.ExceedThresholdException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/* Made with reference: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo */
public class JsonReaderTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            List<Deck> decks = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        } catch (ExceedThresholdException | InvalidCardException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testReaderNoDecks() {
        JsonReader reader = new JsonReader("./data/testReaderEmpty.json");
        try {
            List<Deck> decks = reader.read();
            assertEquals(0, decks.size());
        } catch (IOException e) {
            fail("Couldn't read from file");
        } catch (ExceedThresholdException | InvalidCardException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testReaderGeneralDeck() {
        JsonReader reader = new JsonReader("./data/testReaderNormal.json");
        try {
            List<Deck> decks = reader.read();
            assertEquals(2, decks.size());

            Deck d1 = decks.get(0);
            Deck d2 = decks.get(1);

            Card c1 = d1.getNthCard(1);
            Card c2 = d1.getNthCard(2);

            assertEquals("Deck One", d1.getName());
            assertEquals(2, d1.getNumOfCards());
            assertEquals("Where is UBC located?", c1.getFront());
            assertEquals("BC", c1.getBack());
            assertEquals(1, c1.getScore());
            assertEquals("What is 1+1?", c2.getFront());
            assertEquals("2", c2.getBack());
            assertEquals(-3, c2.getScore());

            assertEquals("Empty Deck", d2.getName());
            assertEquals(0, d2.getNumOfCards());

        } catch (IOException e) {
            fail("Couldn't read from file");
        } catch (ExceedThresholdException | InvalidCardException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testReaderThresholdExceptionDeck() {
        JsonReader reader = new JsonReader("./data/testReaderException.json");
        try {
            reader.read();
            fail("ExceedThresholdException expected from Card 2");
        } catch (ExceedThresholdException e) {
            // pass
        } catch (IOException e) {
            fail("Couldn't read from file");
        } catch (InvalidCardException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testReaderInvalidCardExceptionDeck() {
        JsonReader reader = new JsonReader("./data/testReaderCardException.json");
        try {
            reader.read();
            fail("InvalidCardException expected from Card 2");
        } catch (ExceedThresholdException e) {
            fail("Exception not expected");
        } catch (IOException e) {
            fail("Couldn't read from file");
        } catch (InvalidCardException e) {
            // pass
        }
    }
}
