package ui;

import exceptions.ExceedThresholdException;
import exceptions.InvalidCardException;
import model.Card;
import model.Deck;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/* Launches the application
   Save & Load features via JSON Made with reference: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo */
public class ConsoleApplication {
    private List<Deck> decks;
    private static final int CORRECT_POINTS = 1;
    private static final int INCORRECT_POINTS = -1;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private static final String JSON_STORE = "./data/reviewer.json";

    public ConsoleApplication() {
        this.decks = new ArrayList<>();
        this.jsonWriter = new JsonWriter(JSON_STORE);
        this.jsonReader = new JsonReader(JSON_STORE);
        init();
    }

    // MODIFIES: this
    // EFFECTS: Starts the flashcard application
    private void init() {
        System.out.println("\n\nWelcome to Reviewer!");
        System.out.println("Your current decks:");
        listDecks();
        System.out.println("Type 'review name' to review the deck with the given name.");
        System.out.println("Type 'new name' to create a new deck with the given name.");
        System.out.println("Type 'edit name' to edit the deck with the given name.");
        System.out.println("Type 'mastery' to see the level of mastery attained for all decks.");
        System.out.println("Type 'save' to save the current state of the application.");
        System.out.println("Type 'load' to save the current state of the application.");
        System.out.println("Type 'exit' to end the application.");
        getAction();
    }

    // EFFECTS: saves the decks to file
    private void saveDecks() {
        try {
            jsonWriter.open();
            jsonWriter.write(this.decks);
            jsonWriter.close();
            System.out.println("Saved all decks to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
            System.exit(0);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads decks from file
    private void loadDecks() {
        try {
            this.decks = jsonReader.read();
            System.out.println("Loaded all decks from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
            System.exit(0);
        } catch (InvalidCardException | ExceedThresholdException e) {
            System.out.println("Save file error.");
            System.exit(0);
        }
    }

    // EFFECTS: List all the decks currently available.
    private void listDecks() {
        if (decks.size() == 0) {
            System.out.println("You have no decks right now - make one using 'new name'!");
        } else {
            for (Deck d : decks) {
                System.out.println(d.getName());
            }
        }
    }

    // EFFECTS: Lists all decks and the mastery level attained for the deck.
    private void showMastery() {
        if (decks.size() == 0) {
            System.out.println("You currently have no decks! Make one using the command 'new name'!");
        } else {
            for (Deck d : decks) {
                System.out.println(d.getName() + " | MASTERY LEVEL: " + d.getMastery() + "%");
            }
        }
    }

    // MODIFIES: this
    // EFFECT: Get input from user to go to next action
    private void getAction() {
        Scanner s = new Scanner(System.in);
        String input = s.nextLine();
        if (input.startsWith("review") && input.length() >= 8) {
            tryStartAction(input.substring(7), "review");
        } else if (input.startsWith("new") && input.length() >= 5) {
            addDeck(input);
        } else if (input.startsWith("edit") && input.length() >= 6) {
            tryStartAction(input.substring(5), "edit");
        } else if (input.equals("mastery")) {
            showMastery();
        } else if (input.equals("save")) {
            saveDecks();
        } else if (input.equals("load")) {
            loadDecks();
        } else if (input.equals("exit")) {
            System.exit(0);
        } else {
            System.out.println("Invalid command - please try again!");
        }
        init();
    }

    // REQUIRES: deck cannot be an empty string.
    // MODIFIES: this, d
    // EFFECTS: Starts desired action for user if the given deck is found
    private void tryStartAction(String deck, String action) {
        Deck d = findDeckWithName(deck);
        if (d != null) {
            if (action.equals("edit")) {
                startEdit(d);
            } else if (action.equals("review")) {
                startReview(d);
            }
        } else {
            System.out.println("Deck not found - please try again!");
        }
    }

    // EFFECTS: Attempts to find deck with given name from decks.
    private Deck findDeckWithName(String name) {
        for (Deck d : decks) {
            if (d.getName().equals(name)) {
                return d;
            }
        }
        return null;
    }

    // MODIFIES: this
    // EFFECTS: adds a new deck with given name to list of decks,
    //          as long as the name does not overlap with another deck
    private void addDeck(String input) {
        String name = input.substring(4);
        if (findDeckWithName(name) == null && !name.trim().isEmpty()) {
            decks.add(new Deck(name.trim()));
            System.out.println("A new deck with the name " + name + " has been successfully created!");
        } else {
            System.out.println("Invalid name! Please choose a non-duplicate, non-empty name.");
        }
    }

    // MODIFIES: this, d
    // EFFECTS: Start review session with given deck.
    private void startReview(Deck d) {
        double oldMastery = d.getMastery();
        if (d.getNumOfCards() > 0) {
            System.out.println("\nStarting review session for " + d.getName());
            Scanner s = new Scanner(System.in);
            for (int i = 1; i <= d.getNumOfCards(); i++) {
                Card c = d.getNthCard(i);
                System.out.println(c.getFront());
                System.out.println("Type anything when you are ready to reveal the back of the card!");
                s.nextLine();
                System.out.println(c.getBack());
                System.out.println("Did you get it right? Type y for yes, and n for no.");
                boolean b = recordScore(c, s.nextLine());
                while (!b) {
                    System.out.println("Invalid input! Please try again. Type y for yes, and n for no.");
                    b = recordScore(c, s.nextLine());
                }
            }
            System.out.println("Review complete!");
            System.out.println("Your mastery level has changed from " + oldMastery + "% to " + d.getMastery() + "%!");
        } else {
            System.out.println("Your deck has no cards! Please add some cards before trying to review.");
        }
    }

    // MODIFIES: this, c
    // EFFECTS: Adds or deducts mastery point to card depending on response
    //          - returns true if response can be parsed ( 'y' or 'n' ), false otherwise.
    private boolean recordScore(Card c, String response) {
        if (response.equals("y")) {
            try {
                c.changeScoreBy(CORRECT_POINTS);
            } catch (ExceedThresholdException e) {
                //
            }
            return true;
        } else if (response.equals("n")) {
            try {
                c.changeScoreBy(INCORRECT_POINTS);
            } catch (ExceedThresholdException e) {
                //
            }
            return true;
        }
        return false;
    }

    // MODIFIES: this, d
    // EFFECTS: Start edit session with given deck.
    private void startEdit(Deck d) {
        System.out.println("\nYou are now editing deck " + d.getName());
        System.out.println("Here are the cards in this deck:\n");
        System.out.println(d.listCards());
        System.out.println("\nType 'new' to create a new card");
        System.out.println("Type 'delete n', where n is the nth card to delete the nth card.");
        System.out.println("Type 'delete deck' to delete this deck completely!");
        System.out.println("Type anything else to go back to the main menu.");
        Scanner s = new Scanner(System.in);
        String input = s.nextLine();
        if (input.startsWith("new")) {
            addCard(d);
            startEdit(d);
        } else if (input.startsWith("delete deck")) {
            deleteDeck(d);
        } else if (input.startsWith("delete") && input.length() >= 8) {
            tryDeleteCard(d, input.substring(7));
        }
    }

    // MODIFIES: this, d
    // EFFECTS: creates and adds a card to the deck.
    private void addCard(Deck d) {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the front side of the card");
        String front = s.nextLine();
        System.out.println("Enter the back side of the card");
        String back = s.nextLine();
        try {
            d.addCard(new Card(front, back));
        } catch (InvalidCardException e) {
            //
        }
        System.out.println("Card has been successfully added to the deck!");
    }

    // REQUIRES: deck must be in list of decks
    // MODIFIES: this
    // EFFECTS: deletes deck from list of decks in the application.
    private void deleteDeck(Deck d) {
        decks.remove(d);
        System.out.println("The deck '" + d.getName() + "' has been removed.");
    }

    // MODIFIES: this, d
    // EFFECTS: deletes the nth card from deck if the nth card exists.
    private void tryDeleteCard(Deck d, String n) {
        try {
            int card = Integer.parseInt(n);
            d.removeNthCard(card);
            System.out.println("The " + "#" + n + " card of the deck has been removed successfully.");
        } catch (Exception e) {
            System.out.println("Error: The nth card does not exist, or you entered a invalid input."
                    + "Please try again!");
        }
        startEdit(d);
    }
}
