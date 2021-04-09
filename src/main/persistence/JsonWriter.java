package persistence;

import model.Deck;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

/* Writes existing data to file when the application is closed
   Made with reference: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo */
public class JsonWriter {
    private static final int TAB = 4;
    private PrintWriter writer;
    private String destination;

    // EFFECTS: constructs writer to write to destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if destination file cannot
    // be opened for writing
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of decks in application to file
    public void write(List<Deck> decks) {
        JSONObject json = new JSONObject();
        json.put("decks", addDecks(decks));
        saveToFile(json.toString(TAB));
    }

    // EFFECTS: returns decks in this application as a JSONArray
    public JSONArray addDecks(List<Deck> decks) {
        JSONArray jsonArray = new JSONArray();

        for (Deck d : decks) {
            jsonArray.put(d.toJson());
        }

        return jsonArray;
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }
}
