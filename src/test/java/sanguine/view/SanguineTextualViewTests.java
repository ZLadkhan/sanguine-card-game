package sanguine.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.model.PlayingCard;

/**
 * Represents the class for testing SanguineTextualView.
 */
public class SanguineTextualViewTests {

  private BasicSanguine model;
  private SanguineTextualView view;
  private List<Card> deck;

  /**
   * Set up for tests on the view.
   */
  @Before
  public void setUp() {
    model = new BasicSanguine();

    boolean[][] influence = new boolean[5][5];
    influence[2][2] = true;

    deck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      deck.add(new PlayingCard("Card" + i, 1, 1, influence));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullModel() {
    new SanguineTextualView(null);
  }

  @Test
  public void testToStringInitialBoard() {
    model.startGame(deck, deck, 3, 5, 5);
    view = new SanguineTextualView(model);

    String output = view.toString();
    String[] lines = output.split("\n");
    assertEquals(3, lines.length);

    for (String line : lines) {
      assertTrue(line.matches("0 1___1 0"));
    }
  }

  @Test
  public void testToStringShowsRedCard() {
    model.startGame(deck, deck, 3, 5, 5);
    view = new SanguineTextualView(model);

    model.placeCard(0, 0, 0);

    String output = view.toString();
    assertTrue(output.contains("R"));
  }

  @Test
  public void testToStringShowsBlueCard() {
    model.startGame(deck, deck, 3, 5, 5);
    view = new SanguineTextualView(model);

    model.passTurn();
    model.placeCard(0, 0, 4);

    String output = view.toString();
    assertTrue(output.contains("B"));
  }

  @Test
  public void testToStringShowsPawnCounts() {
    model.startGame(deck, deck, 3, 5, 5);
    view = new SanguineTextualView(model);

    String output = view.toString();
    assertTrue(output.contains("1"));
  }

  @Test
  public void testToStringShowsEmptyCells() {
    model.startGame(deck, deck, 3, 5, 5);
    view = new SanguineTextualView(model);

    String output = view.toString();
    assertTrue(output.contains("_"));
  }

  @Test
  public void testToStringShowsRowScores() {
    List<Card> valueDeck = new ArrayList<>();
    boolean[][] influence = new boolean[5][5];
    influence[2][2] = true;

    for (int i = 0; i < 20; i++) {
      valueDeck.add(new PlayingCard("Card" + i, 1, 3, influence));
    }

    model.startGame(valueDeck, valueDeck, 3, 5, 5);
    view = new SanguineTextualView(model);

    model.placeCard(0, 0, 0);

    String output = view.toString();
    String[] lines = output.split("\n");
    assertTrue(lines[0].startsWith("3 "));
  }

  @Test
  public void testToStringNoTrailingNewline() {
    model.startGame(deck, deck, 3, 5, 5);
    view = new SanguineTextualView(model);

    String output = view.toString();
    assertFalse(output.endsWith("\n"));
  }

  @Test
  public void testToStringCorrectColumnCount() {
    model.startGame(deck, deck, 3, 7, 5);
    view = new SanguineTextualView(model);

    String output = view.toString();
    String[] lines = output.split("\n");

    for (String line : lines) {
      String[] parts = line.split(" ");
      assertEquals(7, parts[1].length());
    }
  }

  @Test
  public void testRenderToAppendable() throws IOException {
    model.startGame(deck, deck, 3, 5, 5);
    view = new SanguineTextualView(model);

    StringBuilder output = new StringBuilder();
    view.render(output);

    assertTrue(output.toString().contains("1___1"));
  }

  @Test(expected = IOException.class)
  public void testRenderThrowsIoException() throws IOException {
    model.startGame(deck, deck, 3, 5, 5);
    view = new SanguineTextualView(model);

    Appendable badAppendable = new Appendable() {
      public Appendable append(CharSequence csq) throws IOException {
        throw new IOException("Test");
      }

      public Appendable append(CharSequence csq, int start, int end) throws IOException {
        throw new IOException("Test");
      }

      public Appendable append(char c) throws IOException {
        throw new IOException("Test");
      }
    };

    view.render(badAppendable);
  }
}
