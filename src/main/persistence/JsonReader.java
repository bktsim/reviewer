package persistence;

import exceptions.InvalidCardException;
import model.Card;
import model.Deck;
import exceptions.ExceedThresholdException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/* Reads JSON file and loads the data automatically
   Made with reference: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo */
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads all decks from file and returns it;
    // throws IOException if an error occurs reading data from file
    public List<Deck> read() throws IOException, ExceedThresholdException, InvalidCardException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseDecks(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses all decks from JSON object and returns it
    public List<Deck> parseDecks(JSONObject jsonObject) throws ExceedThresholdException, InvalidCardException {
        JSONArray jsonArray = jsonObject.getJSONArray("decks");
        List<Deck> decks = new ArrayList<>();

        for (Object json : jsonArray) {
            Deck d = parseDeck((JSONObject) json);
            decks.add(d);
        }
        return decks;
    }

    // EFFECTS: parses deck from JSON object and returns it
    private Deck parseDeck(JSONObject jsonObject) throws ExceedThresholdException, InvalidCardException {
        String name = jsonObject.getString("name");
        Deck d = new Deck(name);
        addCards(d, jsonObject);
        return d;
    }

    // MODIFIES: d
    // EFFECTS: parses cards from JSON object (deck) and adds them to deck
    private void addCards(Deck d, JSONObject jsonObject) throws ExceedThresholdException, InvalidCardException {
        JSONArray jsonArray = jsonObject.getJSONArray("flashcards");
        for (Object json : jsonArray) {
            JSONObject nextThingy = (JSONObject) json;
            addCard(d, nextThingy);
        }
    }

    // MODIFIES: d
    // EFFECTS: parses card from JSON object and adds it to deck
    private void addCard(Deck d, JSONObject jsonObject) throws ExceedThresholdException, InvalidCardException {
        String front = jsonObject.getString("front");
        String back = jsonObject.getString("back");
        int score = jsonObject.getInt("score");
        Card c = new Card(front, back);
        c.changeScoreBy(score);
        d.addCard(c);
    }
}
