package sanguine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.PlayingCard;
import sanguine.model.SanguineModel;

/**
 * Represents the tests for the SanguineModel and ReadOnlySanguineModel interfaces.
 */
public class SanguineModelInterfaceTests {

  private SanguineModel model;
  private List<Card> deck;
  private boolean[][] simpleInfluence;

  /**
   * Setup for tests for the interfaces.
   */
  @Before
  public void setUp() {
    model = new BasicSanguine();

    simpleInfluence = new boolean[5][5];
    simpleInfluence[2][2] = true;
    simpleInfluence[2][3] = true;

    deck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      deck.add(new PlayingCard("Card" + i, 1, 1, simpleInfluence));
    }
  }

  @Test
  public void testStartGameValid() {
    model.startGame(deck, deck, 3, 5, 5);
    assertFalse(model.isGameOver());
    assertEquals(Player.RED, model.getCurrentPlayer());
  }

  @Test(expected = IllegalStateException.class)
  public void testStartGameTwice() {
    model.startGame(deck, deck, 3, 5, 5);
    model.startGame(deck, deck, 3, 5, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameInvalidRowsZero() {
    model.startGame(deck, deck, 0, 5, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameInvalidColumnsEven() {
    model.startGame(deck, deck, 3, 4, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameHandSizeTooLarge() {
    model.startGame(deck, deck, 3, 5, 10);
  }

  @Test
  public void testPlaceCardSwitchesPlayers() {
    model.startGame(deck, deck, 3, 5, 5);
    assertEquals(Player.RED, model.getCurrentPlayer());
    model.placeCard(0, 0, 0);
    assertEquals(Player.BLUE, model.getCurrentPlayer());
  }

  @Test(expected = IllegalStateException.class)
  public void testPlaceCardBeforeStart() {
    model.placeCard(0, 0, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlaceCardInvalidHandIndex() {
    model.startGame(deck, deck, 3, 5, 5);
    model.placeCard(99, 0, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlaceCardNegativeRow() {
    model.startGame(deck, deck, 3, 5, 5);
    model.placeCard(0, -1, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlaceCardOnEmptyCell() {
    model.startGame(deck, deck, 3, 5, 5);
    model.placeCard(0, 0, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlaceCardOnEnemyPawns() {
    model.startGame(deck, deck, 3, 5, 5);
    model.placeCard(0, 0, 4);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlaceCardNotEnoughPawns() {
    List<Card> expensiveDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      expensiveDeck.add(new PlayingCard("Exp" + i, 3, 5, simpleInfluence));
    }
    model.startGame(expensiveDeck, expensiveDeck, 3, 5, 5);
    model.placeCard(0, 0, 0);
  }

  @Test(expected = IllegalStateException.class)
  public void testPassTurnBeforeStart() {
    model.passTurn();
  }

  @Test
  public void testBothPlayersPassEndsGame() {
    model.startGame(deck, deck, 3, 5, 5);
    assertFalse(model.isGameOver());
    model.passTurn();
    assertFalse(model.isGameOver());
    model.passTurn();
    assertTrue(model.isGameOver());
  }

  @Test(expected = IllegalStateException.class)
  public void testGetCurrentPlayerAfterGameIsOver() {
    model.startGame(deck, deck, 3, 5, 5);
    model.passTurn();
    model.passTurn();
    model.getCurrentPlayer();
  }

  @Test
  public void testGetCurrentPlayerHandDefensiveCopy() {
    model.startGame(deck, deck, 3, 5, 5);
    List<Card> hand1 = model.getCurrentPlayerHand();
    List<Card> hand2 = model.getCurrentPlayerHand();
    assertNotSame(hand1, hand2);
  }

  @Test
  public void testGetCurrentDeckSize() {
    model.startGame(deck, deck, 3, 5, 5);
    int size = model.getCurrentDeckSize();
    assertTrue(size >= 0);
  }

  @Test
  public void testGetWinningPlayerWhereRedWins() {
    List<Card> scoreDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      scoreDeck.add(new PlayingCard("Card" + i, 1, 5, simpleInfluence));
    }

    model.startGame(scoreDeck, scoreDeck, 3, 5, 5);
    model.placeCard(0, 0, 0);
    model.passTurn();
    model.passTurn();

    assertEquals(Player.RED, model.getWinningPlayer());
  }

  @Test
  public void testGetWinningPlayerTie() {
    model.startGame(deck, deck, 3, 5, 5);
    model.passTurn();
    model.passTurn();
    assertNull(model.getWinningPlayer());
  }
}
