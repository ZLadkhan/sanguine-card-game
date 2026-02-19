package sanguine.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Represents the test class for testing the model, card, and cell in the Sanguine game board.
 */
public class SanguineModelTests {

  private boolean[][] simpleInfluence;
  private boolean[][] crossInfluence;
  private BasicSanguine model;
  private List<Card> deck;

  /**
   * Represents creating a deck with associated influence patterns for testing purposes.
   */
  @Before
  public void setUp() {
    simpleInfluence = new boolean[5][5];
    simpleInfluence[2][2] = true;
    simpleInfluence[2][3] = true;

    crossInfluence = new boolean[5][5];
    crossInfluence[2][2] = true;
    crossInfluence[1][2] = true;
    crossInfluence[3][2] = true;
    crossInfluence[2][1] = true;
    crossInfluence[2][3] = true;

    model = new BasicSanguine();
    deck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      deck.add(new PlayingCard("Card" + i, 1, 1, simpleInfluence));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayingCardInvalidCostTooLow() {
    new PlayingCard("Invalid", 0, 2, simpleInfluence);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayingCardInvalidCostTooHigh() {
    new PlayingCard("Invalid", 4, 2, simpleInfluence);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayingCardInvalidValueZero() {
    new PlayingCard("Invalid", 2, 0, simpleInfluence);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayingCardInvalidGridNoCenterInfluence() {
    boolean[][] invalid = new boolean[5][5];
    new PlayingCard("Invalid", 2, 2, invalid);
  }

  @Test
  public void testPlayingCardHasInfluenceAtRedPlayer() {
    Card card = new PlayingCard("Test", 1, 1, crossInfluence);
    assertTrue(card.hasInfluenceAt(2, 2, false));
    assertTrue(card.hasInfluenceAt(1, 2, false));
    assertFalse(card.hasInfluenceAt(0, 0, false));
  }

  @Test
  public void testPlayingCardHasInfluenceAtBluePlayerMirrored() {
    Card card = new PlayingCard("Test", 1, 1, crossInfluence);
    assertTrue(card.hasInfluenceAt(2, 2, true));
    assertTrue(card.hasInfluenceAt(2, 1, true));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayingCardHasInfluenceAtInvalidRow() {
    Card card = new PlayingCard("Test", 1, 1, simpleInfluence);
    card.hasInfluenceAt(-1, 2, false);
  }

  @Test
  public void testPlayingCardEquals() {
    Card card1 = new PlayingCard("Knight", 2, 3, crossInfluence);
    Card card2 = new PlayingCard("Knight", 2, 3, crossInfluence);
    assertEquals(card1, card2);
  }

  @Test
  public void testPlayingCardNotEqualsDifferentInfluence() {
    Card card1 = new PlayingCard("Knight", 2, 3, crossInfluence);
    Card card2 = new PlayingCard("Knight", 2, 3, simpleInfluence);
    assertNotEquals(card1, card2);
  }

  @Test
  public void testGameCellAddPawnMaxThree() {
    GameCell c = new GameCell();
    c.addPawn(Player.RED);
    c.addPawn(Player.RED);
    c.addPawn(Player.RED);
    c.addPawn(Player.RED);
    assertEquals(3, c.getPawnCount());
  }

  @Test
  public void testGameCellConvertPawns() {
    GameCell c = new GameCell();
    c.addPawn(Player.RED);
    c.addPawn(Player.RED);
    c.convertPawns(Player.BLUE);
    assertEquals(2, c.getPawnCount());
    assertEquals(Player.BLUE, c.getPawnOwner());
  }

  @Test
  public void testGameCellPlaceCardRemovesPawns() {
    GameCell c = new GameCell();
    c.addPawn(Player.RED);
    c.placeCard(new PlayingCard("Test", 1, 1, simpleInfluence), Player.RED);
    assertFalse(c.hasPawns());
    assertNotNull(c.getCard());
  }

  @Test
  public void testBasicSanguineInitializesPawnsCorrectly() {
    model.startGame(deck, deck, 3, 5, 5);

    Cell redCell = model.getCellAt(0, 0);
    assertEquals(Player.RED, redCell.getPawnOwner());
    assertEquals(1, redCell.getPawnCount());

    Cell blueCell = model.getCellAt(0, 4);
    assertEquals(Player.BLUE, blueCell.getPawnOwner());
  }

  @Test
  public void testBasicSanguineGetGameBoardDimensions() {
    model.startGame(deck, deck, 4, 7, 5);
    int[] dims = model.getGameBoardDimensions();
    assertEquals(4, dims[0]);
    assertEquals(7, dims[1]);
  }

  @Test
  public void testBasicSanguineInfluenceAppliesCorrectly() {
    model.startGame(deck, deck, 3, 5, 5);
    model.placeCard(0, 0, 0);

    Cell influenced = model.getCellAt(0, 1);
    assertTrue(influenced.hasPawns());
    assertEquals(Player.RED, influenced.getPawnOwner());
  }

  @Test
  public void testBasicSanguineInfluenceDoesNotAffectCards() {
    model.startGame(deck, deck, 3, 5, 5);
    model.placeCard(0, 0, 0);

    Cell cardCell = model.getCellAt(0, 0);
    assertNotNull(cardCell.getCard());
    assertEquals(Player.RED, cardCell.getCardOwner());
  }

  @Test
  public void testBasicSanguineScoringRedWins() {
    List<Card> scoreDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      scoreDeck.add(new PlayingCard("Card" + i, 1, 3, simpleInfluence));
    }

    model.startGame(scoreDeck, scoreDeck, 3, 5, 5);
    model.placeCard(0, 0, 0);
    model.passTurn();
    model.passTurn();

    assertTrue(model.isGameOver());
    int[] scores = model.getFinalScores();
    assertTrue(scores[0] > 0);
    assertEquals(0, scores[1]);
    assertEquals(Player.RED, model.getWinner());
  }

  @Test
  public void testBasicSanguineScoringTie() {
    model.startGame(deck, deck, 3, 5, 5);
    model.passTurn();
    model.passTurn();

    int[] scores = model.getFinalScores();
    assertEquals(0, scores[0]);
    assertEquals(0, scores[1]);
    assertNull(model.getWinner());
  }

  @Test
  public void testBasicSanguineInfluenceAddsToFriendlyPawns() {
    boolean[][] upInfluence = new boolean[5][5];
    upInfluence[2][2] = true;
    upInfluence[1][2] = true;

    List<Card> influenceDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      influenceDeck.add(new PlayingCard("Card" + i, 1, 1, upInfluence));
    }

    model.startGame(influenceDeck, influenceDeck, 3, 5, 5);

    Cell targetCell = model.getCellAt(0, 0);
    assertEquals(1, targetCell.getPawnCount());

    model.placeCard(0, 1, 0);

    Cell afterCell = model.getCellAt(0, 0);
    assertEquals(2, afterCell.getPawnCount());
    assertEquals(Player.RED, afterCell.getPawnOwner());
  }

  @Test
  public void testIsLegalMoveValid() {
    model.startGame(deck, deck, 3, 5, 5);
    assertTrue(model.isLegalMove(0, 0, 0));
  }

  @Test
  public void testIsLegalMoveInvalidHandIndex() {
    model.startGame(deck, deck, 3, 5, 5);
    assertFalse(model.isLegalMove(99, 0, 0));
  }

  @Test
  public void testIsLegalMoveEmptyCell() {
    model.startGame(deck, deck, 3, 5, 5);
    assertFalse(model.isLegalMove(0, 0, 2));
  }

  @Test
  public void testIsLegalMoveEnemyPawns() {
    model.startGame(deck, deck, 3, 5, 5);
    assertFalse(model.isLegalMove(0, 0, 4));
  }

  @Test
  public void testIsLegalMoveBeforeGameStart() {
    assertFalse(model.isLegalMove(0, 0, 0));
  }

  @Test
  public void testGetRowScoreNoCards() {
    model.startGame(deck, deck, 3, 5, 5);
    int[] scores = model.getRowScore(0);
    assertEquals(0, scores[0]);
    assertEquals(0, scores[1]);
  }

  @Test
  public void testBluePlayerInfluenceMirrored() {
    boolean[][] rightInfluence = new boolean[5][5];
    rightInfluence[2][2] = true;
    rightInfluence[2][3] = true;

    List<Card> influenceDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      influenceDeck.add(new PlayingCard("Card" + i, 1, 1, rightInfluence));
    }

    model.startGame(influenceDeck, influenceDeck, 3, 5, 5);

    model.passTurn();
    model.placeCard(0, 0, 4);

    Cell leftCell = model.getCellAt(0, 3);
    assertTrue(leftCell.hasPawns());
    assertEquals(Player.BLUE, leftCell.getPawnOwner());
  }

  @Test
  public void testGetRowScoreWithRedCard() {
    List<Card> scoreDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      scoreDeck.add(new PlayingCard("Card" + i, 1, 3, simpleInfluence));
    }

    model.startGame(scoreDeck, scoreDeck, 3, 5, 5);
    model.placeCard(0, 0, 0);

    int[] scores = model.getRowScore(0);
    assertEquals(3, scores[0]);
    assertEquals(0, scores[1]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetRowScoreInvalidRow() {
    model.startGame(deck, deck, 3, 5, 5);
    model.getRowScore(10);
  }

  @Test(expected = IllegalStateException.class)
  public void testGetRowScoreBeforeGameStart() {
    model.getRowScore(0);
  }
}
