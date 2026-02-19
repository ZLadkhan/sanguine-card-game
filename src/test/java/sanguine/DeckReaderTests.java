package sanguine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import sanguine.model.Card;


/**
 * Represents the test class to test SanguineDeckReader functionality.
 */
public class DeckReaderTests {

  @Test
  public void testReadClashRoyaleDeck() throws IOException {
    String path = "docs" + File.separator + "clashRoyale.deck";
    File config = new File(path);

    DeckReader reader = new SanguineDeckReader();
    List<Card> deck = reader.readDeck(path);

    assertEquals(30, deck.size());
    assertEquals("Skeletons", deck.getFirst().getName());
    assertEquals(1, deck.getFirst().getCost());
    assertEquals(1, deck.getFirst().getValue());
  }

  @Test
  public void testReadNbaDeck() throws IOException {
    String path = "docs" + File.separator + "nba.deck";
    File config = new File(path);

    DeckReader reader = new SanguineDeckReader();
    List<Card> deck = reader.readDeck(path);

    assertEquals(21, deck.size());
    assertEquals("Butler", deck.getFirst().getName());
    assertEquals("LeBron", deck.get(20).getName());
    assertEquals(7, deck.get(20).getValue());
  }

  @Test
  public void testReadExampleDeck() throws IOException {
    String path = "docs" + File.separator + "example.deck";
    File config = new File(path);

    DeckReader reader = new SanguineDeckReader();
    List<Card> deck = reader.readDeck(path);

    assertTrue(deck.size() >= 15);
    assertEquals("Security", deck.getFirst().getName());
  }

  @Test(expected = IOException.class)
  public void testReadNonexistentFile() throws IOException {
    String path = "docs" + File.separator + "nonexistent.config";
    File config = new File(path);

    DeckReader reader = new SanguineDeckReader();
    reader.readDeck(path);
  }

  @Test
  public void testClashRoyaleDeckCardOrder() throws IOException {
    String path = "docs" + File.separator + "clashRoyale.deck";
    DeckReader reader = new SanguineDeckReader();
    List<Card> deck = reader.readDeck(path);

    assertEquals("ThreeMusketeers", deck.get(29).getName());
    assertEquals(3, deck.get(29).getCost());
    assertEquals(6, deck.get(29).getValue());
  }

  @Test
  public void testNbaDeckHasLegends() throws IOException {
    String path = "docs" + File.separator + "nba.deck";
    DeckReader reader = new SanguineDeckReader();
    List<Card> deck = reader.readDeck(path);

    boolean hasLeBron = false;
    boolean hasJordan = false;
    boolean hasKobe = false;

    for (Card card : deck) {
      if (card.getName().equals("LeBron")) {
        hasLeBron = true;
      }
      if (card.getName().equals("Jordan")) {
        hasJordan = true;
      }
      if (card.getName().equals("Kobe")) {
        hasKobe = true;
      }
    }
    assertTrue(hasLeBron);
    assertTrue(hasJordan);
    assertTrue(hasKobe);
  }

  @Test
  public void testExampleDeckHasDuplicates() throws IOException {
    String path = "docs" + File.separator + "example.deck";
    DeckReader reader = new SanguineDeckReader();
    List<Card> deck = reader.readDeck(path);

    int securityCount = 0;
    for (Card card : deck) {
      if (card.getName().equals("Security")) {
        securityCount++;
      }
    }

    assertEquals(2, securityCount);
  }

  @Test
  public void testClashRoyaleDeckInfluencePatterns() throws IOException {
    String path = "docs" + File.separator + "clashRoyale.deck";
    DeckReader reader = new SanguineDeckReader();
    List<Card> deck = reader.readDeck(path);

    Card threeMusketeers = deck.get(29);
    assertTrue(threeMusketeers.hasInfluenceAt(2, 2, false));
    assertTrue(threeMusketeers.hasInfluenceAt(1, 1, false));
    assertTrue(threeMusketeers.hasInfluenceAt(1, 3, false));
  }

  @Test
  public void testNbaDeckCostRange() throws IOException {
    String path = "docs" + File.separator + "nba.deck";
    DeckReader reader = new SanguineDeckReader();
    List<Card> deck = reader.readDeck(path);

    for (Card card : deck) {
      assertTrue(card.getCost() >= 1 && card.getCost() <= 3);
      assertTrue(card.getValue() > 0);
    }
  }

  @Test
  public void testDeckWithMinimumCards() throws IOException {
    String path = "docs" + File.separator + "example.deck";
    DeckReader reader = new SanguineDeckReader();
    List<Card> deck = reader.readDeck(path);

    assertTrue(deck.size() >= 15);
  }
}
