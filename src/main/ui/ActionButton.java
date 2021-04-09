package ui;

import ui.tabs.EditMenu;
import ui.tabs.MainMenu;
import ui.tabs.ReviewMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static ui.Controller.*;

/* Class to manage button actions in main menu */
public class ActionButton implements ActionListener {
    private Controller controller;
    private JButton button;

    public ActionButton(Controller controller, String text, Container container) {
        this.controller = controller;
        this.button = new JButton(text);
        this.button.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.button.setAlignmentY(Component.TOP_ALIGNMENT);
        this.button.setMaximumSize(new Dimension(GUI_SIZE_X - (GUI_SIZE_X / 4), 20));
        this.button.setSize(GUI_SIZE_X, 20);
        this.button.addActionListener(this);
        container.add(this.button);
    }

    // MODIFIES: this
    // EFFECTS: sets the action command for the button
    public void setActionCommand(String text) {
        this.button.setActionCommand(text);
    }

    // MODIFIES: controller
    // EFFECTS: starts action for controller/the application
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (MainMenu.COMMAND_REVIEW.equals(action)) {
            controller.reviewDeck();
        } else if (MainMenu.COMMAND_EDIT.equals(action)) {
            controller.editDeck();
        } else if (MainMenu.COMMAND_NEW_DECK.equals(action)) {
            controller.newDeck();
        } else if (MainMenu.COMMAND_DELETE_DECK.equals(action)) {
            controller.deleteDeck();
        } else if (MainMenu.COMMAND_SAVE.equals(action)) {
            controller.saveDecks();
        } else if (MainMenu.COMMAND_LOAD.equals(action)) {
            controller.loadDecks();
        } else {
            actionPerformedExtension(e);
        }
    }

    // MODIFIES: controller
    // EFFECTS: starts action for controller/the application
    private void actionPerformedExtension(ActionEvent e) {
        String action = e.getActionCommand();
        if (ReviewMenu.COMMAND_REVEAL.equals(action)) {
            controller.reviewDeckAction(ReviewMenu.COMMAND_REVEAL);
        } else if (ReviewMenu.COMMAND_ANSWER_CORRECT.equals(action)) {
            controller.reviewDeckAction(ReviewMenu.COMMAND_ANSWER_CORRECT);
        } else if (ReviewMenu.COMMAND_ANSWER_INCORRECT.equals(action)) {
            controller.reviewDeckAction(ReviewMenu.COMMAND_ANSWER_INCORRECT);
        } else if (EditMenu.COMMAND_NEW_CARD.equals(action)) {
            controller.editDeckAction(EditMenu.COMMAND_NEW_CARD);
        } else if (EditMenu.COMMAND_DELETE_CARD.equals(action)) {
            controller.editDeckAction(EditMenu.COMMAND_DELETE_CARD);
        } else if (EditMenu.COMMAND_RETURN_TO_MENU.equals(action)) {
            controller.editDeckAction(EditMenu.COMMAND_RETURN_TO_MENU);
        }
    }
}
