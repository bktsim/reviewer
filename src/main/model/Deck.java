package model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import persistence.Writable;

/* A holder for a collection of flashcards (Card) to be stored and reviewed by the user.
   Method toJson & cardsToJson Made with reference: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo*/
public class Deck implements Writable {
    private String name;
    private ArrayList<Card> cards;
    public static final String ERROR_MESSAGE = "You have no cards in this deck!";

    public Deck(String name) {
        this.name = name;
        this.cards = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: Adds given card to the list of cards and returns the added card.
    public Card addCard(Card c) {
        this.cards.add(c);
        return c;
    }

    // EFFECTS: creates a message that when outputted, lists out all the cards in the deck
    public String listCards() {
        String output = "";
        if (cards.size() == 0) {
            output += ERROR_MESSAGE;
        } else {
            for (int i = 1; i <= cards.size(); i++) {
                output += i + ": " + getNthCard(i).getFront() + "\n";
            }
        }
        return output;
    }

    // REQUIRES: 1 <= n <= cards.size()
    // MODIFIES: this
    // EFFECTS: Removes selected card from deck and returns card removed
    public Card removeNthCard(int n) {
        Card c = this.cards.get(n - 1);
        this.cards.remove(n - 1);
        return c;
    }

    // EFFECTS: Returns the name of the deck
    public String getName() {
        return this.name;
    }

    // EFFECTS: Returns a list of cards in the deck.
    public List<Card> getCards() {
        return this.cards;
    }

    // EFFECTS: Returns the number of cards in the deck
    public int getNumOfCards() {
        return this.cards.size();
    }

    // REQUIRES: 1 <= n <= card.size()
    // EFFECTS: Return the Nth card of the deck.
    public Card getNthCard(int n) {
        return this.cards.get(n - 1);
    }

    // EFFECTS: Calculate the mastery (%) attained for the deck.
    public double getMastery() {
        double totalMastery = 0;
        double maxMastery = this.cards.size() * Card.BEST_THRESHOLD;

        for (Card c : this.cards) {
            totalMastery += Math.max(c.getScore(), 0);
        }

        if (maxMastery == 0) {
            return 100.0;
        } else {
            return Math.round((totalMastery / maxMastery) * 100.0);
        }
    }

    @Override
    // EFFECTS: represents contents of deck as a JSONObject
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("flashcards", cardsToJson());
        return json;
    }

    // EFFECTS: returns flashcards in this deck as a JSON array
    private JSONArray cardsToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Card c : this.cards) {
            jsonArray.put(c.toJson());
        }

        return jsonArray;
    }

    @Override
    // EFFECTS: returns a nice looking string for JComboBox
    public String toString() {
        return this.getName() + " | MASTERY: " + this.getMastery() + "%";
    }
}
