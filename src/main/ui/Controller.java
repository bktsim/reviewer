package ui;

import exceptions.InvalidCardException;
import model.Deck;
import exceptions.ExceedThresholdException;
import persistence.JsonReader;
import persistence.JsonWriter;
import ui.tabs.EditMenu;
import ui.tabs.MainMenu;
import ui.tabs.ReviewMenu;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
Citations:
    JFrame Exit On Close:
    https://stackoverflow.com/questions/7799940/jframe-exit-on-close-java

    NullPointerException on JOptionDialog:
    https://stackoverflow.com/questions/41256166/null-pointer-exception-when-joptionpane-showmessagedialog-is-cancelled

    JFrame Position at Center:
    https://stackoverflow.com/questions/9543320/how-to-position-the-form-in-the-center-screen/9543339

    JOptionPane:
    https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html

    LayoutManagers:
    https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html

    SmartHome (Implementation of JFrame, JPanel, and 'tabs', as well as ActionButton):
    https://github.com/UBCx-Software-Construction/long-form-problem-starters.git

    DefaultJComboBoxModel & JComboBox:
    https://stackoverflow.com/questions/10904639/how-to-refresh-the-jcombobox-data

    Play Sound:
    https://stackoverflow.com/questions/15526255/best-way-to-get-sound-on-button-press-for-a-java-calculator
    https://docs.oracle.com/javase/tutorial/sound/playing.html
    https://docs.oracle.com/javase/tutorial/sound/accessing.html#113154
    https://docs.oracle.com/javase/tutorial/sound/converters.html
 */

/* Constructs and controller the user interface for the application */
public class Controller {
    public static final int GUI_SIZE_X = 400;
    public static final int GUI_SIZE_Y = 300;

    private List<Deck> decks;
    private Deck selectedDeck;
    private JFrame mainframe;
    private JPanel mainMenu;
    private JPanel reviewMenu;
    private JPanel editMenu;

    public static final String SOUND_CORRECT = "./data/sound/correct.wav";
    public static final String SOUND_INCORRECT = "./data/sound/incorrect.wav";
    public static final String SOUND_ALERT = "./data/sound/alert.wav";
    public static final String SOUND_REVIEW = "./data/sound/review.wav";

    private final JsonWriter jsonWriter;
    private final JsonReader jsonReader;
    private static final String JSON_STORE = "./data/reviewer.json";

    // EFFECTS: constructs the UI
    public Controller() throws FileNotFoundException {
        this.decks = new ArrayList<>();
        this.jsonWriter = new JsonWriter(JSON_STORE);
        this.jsonReader = new JsonReader(JSON_STORE);
        loadDecks();
        initializeGUI();
        mainframe.setLocationRelativeTo(null);
    }

    // MODIFIES: this
    // EFFECTS: Constructs the UI
    private void initializeGUI() {
        mainframe = new JFrame("Reviewer");
        mainframe.setSize(GUI_SIZE_X, GUI_SIZE_Y);
        mainframe.setLayout(new CardLayout());

        this.mainMenu = new MainMenu(this);
        mainframe.setVisible(true);
        mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // EFFECTS: returns the mainframe (JFrame) of the UI
    public JFrame getMainframe() {
        return this.mainframe;
    }

    // EFFECTS: returns all decks
    public List<Deck> getDecks() {
        return this.decks;
    }

    // EFFECTS: sets the selected deck that is being interacted with
    public void setSelectedDeck(Deck d) {
        this.selectedDeck = d;
    }

    // EFFECTS: returns the selected deck that is being interacted with
    public Deck getSelectedDeck() {
        return this.selectedDeck;
    }

    // MODIFIES: this
    // EFFECTS: starts review session for deck if deck is eligible and able to do a review
    public void reviewDeck() {
        if (selectedDeck == null) {
            makeAlert("You need to have selected a deck to review it!");
        } else if (selectedDeck.getNumOfCards() == 0) {
            makeAlert("Your deck needs to have cards to be able to review!");
        } else {
            this.reviewMenu = new ReviewMenu(this);
        }
    }

    // REQUIRES: reviewMenu != null
    // MODIFIES: this
    // EFFECTS: allows user to interact/control the application during their review session for a deck
    public void reviewDeckAction(String action) {
        ReviewMenu reviewMenu = (ReviewMenu) this.reviewMenu;
        switch (action) {
            case ReviewMenu.COMMAND_REVEAL:
                reviewMenu.showNextCardFrame();
                break;
            case ReviewMenu.COMMAND_ANSWER_CORRECT:
                playSound(SOUND_CORRECT);
                reviewMenu.answer(1);
                break;
            case ReviewMenu.COMMAND_ANSWER_INCORRECT:
                playSound(SOUND_INCORRECT);
                reviewMenu.answer(0);
                break;
        }
    }

    // REQUIRES: editMenu != null
    // MODIFIES: this
    // EFFECTS: allows user to interact/control the application during their edit session for a deck.
    public void editDeckAction(String action) {
        EditMenu editMenu = (EditMenu) this.editMenu;
        switch (action) {
            case EditMenu.COMMAND_NEW_CARD:
                editMenu.newCard();
                break;
            case EditMenu.COMMAND_DELETE_CARD:
                editMenu.deleteSelectedCard();
                break;
            case EditMenu.COMMAND_RETURN_TO_MENU:
                endEdit();
                break;
        }
    }

    // REQUIRES: reviewMenu != null
    // MODIFIES: this
    // EFFECTS: ends review session and returns user back to main menu
    public void endReview() {
        reviewMenu.setVisible(false);
        mainframe.remove(reviewMenu);
        this.reviewMenu = null;

        this.mainMenu = new MainMenu(this);
        mainframe.repaint();
        playSound(SOUND_REVIEW);
        makeNotification("You have finished your review!");
    }

    // REQUIRES: editMenu != null
    // MODIFIES: this
    // EFFECTS: ends edit session and returns user back to main menu
    public void endEdit() {
        editMenu.setVisible(false);
        mainframe.remove(editMenu);
        this.editMenu = null;

        this.mainMenu = new MainMenu(this);
        mainframe.repaint();
    }

    // MODIFIES: this
    // EFFECTS: starts edit session for deck if deck is eligible and able to do a review
    public void editDeck() {
        if (selectedDeck == null) {
            makeAlert("You need to have selected a deck to edit it!");
        } else {
            this.editMenu = new EditMenu(this);
        }
    }

    // MODIFIES: this
    // EFFECTS: creates a new deck if no current decks have the given name & the name is valid.
    //          if deck creation is successful, sets new deck as selected deck.
    public void newDeck() {
        MainMenu mainMenu = (MainMenu) this.mainMenu;
        try {
            mainMenu.newDeck();
        } catch (NullPointerException e) {
            // Do nothing - user cancelled input.
        }
    }

    // MODIFIES: this
    // EFFECTS: deletes selectedDeck if selectedDeck is not null.
    public void deleteDeck() {
        MainMenu mainMenu = (MainMenu) this.mainMenu;
        mainMenu.deleteDeck();
    }

    // EFFECTS: saves the decks to file
    public void saveDecks() {
        try {
            jsonWriter.open();
            jsonWriter.write(this.decks);
            jsonWriter.close();
            makeNotification("Saved all decks to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            makeAlert("Unable to write file to " + JSON_STORE);
            System.exit(0);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads decks from file. Closes application if a error makes it so that save file cannot be read.
    public void loadDecks() {
        try {
            this.decks = jsonReader.read();
            if (this.mainMenu != null) {
                this.mainMenu = new MainMenu(this);
            }
            makeNotification("Loaded all decks from " + JSON_STORE);
        } catch (IOException e) {
            makeAlert("Unable to read from file:" + JSON_STORE);
            System.exit(1);
        } catch (ExceedThresholdException e) {
            makeAlert("Problem with loading cards due to threshold issue. Please consider expanding BEST and WORST "
                    + "thresholds for cards, and then relaunch the application.");
            System.exit(1);
        } catch (InvalidCardException e) {
            makeAlert("Problem with loading cards, as one of the cards in a deck in save file has a empty "
                    + "front/back side. Please ensure that no cards are empty, and relaunch the application.");
            System.exit(1);
        }
    }

    // EFFECTS: constructs a popup for user to notify them of message in specified container
    public void makeNotification(String message) {
        JOptionPane.showMessageDialog(null, message, "Notification", JOptionPane.INFORMATION_MESSAGE);
    }

    // EFFECTS: constructs a popup for user to alert them of something going wrong
    public void makeAlert(String message) {
        playSound(SOUND_ALERT);
        JOptionPane.showMessageDialog(null, message, "Alert", JOptionPane.ERROR_MESSAGE);
    }

    /*
    CITATION:
    https://docs.oracle.com/javase/tutorial/sound/playing.html
    https://docs.oracle.com/javase/tutorial/sound/accessing.html#113154
    https://docs.oracle.com/javase/tutorial/sound/converters.html
    https://stackoverflow.com/questions/15526255/best-way-to-get-sound-on-button-press-for-a-java-calculator
     */

    // EFFECTS: plays a sound with given file directory
    public void playSound(String sound) {
        try {
            File soundFile = new File(sound);
            AudioInputStream audio = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (Exception e) {
            //
        }
    }
}
