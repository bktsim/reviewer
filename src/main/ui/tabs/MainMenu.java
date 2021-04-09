package ui.tabs;

import model.Deck;
import ui.ActionButton;
import ui.Controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

import static ui.Controller.GUI_SIZE_X;
import static ui.Controller.GUI_SIZE_Y;

/*
Citations:
    NullPointerException on JOptionDialog:
    https://stackoverflow.com/questions/41256166/null-pointer-exception-when-joptionpane-showmessagedialog-is-cancelled

    JOptionPane:
    https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html

    LayoutManagers:
    https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html

    SmartHome (Implementation of JFrame, JPanel, and 'tabs', as well as ActionButton):
    https://github.com/UBCx-Software-Construction/long-form-problem-starters.git

    DefaultJComboBoxModel & JComboBox:
    https://stackoverflow.com/questions/10904639/how-to-refresh-the-jcombobox-data
 */

/* Main menu (UI) that allows user to access other menus and conduct basic operations */
public class MainMenu extends Tab {
    public static final String COMMAND_REVIEW = "review";
    public static final String COMMAND_EDIT = "edit";
    public static final String COMMAND_NEW_DECK = "new";
    public static final String COMMAND_DELETE_DECK = "delete";
    public static final String COMMAND_SAVE = "save";
    public static final String COMMAND_LOAD = "load";

    private DefaultComboBoxModel<Deck> deckComboBox;

    public MainMenu(Controller controller) {
        super(controller);
        this.deckComboBox = new DefaultComboBoxModel<>();
        initializeMainMenu();
    }

    // MODIFIES: this
    // EFFECTS: builds the main menu
    private void initializeMainMenu() {
        JPanel mainMenu = new JPanel();
        mainMenu.setLayout(new BoxLayout(mainMenu, BoxLayout.Y_AXIS));
        mainMenu.setSize(new Dimension(GUI_SIZE_X, GUI_SIZE_Y));
        JFrame mainframe = controller.getMainframe();

        initializeMainMenuTitle(mainMenu);
        initializeMainMenuDeckList(mainMenu);
        initializeMainMenuButtons(mainMenu);
        mainframe.add("Menu", mainMenu);
        mainframe.setContentPane(mainMenu);
        mainframe.setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: creates title for the main menu
    private static void initializeMainMenuTitle(Container mainMenu) {
        JLabel text = new JLabel("Reviewer");
        text.setFont(new Font("Serif", Font.BOLD, 36));
        text.setAlignmentX(Component.CENTER_ALIGNMENT);
        text.setBorder(new EmptyBorder(10,0,5,0));
        mainMenu.add(text);
    }

    // MODIFIES: this
    // EFFECTS: creates list of decks for user to select in main menu
    private void initializeMainMenuDeckList(Container mainMenu) {
        JPanel decksUI = new JPanel();
        decksUI.setBorder(new EmptyBorder(10,20,20,20));
        mainMenu.add(decksUI);

        List<Deck> decks = controller.getDecks();
        JComboBox<Deck> deckList = new JComboBox<>(deckComboBox);

        for (Deck d : decks) {
            deckComboBox.addElement(d);
        }

        // lambda: new itemListener that overrides the itemStateChanged event to update selectedDeck whenever
        //         the user selects something else in the list

        // MODIFIES: this
        // EFFECTS: update selectedDeck whenever user selects something else in the list.
        deckList.addItemListener(e -> controller.setSelectedDeck((Deck) deckList.getSelectedItem()));
        controller.setSelectedDeck(decks.size() != 0 ? decks.get(0) : null);
        decksUI.add(deckList);
    }

    // MODIFIES: this
    // EFFECTS: creates buttons that users can press to use the application
    private void initializeMainMenuButtons(Container mainMenu) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(10,10,10,10));
        buttonPanel.setLayout(new GridLayout(4, 1, 0, 5));
        buttonPanel.setMaximumSize(new Dimension(GUI_SIZE_X - (GUI_SIZE_X / 4), 120));

        ActionButton newDeckButton = new ActionButton(controller, "New Deck", buttonPanel);
        ActionButton reviewDeckButton = new ActionButton(controller, "Review Deck", buttonPanel);
        ActionButton editDeckButton = new ActionButton(controller, "Edit Deck", buttonPanel);
        ActionButton deleteDeckButton = new ActionButton(controller, "Delete Deck", buttonPanel);
        ActionButton saveDeckButton = new ActionButton(controller, "Save Decks", buttonPanel);
        ActionButton loadDeckButton = new ActionButton(controller, "Load Decks", buttonPanel);

        newDeckButton.setActionCommand(COMMAND_NEW_DECK);
        reviewDeckButton.setActionCommand(COMMAND_REVIEW);
        editDeckButton.setActionCommand(COMMAND_EDIT);
        deleteDeckButton.setActionCommand(COMMAND_DELETE_DECK);
        saveDeckButton.setActionCommand(COMMAND_SAVE);
        loadDeckButton.setActionCommand(COMMAND_LOAD);
        mainMenu.add(buttonPanel);
    }

    // MODIFIES: this, controller
    // EFFECTS: deletes selectedDeck if selectedDeck is not null and removes deck from visual UI.
    public void deleteDeck() {
        Deck selectedDeck = controller.getSelectedDeck();
        List<Deck> decks = controller.getDecks();
        if (selectedDeck != null) {
            try {
                decks.remove(selectedDeck);
                this.deckComboBox.removeElement(selectedDeck);
                controller.makeNotification("Deleted deck: " + selectedDeck.getName());
                selectedDeck = decks.size() != 0 ? decks.get(decks.size() - 1) : null;
                controller.setSelectedDeck(selectedDeck);
                deckComboBox.setSelectedItem(selectedDeck);
            } catch (Exception e) {
                controller.makeAlert("An error has occurred - please try again!");
            }
        } else {
            if (decks.size() == 0) {
                controller.makeAlert("You have no decks to delete - make a new deck!");
            } else {
                controller.makeAlert("Please select a deck to delete!");
            }
        }
    }

    // MODIFIES: this, controller
    // EFFECTS: creates a new deck and adds it if the name is valid. Sets new deck as selected deck in UI
    public void newDeck() {
        List<Deck> decks = controller.getDecks();
        String input = JOptionPane.showInputDialog("Please enter a name for the new deck").trim();
        if (input.isEmpty()) {
            controller.makeAlert("You cannot have a deck with an empty name!");
        } else {
            boolean deckOfSameNameExists = false;
            for (Deck d : decks) {
                if (d.getName().equals(input)) {
                    deckOfSameNameExists = true;
                    break;
                }
            }
            if (deckOfSameNameExists) {
                controller.makeAlert("You cannot have a deck with the same name as one of the existing decks!");
            } else {
                Deck deck = new Deck(input);
                decks.add(deck);
                deckComboBox.addElement(deck);
                controller.setSelectedDeck(deck);
                deckComboBox.setSelectedItem(deck);
                controller.makeNotification("New deck with name " + deck.getName() + " created!");
            }
        }
    }
}
