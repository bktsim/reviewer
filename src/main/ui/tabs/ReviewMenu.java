package ui.tabs;

import model.Card;
import model.Deck;
import exceptions.ExceedThresholdException;
import ui.ActionButton;
import ui.Controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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

/* Review menu (UI) that allows user to review their decks */
public class ReviewMenu extends Tab {
    public static final int CORRECT_POINTS = 1;
    public static final int INCORRECT_POINTS = -1;
    public static final String COMMAND_ANSWER_CORRECT = "yes";
    public static final String COMMAND_ANSWER_INCORRECT = "no";
    public static final String COMMAND_REVEAL = "reveal";

    private static final String FRAME_FRONT_INSTRUCTIONS = "front";
    private static final String FRAME_BACK_INSTRUCTIONS = "back";
    private static final String FRAME_OK_BUTTON = "ok";
    private static final String FRAME_RESPONSE_BUTTONS = "response";

    private static final String FRONT_INSTRUCTIONS = "Click 'OK' when you are ready to see the back of the card!";
    private static final String BACK_INSTRUCTIONS = "Did you get it right? Click 'YES' or 'NO'!";

    private Deck selectedDeck;
    private int cardNum;
    private Card selectedCard;
    private JPanel instructions;
    private JPanel contentPanel;
    private JPanel buttons;

    // REQUIRES: selectedDeck != null, selectedDeck.getNumOfCards() >= 1.
    public ReviewMenu(Controller controller) {
        super(controller);
        this.cardNum = 1;
        this.selectedDeck = controller.getSelectedDeck();
        this.selectedCard = controller.getSelectedDeck().getNthCard(1);
        initializeReviewMenu();
    }

    // MODIFIES: this
    // EFFECTS: creates the overall review menu for reviewing cards
    private void initializeReviewMenu() {
        JPanel reviewMenu = new JPanel();
        reviewMenu.setLayout(new BoxLayout(reviewMenu, BoxLayout.Y_AXIS));
        reviewMenu.setSize(new Dimension(GUI_SIZE_X, GUI_SIZE_Y));
        JFrame mainframe = controller.getMainframe();

        initializeReviewMenuTitle(reviewMenu);
        initializeReviewMenuCardLayout(reviewMenu);
        initializeReviewMenuInstructions(reviewMenu);
        initializeReviewMenuButtons(reviewMenu);

        mainframe.add(reviewMenu, BorderLayout.CENTER);
        mainframe.setContentPane(reviewMenu);
        reviewMenu.setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: creates the title for the menu
    private void initializeReviewMenuTitle(Container reviewMenu) {
        JLabel text = new JLabel("Reviewing Deck: " + selectedDeck.getName());
        text.setFont(new Font("Serif", Font.BOLD, 24));
        text.setAlignmentX(Component.CENTER_ALIGNMENT);
        text.setBorder(new EmptyBorder(10,0,5,0));
        reviewMenu.add(text);
    }

    // MODIFIES: this
    // EFFECTS: creates a smaller text label for showing instructions
    private void initializeReviewMenuInstructions(Container reviewMenu) {
        JPanel instructions = new JPanel(new CardLayout());
        instructions.setMaximumSize(new Dimension(GUI_SIZE_X, 20));

        this.instructions = instructions;
        JLabel front = new JLabel(FRONT_INSTRUCTIONS, SwingConstants.CENTER);
        JLabel back = new JLabel(BACK_INSTRUCTIONS, SwingConstants.CENTER);
        instructions.add(front, FRAME_FRONT_INSTRUCTIONS);
        instructions.add(back, FRAME_BACK_INSTRUCTIONS);
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructions.setAlignmentY(Component.TOP_ALIGNMENT);
        instructions.setBorder(new EmptyBorder(5, 0, 5, 0));

        reviewMenu.add(instructions);
        instructions.setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: sets up the information (card front/back) to show for this review session
    private void initializeReviewMenuCardLayout(Container reviewMenu) {
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setMaximumSize(new Dimension(GUI_SIZE_X, 140));
        contentPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        for (int i = 1; i <= selectedDeck.getNumOfCards(); i++) {
            Card c = selectedDeck.getNthCard(i);
            JLabel front = new JLabel(c.getFront(), SwingConstants.CENTER);
            JLabel back = new JLabel(c.getBack(), SwingConstants.CENTER);

            contentPanel.add(front);
            contentPanel.add(back);
        }
        this.contentPanel = contentPanel;
        reviewMenu.add(contentPanel);
    }

    // MODIFIES: this
    // EFFECTS: creates buttons for user to interact with during review session
    private void initializeReviewMenuButtons(Container reviewMenu) {
        JPanel buttonsUI = new JPanel(new CardLayout());
        buttonsUI.setMaximumSize(new Dimension(GUI_SIZE_X - (GUI_SIZE_X / 4), 20));

        JPanel okButtonUI = new JPanel(new GridLayout(1, 1, 0, 0));
        ActionButton okButton = new ActionButton(controller, "OK", okButtonUI);
        okButton.setActionCommand(COMMAND_REVEAL);

        JPanel responseUI = new JPanel(new GridLayout(1, 2, 5, 0));
        ActionButton yesButton = new ActionButton(controller, "Yes", responseUI);
        ActionButton noButton = new ActionButton(controller, "No", responseUI);
        yesButton.setActionCommand(COMMAND_ANSWER_CORRECT);
        noButton.setActionCommand(COMMAND_ANSWER_INCORRECT);

        buttonsUI.add(okButtonUI, FRAME_OK_BUTTON);
        buttonsUI.add(responseUI, FRAME_RESPONSE_BUTTONS);
        reviewMenu.add(buttonsUI);
        this.buttons = buttonsUI;
    }


    // MODIFIES: this
    // EFFECTS: if response == 1; then answer is correct. add point to card.
    //             alert player if player reaches threshold (mastered the card)
    //          if response == 0; then answer is incorrect. remove point from card.
    //             alert player if player reaches threshold (card at lowest possible level of mastery)
    //          move to next card if there are still cards in the deck. Otherwise, end session and return to Menu.
    public void answer(int response) {
        if (response == 1) {
            try {
                selectedCard.changeScoreBy(CORRECT_POINTS);
            } catch (ExceedThresholdException e) {
                controller.makeNotification("You have mastered this card!");
                controller.playSound(controller.SOUND_REVIEW);
            }
        } else if (response == 0) {
            try {
                selectedCard.changeScoreBy(INCORRECT_POINTS);
            } catch (ExceedThresholdException e) {
                controller.makeAlert("Try to pay extra attention to this card! You seem to have trouble with it.");
            }
        }
        if (cardNum + 1 <= selectedDeck.getNumOfCards()) {
            this.cardNum += 1;
            this.selectedCard = selectedDeck.getNthCard(cardNum);
            showNextCardFrame();
        } else {
            controller.endReview();
        }
    }

    // MODIFIES: this
    // EFFECTS: changes information on panel to show new information for next step of review
    public void showNextCardFrame() {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.next(contentPanel);

        CardLayout cardLayout2 = (CardLayout) instructions.getLayout();
        cardLayout2.next(instructions);

        CardLayout cardLayout3 = (CardLayout) buttons.getLayout();
        cardLayout3.next(buttons);
    }
}
