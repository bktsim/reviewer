package ui.tabs;

import ui.Controller;

import javax.swing.*;

/*
Citations:
    Tab setup
    https://github.com/UBCx-Software-Construction/long-form-problem-starters/tree/master/SmartHome
*/

/* Default setup for all tabs */
public abstract class Tab extends JPanel {
    protected Controller controller;

    public Tab(Controller controller) {
        this.controller = controller;
    }
}
