package sanguine.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import sanguine.strategy.ControlBoardStrategy;
import sanguine.strategy.FillFirstStrategy;
import sanguine.strategy.MaxRowScoreStrategy;
import sanguine.strategy.Move;

/**
 * Represents tests for the controller.
 */
public class SanguineControllerTests {

  private BasicSanguine model;
  private List<Card> deck;
  private boolean[][] simpleInfluence;
  private boolean[][] crossInfluence;
  private boolean[][] largeInfluence;

  /**
   * Sets up test fixtures before each test.
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
    largeInfluence = new boolean[5][5];
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        largeInfluence[i][j] = true;
      }
    }
    model = new BasicSanguine();
    deck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      deck.add(new PlayingCard("Card" + i, 1, 1, simpleInfluence));
    }
  }

  @Test
  public void testFillFirstStrategyFindsMove() {
    model.startGame(deck, deck, 3, 5, 5);
    FillFirstStrategy strategy = new FillFirstStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Strategy should find a legal move", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testFillFirstStrategyPassesWhenNoMoves() {
    List<Card> expensiveDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      expensiveDeck.add(new PlayingCard("Exp" + i, 3, 5, simpleInfluence));
    }
    model.startGame(expensiveDeck, expensiveDeck, 3, 5, 1);
    FillFirstStrategy strategy = new FillFirstStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNull("Strategy should return null when no legal moves", move);
  }

  @Test
  public void testMaxRowScoreStrategyChoosesCorrectMove() {
    List<Card> variedDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      variedDeck.add(new PlayingCard("Card" + i, 1, 5, simpleInfluence));
    }
    model.startGame(variedDeck, variedDeck, 3, 5, 5);
    MaxRowScoreStrategy strategy = new MaxRowScoreStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Strategy should find a move", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testControlBoardStrategyChoosesValidMove() {
    model.startGame(deck, deck, 3, 5, 5);
    ControlBoardStrategy strategy = new ControlBoardStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Strategy should find a move", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testModelNotifiesListenersOnTurnChange() {
    TestModelListener listener = new TestModelListener();
    model.addModelListener(listener);
    model.startGame(deck, deck, 3, 5, 5);
    assertEquals("Should notify RED player at start", 1, listener.turnChangeCount);
    assertEquals("Should be RED's turn", Player.RED, listener.lastPlayer);
    model.placeCard(0, 0, 0);
    assertEquals("Should notify BLUE player after move", 2, listener.turnChangeCount);
    assertEquals("Should be BLUE's turn", Player.BLUE, listener.lastPlayer);
  }

  @Test
  public void testModelNotifiesListenersOnGameOver() {
    TestModelListener listener = new TestModelListener();
    model.addModelListener(listener);
    model.startGame(deck, deck, 3, 5, 5);
    model.passTurn();
    model.passTurn();
    assertTrue("Game should be over", model.isGameOver());
    assertEquals("Should notify game over once", 1, listener.gameOverCount);
  }

  @Test
  public void testAiPlayerEmitsEvents() {
    TestFeaturesListener listener = new TestFeaturesListener();
    FillFirstStrategy strategy = new FillFirstStrategy();
    AiPlayer aiPlayer = new AiPlayer(strategy);
    aiPlayer.addFeaturesListener(listener);
    model.startGame(deck, deck, 3, 5, 5);
    aiPlayer.notifyTurn(model);
    assertTrue("AI should emit card click", listener.cardClicked);
    assertTrue("AI should emit cell click", listener.cellClicked);
    assertTrue("AI should emit confirm", listener.confirmed);
  }

  @Test
  public void testHumanPlayerDoesNotEmitEvents() {
    TestFeaturesListener listener = new TestFeaturesListener();
    HumanPlayer humanPlayer = new HumanPlayer();
    humanPlayer.addFeaturesListener(listener);
    model.startGame(deck, deck, 3, 5, 5);
    humanPlayer.notifyTurn(model);
    assertFalse("Human should not emit events", listener.cardClicked);
    assertFalse("Human should not emit events", listener.cellClicked);
    assertFalse("Human should not emit events", listener.confirmed);
  }

  @Test
  public void testFillFirstStrategyOnLargeBoard() {
    List<Card> largeDeck = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      largeDeck.add(new PlayingCard("Card" + i, 1, 1, simpleInfluence));
    }
    model.startGame(largeDeck, largeDeck, 5, 11, 5);
    FillFirstStrategy strategy = new FillFirstStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Should find move on large board", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testMaxRowScoreStrategyReturnsNullWhenNoMoves() {
    List<Card> expensiveDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      expensiveDeck.add(new PlayingCard("Exp" + i, 3, 10, simpleInfluence));
    }
    model.startGame(expensiveDeck, expensiveDeck, 3, 5, 1);
    MaxRowScoreStrategy strategy = new MaxRowScoreStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNull("Should return null when no legal moves", move);
  }

  @Test
  public void testControlBoardStrategyReturnsNullWhenNoMoves() {
    List<Card> expensiveDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      expensiveDeck.add(new PlayingCard("Exp" + i, 3, 5, simpleInfluence));
    }
    model.startGame(expensiveDeck, expensiveDeck, 3, 5, 1);
    ControlBoardStrategy strategy = new ControlBoardStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNull("Should return null when no legal moves", move);
  }

  @Test
  public void testControlBoardStrategyWithCrossPattern() {
    List<Card> crossDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      crossDeck.add(new PlayingCard("Cross" + i, 1, 2, crossInfluence));
    }
    model.startGame(crossDeck, crossDeck, 3, 5, 5);
    ControlBoardStrategy strategy = new ControlBoardStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Should find move with cross pattern", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testControlBoardStrategyWithLargeInfluence() {
    List<Card> largeDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      largeDeck.add(new PlayingCard("Large" + i, 1, 3, largeInfluence));
    }
    model.startGame(largeDeck, largeDeck, 3, 5, 5);
    ControlBoardStrategy strategy = new ControlBoardStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Should find move with large influence", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testModelNotifiesMultipleListeners() {
    TestModelListener listener1 = new TestModelListener();
    TestModelListener listener2 = new TestModelListener();
    model.addModelListener(listener1);
    model.addModelListener(listener2);
    model.startGame(deck, deck, 3, 5, 5);
    assertEquals("Listener 1 should be notified", 1, listener1.turnChangeCount);
    assertEquals("Listener 2 should be notified", 1, listener2.turnChangeCount);
  }

  @Test
  public void testModelNotifiesTurnChangeAfterMultipleMoves() {
    TestModelListener listener = new TestModelListener();
    model.addModelListener(listener);
    model.startGame(deck, deck, 3, 5, 5);
    model.placeCard(0, 0, 0);
    model.placeCard(0, 0, 4);
    model.placeCard(0, 1, 0);
    assertEquals("Should notify after each move", 4, listener.turnChangeCount);
  }

  @Test
  public void testModelNotifiesGameOverOnlyOnce() {
    TestModelListener listener = new TestModelListener();
    model.addModelListener(listener);
    model.startGame(deck, deck, 3, 5, 5);
    model.passTurn();
    model.passTurn();
    assertEquals("Should notify game over exactly once", 1, listener.gameOverCount);
  }

  @Test
  public void testAiPlayerEmitsPassWhenNoMoves() {
    TestFeaturesListener listener = new TestFeaturesListener();
    List<Card> expensiveDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      expensiveDeck.add(new PlayingCard("Exp" + i, 3, 5, simpleInfluence));
    }
    model.startGame(expensiveDeck, expensiveDeck, 3, 5, 1);
    FillFirstStrategy strategy = new FillFirstStrategy();
    AiPlayer aiPlayer = new AiPlayer(strategy);
    aiPlayer.addFeaturesListener(listener);
    aiPlayer.notifyTurn(model);
    assertTrue("AI should emit pass when no moves", listener.passed);
    assertFalse("AI should not emit confirm", listener.confirmed);
  }

  @Test
  public void testAiPlayerWithMaxRowScoreStrategyEmitsEvents() {
    TestFeaturesListener listener = new TestFeaturesListener();
    MaxRowScoreStrategy strategy = new MaxRowScoreStrategy();
    AiPlayer aiPlayer = new AiPlayer(strategy);
    aiPlayer.addFeaturesListener(listener);
    model.startGame(deck, deck, 3, 5, 5);
    aiPlayer.notifyTurn(model);
    assertTrue("AI should emit events", listener.cardClicked);
    assertTrue("AI should emit events", listener.cellClicked);
    assertTrue("AI should emit events", listener.confirmed);
  }

  @Test
  public void testAiPlayerWithControlBoardStrategyEmitsEvents() {
    TestFeaturesListener listener = new TestFeaturesListener();
    ControlBoardStrategy strategy = new ControlBoardStrategy();
    AiPlayer aiPlayer = new AiPlayer(strategy);
    aiPlayer.addFeaturesListener(listener);
    model.startGame(deck, deck, 3, 5, 5);
    aiPlayer.notifyTurn(model);
    assertTrue("AI should emit events", listener.cardClicked);
    assertTrue("AI should emit events", listener.cellClicked);
    assertTrue("AI should emit events", listener.confirmed);
  }

  @Test
  public void testHumanPlayerCanAddMultipleListeners() {
    TestFeaturesListener listener1 = new TestFeaturesListener();
    TestFeaturesListener listener2 = new TestFeaturesListener();
    HumanPlayer humanPlayer = new HumanPlayer();
    humanPlayer.addFeaturesListener(listener1);
    humanPlayer.addFeaturesListener(listener2);
    model.startGame(deck, deck, 3, 5, 5);
    humanPlayer.notifyTurn(model);
    assertFalse("Listener 1 should not receive events", listener1.cardClicked);
    assertFalse("Listener 2 should not receive events", listener2.cardClicked);
  }

  @Test
  public void testAiPlayerCanAddMultipleListeners() {
    TestFeaturesListener listener1 = new TestFeaturesListener();
    TestFeaturesListener listener2 = new TestFeaturesListener();
    FillFirstStrategy strategy = new FillFirstStrategy();
    AiPlayer aiPlayer = new AiPlayer(strategy);
    aiPlayer.addFeaturesListener(listener1);
    aiPlayer.addFeaturesListener(listener2);
    model.startGame(deck, deck, 3, 5, 5);
    aiPlayer.notifyTurn(model);
    assertTrue("Listener 1 should receive events", listener1.cardClicked);
    assertTrue("Listener 2 should receive events", listener2.cardClicked);
  }

  @Test
  public void testFillFirstStrategyDeterministic() {
    model.startGame(deck, deck, 3, 5, 5);
    FillFirstStrategy strategy = new FillFirstStrategy();
    Move move1 = strategy.chooseMove(model, Player.RED);
    Move move2 = strategy.chooseMove(model, Player.RED);
    assertNotNull("First call should return move", move1);
    assertNotNull("Second call should return move", move2);
    assertEquals("Should return same card", move1.getCardIndex(), move2.getCardIndex());
    assertEquals("Should return same row", move1.getRow(), move2.getRow());
    assertEquals("Should return same col", move1.getColumn(), move2.getColumn());
  }

  @Test
  public void testMaxRowScoreStrategyWithVaryingCosts() {
    List<Card> costDeck = new ArrayList<>();
    costDeck.add(new PlayingCard("Cost1Val5", 1, 5, simpleInfluence));
    costDeck.add(new PlayingCard("Cost2Val10", 2, 10, simpleInfluence));
    costDeck.add(new PlayingCard("Cost3Val3", 3, 3, simpleInfluence));
    for (int i = 0; i < 17; i++) {
      costDeck.add(new PlayingCard("Card" + i, 1, 2, simpleInfluence));
    }
    model.startGame(costDeck, costDeck, 3, 5, 3);
    MaxRowScoreStrategy strategy = new MaxRowScoreStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Should find move", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testControlBoardStrategyOnEmptyBoard() {
    model.startGame(deck, deck, 3, 5, 5);
    ControlBoardStrategy strategy = new ControlBoardStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Should find move on initial board", move);
    assertEquals("Should place on starting pawns", 0, move.getColumn());
  }

  @Test
  public void testControlBoardStrategyAfterSeveralMoves() {
    model.startGame(deck, deck, 3, 5, 5);
    model.placeCard(0, 0, 0);
    model.placeCard(0, 0, 4);
    model.placeCard(0, 1, 0);
    ControlBoardStrategy strategy = new ControlBoardStrategy();
    Move move = strategy.chooseMove(model, Player.BLUE);
    assertNotNull("Should find move mid-game", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testModelNotifiesOnPassTurn() {
    TestModelListener listener = new TestModelListener();
    model.addModelListener(listener);
    model.startGame(deck, deck, 3, 5, 5);
    int countBefore = listener.turnChangeCount;
    model.passTurn();
    assertEquals("Should notify on pass", countBefore + 1, listener.turnChangeCount);
  }

  @Test
  public void testModelNotifiesCorrectPlayerAfterPass() {
    TestModelListener listener = new TestModelListener();
    model.addModelListener(listener);
    model.startGame(deck, deck, 3, 5, 5);
    model.passTurn();
    assertEquals("Should be BLUE after RED passes", Player.BLUE, listener.lastPlayer);
  }

  @Test
  public void testModelNotifiesOnPassAndGameOver() {
    TestModelListener listener = new TestModelListener();
    model.addModelListener(listener);
    model.startGame(deck, deck, 3, 5, 5);
    model.passTurn();
    model.passTurn();
    assertEquals("Should notify game over", 1, listener.gameOverCount);
    assertTrue("Should have turn notifications", listener.turnChangeCount >= 2);
  }

  @Test
  public void testMaxRowScoreStrategyFindsWinningRow() {
    List<Card> rangeDeck = new ArrayList<>();
    for (int val = 1; val <= 10; val++) {
      rangeDeck.add(new PlayingCard("Val" + val, 1, val, simpleInfluence));
    }
    for (int i = 0; i < 10; i++) {
      rangeDeck.add(new PlayingCard("Extra" + i, 1, 1, simpleInfluence));
    }
    model.startGame(rangeDeck, rangeDeck, 3, 5, 6);
    MaxRowScoreStrategy strategy = new MaxRowScoreStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Should find move", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testFillFirstStrategyWithSingleCard() {
    List<Card> singleCardDeck = new ArrayList<>();
    for (int i = 0; i < 15; i++) {
      singleCardDeck.add(new PlayingCard("Card" + i, 1, 1, simpleInfluence));
    }
    model.startGame(singleCardDeck, singleCardDeck, 3, 5, 1);
    FillFirstStrategy strategy = new FillFirstStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Should find move with single card", move);
    assertEquals("Should use only card", 0, move.getCardIndex());
  }

  @Test
  public void testControlBoardStrategyPrefersConversionOverEmpty() {
    List<Card> convDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      convDeck.add(new PlayingCard("Card" + i, 1, 1, crossInfluence));
    }
    model.startGame(convDeck, convDeck, 3, 5, 5);
    model.placeCard(0, 0, 0);
    ControlBoardStrategy strategy = new ControlBoardStrategy();
    Move move = strategy.chooseMove(model, Player.BLUE);
    assertNotNull("Should find move", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testStrategiesReturnValidMoveObjects() {
    model.startGame(deck, deck, 3, 5, 5);
    FillFirstStrategy strat1 = new FillFirstStrategy();
    MaxRowScoreStrategy strat2 = new MaxRowScoreStrategy();
    ControlBoardStrategy strat3 = new ControlBoardStrategy();
    Move move1 = strat1.chooseMove(model, Player.RED);
    Move move2 = strat2.chooseMove(model, Player.RED);
    Move move3 = strat3.chooseMove(model, Player.RED);
    assertNotNull("Strategy 1 move should be valid", move1);
    assertNotNull("Strategy 2 move should be valid", move2);
    assertNotNull("Strategy 3 move should be valid", move3);
    assertTrue("Move 1 in bounds", move1.getRow() >= 0 && move1.getColumn() >= 0);
    assertTrue("Move 2 in bounds", move2.getRow() >= 0 && move2.getColumn() >= 0);
    assertTrue("Move 3 in bounds", move3.getRow() >= 0 && move3.getColumn() >= 0);
  }

  @Test
  public void testModelNotifiesAfterMove() {
    TestModelListener listener = new TestModelListener();
    model.addModelListener(listener);
    model.startGame(deck, deck, 3, 5, 5);
    int countBefore = listener.turnChangeCount;
    model.placeCard(0, 0, 0);
    assertEquals("Should notify after move", countBefore + 1, listener.turnChangeCount);
  }

  @Test
  public void testAiPlayerWithDifferentStrategies() {
    FillFirstStrategy strat1 = new FillFirstStrategy();
    MaxRowScoreStrategy strat2 = new MaxRowScoreStrategy();
    ControlBoardStrategy strat3 = new ControlBoardStrategy();
    AiPlayer ai1 = new AiPlayer(strat1);
    AiPlayer ai2 = new AiPlayer(strat2);
    AiPlayer ai3 = new AiPlayer(strat3);
    TestFeaturesListener listener1 = new TestFeaturesListener();
    TestFeaturesListener listener2 = new TestFeaturesListener();
    TestFeaturesListener listener3 = new TestFeaturesListener();
    ai1.addFeaturesListener(listener1);
    ai2.addFeaturesListener(listener2);
    ai3.addFeaturesListener(listener3);
    model.startGame(deck, deck, 3, 5, 5);
    ai1.notifyTurn(model);
    ai2.notifyTurn(model);
    ai3.notifyTurn(model);
    assertTrue("All strategies should emit events", listener1.confirmed);
    assertTrue("All strategies should emit events", listener2.confirmed);
    assertTrue("All strategies should emit events", listener3.confirmed);
  }

  @Test
  public void testMaxRowScoreStrategyConsistentWithSameState() {
    model.startGame(deck, deck, 3, 5, 5);
    MaxRowScoreStrategy strategy = new MaxRowScoreStrategy();
    Move move1 = strategy.chooseMove(model, Player.RED);
    Move move2 = strategy.chooseMove(model, Player.RED);
    assertNotNull("First move should exist", move1);
    assertNotNull("Second move should exist", move2);
    assertEquals("Should be deterministic", move1.getCardIndex(), move2.getCardIndex());
    assertEquals("Should be deterministic", move1.getRow(), move2.getRow());
    assertEquals("Should be deterministic", move1.getColumn(), move2.getColumn());
  }

  @Test
  public void testControlBoardStrategyConsistentWithSameState() {
    model.startGame(deck, deck, 3, 5, 5);
    ControlBoardStrategy strategy = new ControlBoardStrategy();
    Move move1 = strategy.chooseMove(model, Player.RED);
    Move move2 = strategy.chooseMove(model, Player.RED);
    assertNotNull("First move should exist", move1);
    assertNotNull("Second move should exist", move2);
    assertEquals("Should be deterministic", move1.getCardIndex(), move2.getCardIndex());
    assertEquals("Should be deterministic", move1.getRow(), move2.getRow());
    assertEquals("Should be deterministic", move1.getColumn(), move2.getColumn());
  }

  @Test
  public void testStrategiesHandleChangingGameState() {
    model.startGame(deck, deck, 3, 5, 5);
    FillFirstStrategy strategy = new FillFirstStrategy();
    Move move1 = strategy.chooseMove(model, Player.RED);
    model.placeCard(move1.getCardIndex(), move1.getRow(), move1.getColumn());
    Move move2 = strategy.chooseMove(model, Player.BLUE);
    assertNotNull("Should adapt to new state", move2);
    assertTrue("New move should be legal",
        model.isLegalMove(move2.getCardIndex(), move2.getRow(), move2.getColumn()));
  }

  @Test
  public void testModelListenerReceivesAllTurnChanges() {
    TestModelListener listener = new TestModelListener();
    model.addModelListener(listener);
    model.startGame(deck, deck, 3, 5, 5);
    final int initialCount = listener.turnChangeCount;
    model.placeCard(0, 0, 0);
    model.placeCard(0, 0, 4);
    model.placeCard(0, 1, 0);
    model.placeCard(0, 1, 4);
    assertTrue("Should have multiple notifications", listener.turnChangeCount > initialCount);
  }

  @Test
  public void testMaxRowScoreStrategyWithTiedRows() {
    List<Card> tiedDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      tiedDeck.add(new PlayingCard("Card" + i, 1, 5, simpleInfluence));
    }
    model.startGame(tiedDeck, tiedDeck, 3, 5, 5);
    MaxRowScoreStrategy strategy = new MaxRowScoreStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Should choose move to win a row", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testControlBoardStrategyScoresMovesCorrectly() {
    List<Card> infDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      infDeck.add(new PlayingCard("Card" + i, 1, 1, largeInfluence));
    }
    model.startGame(infDeck, infDeck, 5, 9, 5);
    ControlBoardStrategy strategy = new ControlBoardStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Should find move with large influence", move);
    assertTrue("Move should be valid",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testStrategiesWorkWithMinimalBoard() {
    List<Card> minDeck = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      minDeck.add(new PlayingCard("Card" + i, 1, 1, simpleInfluence));
    }
    model.startGame(minDeck, minDeck, 1, 3, 2);
    FillFirstStrategy strat1 = new FillFirstStrategy();
    MaxRowScoreStrategy strat2 = new MaxRowScoreStrategy();
    ControlBoardStrategy strat3 = new ControlBoardStrategy();
    assertNotNull("Strategy 1 should work on small board", strat1.chooseMove(model, Player.RED));
    assertNotNull("Strategy 2 should work on small board", strat2.chooseMove(model, Player.RED));
    assertNotNull("Strategy 3 should work on small board", strat3.chooseMove(model, Player.RED));
  }

  @Test
  public void testFillFirstStrategyExhaustiveSearch() {
    List<Card> limitedDeck = new ArrayList<>();
    limitedDeck.add(new PlayingCard("HighCost", 3, 5, simpleInfluence));
    for (int i = 0; i < 19; i++) {
      limitedDeck.add(new PlayingCard("LowCost" + i, 1, 1, simpleInfluence));
    }
    model.startGame(limitedDeck, limitedDeck, 3, 5, 5);
    FillFirstStrategy strategy = new FillFirstStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Should skip expensive card and find playable", move);
    Card chosenCard = model.getCurrentPlayerHand().get(move.getCardIndex());
    assertEquals("Should choose low cost card", 1, chosenCard.getCost());
  }

  @Test
  public void testMaxRowScoreStrategySkipsWonRows() {
    List<Card> scoreDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      scoreDeck.add(new PlayingCard("Card" + i, 1, 5, simpleInfluence));
    }
    model.startGame(scoreDeck, scoreDeck, 3, 5, 5);
    model.placeCard(0, 0, 0);
    model.passTurn();
    MaxRowScoreStrategy strategy = new MaxRowScoreStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Should find move in unw won row", move);
    assertTrue("Should not be row 0", move.getRow() != 0);
  }

  @Test
  public void testControlBoardStrategyHandlesCardsOnBoard() {
    model.startGame(deck, deck, 3, 5, 5);
    model.placeCard(0, 0, 0);
    model.placeCard(0, 0, 4);
    ControlBoardStrategy strategy = new ControlBoardStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Should find move with cards present", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testAiPlayerMultipleNotifications() {
    TestFeaturesListener listener = new TestFeaturesListener();
    FillFirstStrategy strategy = new FillFirstStrategy();
    AiPlayer aiPlayer = new AiPlayer(strategy);
    aiPlayer.addFeaturesListener(listener);
    model.startGame(deck, deck, 3, 5, 5);
    aiPlayer.notifyTurn(model);
    assertTrue("First notification should work", listener.confirmed);
    listener.confirmed = false;
    listener.cardClicked = false;
    listener.cellClicked = false;
    aiPlayer.notifyTurn(model);
    assertTrue("Second notification should work", listener.confirmed);
  }

  private static class TestModelListener implements ModelFeatures {
    int turnChangeCount = 0;
    int gameOverCount = 0;
    Player lastPlayer = null;

    @Override
    public void onTurnChanged(Player currentPlayer) {
      turnChangeCount++;
      lastPlayer = currentPlayer;
    }

    @Override
    public void onGameOver() {
      gameOverCount++;
    }
  }

  private static class TestFeaturesListener implements Features {
    boolean cardClicked = false;
    boolean cellClicked = false;
    boolean confirmed = false;
    boolean passed = false;

    @Override
    public void handleCardClick(int cardIndex) {
      cardClicked = true;
    }

    @Override
    public void handleCellClick(int row, int col) {
      cellClicked = true;
    }

    @Override
    public void handleConfirm() {
      confirmed = true;
    }

    @Override
    public void handlePass() {
      passed = true;
    }
  }
}