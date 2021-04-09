package ui;

import java.io.FileNotFoundException;

/* Launch the application */
public class Main {
    public static void main(String[] args) {
        try {
            new Controller();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to run application - file not found.");
            System.exit(0);
        }
    }
}
