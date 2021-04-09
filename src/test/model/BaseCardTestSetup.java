package model;

import exceptions.InvalidCardException;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class BaseCardTestSetup {
    protected Card c1;
    protected Card c2;
    protected Card c3;
    protected Card c4;
    protected Card c5;

    @BeforeEach
    void setup() {
        try {
            c1 = new Card("What is the powerhouse of the cell?", "Mitochondria");
            c2 = new Card("What is 1+1?", "2");
            c3 = new Card("Where is UBC located?", "Vancouver, BC");
            c4 = new Card("What is the name of this application?", "Flashcards");
            c5 = new Card("Consider f(x) = x^2 + 2x + 1. What are the roots of this function?", "x=-1");
        } catch (InvalidCardException e) {
            fail("No exception expected");
        }
    }
}
