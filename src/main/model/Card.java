package model;

import exceptions.ExceedThresholdException;
import exceptions.InvalidCardException;
import org.json.JSONObject;

import persistence.Writable;

/* A flashcard that a user can create and review. Each flashcard "remembers" how well the user remembers them.
   Method toJson Made with reference: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo */

public class Card implements Writable {
    public static final int BEST_THRESHOLD = 5;
    public static final int WORST_THRESHOLD = -3;

    private String front;
    private String back;
    private int score;

    // EFFECTS: throws InvalidCardException if either the front or back of the card is empty.
    public Card(String front, String back) throws InvalidCardException {
        if (front.isEmpty() || back.isEmpty()) {
            throw new InvalidCardException();
        }
        this.front = front;
        this.back = back;
        this.score = 0;
    }

    // EFFECTS: Returns the score of the card
    public int getScore() {
        return this.score;
    }

    // EFFECTS: Returns the front of the card
    public String getFront() {
        return this.front;
    }

    // EFFECTS: Returns the back of the card
    public String getBack() {
        return this.back;
    }

    // MODIFIES: this
    // EFFECTS: increases the current score by n if increasing/decreasing does not exceed thresholds.
    //          - throws ExceedThresholdException otherwise.
    public void changeScoreBy(int n) throws ExceedThresholdException {
        if (!((this.score + n >= WORST_THRESHOLD) && (this.score + n <= BEST_THRESHOLD))) {
            throw new ExceedThresholdException();
        } else {
            this.score += n;
        }
    }

    @Override
    // EFFECTS: converts information on card to JSONObject
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("front", this.front);
        json.put("back", this.back);
        json.put("score", this.score);
        return json;
    }

    @Override
    // EFFECTS: gives the card a "name" for JComboBox
    public String toString() {
        return getFront();
    }
}
