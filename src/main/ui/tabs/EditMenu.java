package ui.tabs;

import exceptions.InvalidCardException;
import model.Card;
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

/* Edit menu (UI) that allows user to edit the contents of the deck */
public class EditMenu extends Tab {
    public static final String COMMAND_NEW_CARD = "newCard";
    public static final String COMMAND_DELETE_CARD = "deleteCard";
    public static final String COMMAND_RETURN_TO_MENU = "returnToMenu";

    private Deck selectedDeck;
    private DefaultComboBoxModel<Card> cardComboBox;
    private Card selectedCard;

    // REQUIRES: selectedDeck != null
    public EditMenu(Controller controller) {
        super(controller);
        this.cardComboBox = new DefaultComboBoxModel<>();
        this.selectedDeck = controller.getSelectedDeck();
        initializeEditMenu();
    }

    // MODIFIES: this
    // EFFECTS: constructs the entire menu
    private void initializeEditMenu() {
        JPanel editMenu = new JPanel();
        editMenu.setLayout(new BoxLayout(editMenu, BoxLayout.Y_AXIS));
        editMenu.setSize(new Dimension(GUI_SIZE_X, GUI_SIZE_Y));
        JFrame mainframe = controller.getMainframe();

        initializeEditMenuTitle(editMenu);
        initializeEditMenuCardList(editMenu);
        initializeEditMenuButtons(editMenu);

        mainframe.add("Edit", editMenu);
        mainframe.setContentPane(editMenu);
        mainframe.setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: constructs title of menu and adds to the overall panel
    private void initializeEditMenuTitle(Container editMenu) {
        JLabel text = new JLabel("Editing " + selectedDeck.getName());
        text.setFont(new Font("Serif", Font.BOLD, 24));
        text.setAlignmentX(Component.CENTER_ALIGNMENT);
        text.setBorder(new EmptyBorder(10,0,5,0));
        editMenu.add(text);
    }

    // MODIFIES: this
    // EFFECTS: constructs buttons capable of performing functions, such as delete card & deck to panel
    private void initializeEditMenuButtons(Container editMenu) {
        JPanel buttonsUI = new JPanel();
        JPanel buttonsTop = new JPanel(new GridLayout(1, 3, 5, 0));
        buttonsTop.setMaximumSize(new Dimension(GUI_SIZE_X - (GUI_SIZE_X / 4), 20));

        ActionButton newCardButton = new ActionButton(controller, "New Card", buttonsTop);
        ActionButton deleteCardButton = new ActionButton(controller, "Delete Card", buttonsTop);
        buttonsUI.add(buttonsTop);
        ActionButton returnToMenuButton = new ActionButton(controller, "Return to Menu", buttonsUI);

        newCardButton.setActionCommand(COMMAND_NEW_CARD);
        deleteCardButton.setActionCommand(COMMAND_DELETE_CARD);
        returnToMenuButton.setActionCommand(COMMAND_RETURN_TO_MENU);

        editMenu.add(buttonsUI);
    }

    // MODIFIES: this
    // EFFECTS: creates list of decks for user to select in main menu.
    //          sets selectedCard to first card (or null if no cards exist in deck)
    private void initializeEditMenuCardList(Container editMenu) {
        JPanel cardsUI = new JPanel();
        cardsUI.setBorder(new EmptyBorder(10,20,20,20));
        editMenu.add(cardsUI);

        List<Card> cards = selectedDeck.getCards();

        JComboBox<Card> cardList = new JComboBox<>(cardComboBox);

        for (Card c : cards) {
            cardComboBox.addElement(c);
        }

        // lambda: new itemListener that overrides the itemStateChanged event to update selectedCard whenever
        //         the user selects something else in the list

        // MODIFIES: this
        // EFFECTS: update selectedCard whenever user selects something else in the list.
        cardList.addItemListener(e -> EditMenu.this.selectedCard = (Card) cardList.getSelectedItem());

        this.selectedCard = cards.size() != 0 ? cards.get(0) : null;
        cardsUI.add(cardList);
    }

    // MODIFIES: this, selectedDeck
    // EFFECTS: creates a new card from user input
    public void newCard() {
        try {
            boolean newCardSuccessful = false;
            while (!newCardSuccessful) {
                String front = JOptionPane.showInputDialog("Please enter the front side of the card").trim();
                String back = JOptionPane.showInputDialog("Please enter the back side for the new deck").trim();
                try {
                    Card c = new Card(front, back);
                    this.selectedDeck.addCard(c);
                    this.selectedCard = c;
                    cardComboBox.addElement(c);
                    cardComboBox.setSelectedItem(c);
                    controller.makeNotification("Card created!");
                    newCardSuccessful = true;
                } catch (InvalidCardException e) {
                    controller.makeAlert("Your front-side and back-side cannot be empty!");
                }
            }
        } catch (NullPointerException e) {
            // Do nothing - user cancelled input
        }
    }

    // MODIFIES: this, selectedCard, selectedDeck
    // EFFECTS: deletes selectedCard from the selectedDeck if selectedCard != null.
    public void deleteSelectedCard() {
        if (this.selectedCard == null) {
            controller.makeAlert("You need to select a card to delete!");
        } else {
            try {
                List<Card> cards = selectedDeck.getCards();
                cards.remove(this.selectedCard);
                controller.makeNotification("Removed card '" + this.selectedCard.getFront() + "'");
                cardComboBox.removeElement(this.selectedCard);
                this.selectedCard = cards.size() != 0 ? cards.get(cards.size() - 1) : null;
                cardComboBox.setSelectedItem(this.selectedCard);
            } catch (Exception e) {
                controller.makeAlert("An error has occurred. Please try again!");
            }
        }
    }
}
