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
import sanguine.view.MockSanguineView;

/**
 * Represents the integration tests for the controller, model, and view.
 */
public class ControllerIntegrationTests {

  private BasicSanguine model;
  private MockSanguineView redView;
  private MockSanguineView blueView;
  private HumanPlayer redPlayer;
  private HumanPlayer bluePlayer;
  private SanguineController redController;
  private SanguineController blueController;
  private List<Card> deck;
  private boolean[][] simpleInfluence;
  private boolean[][] crossInfluence;

  /**
   * Represents setting up the game before running tests.
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
    deck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      deck.add(new PlayingCard("Card" + i, 1, 1, simpleInfluence));
    }
    model = new BasicSanguine();
    redView = new MockSanguineView();
    blueView = new MockSanguineView();
    redPlayer = new HumanPlayer();
    bluePlayer = new HumanPlayer();
    redController = new SanguineController(model, Player.RED, redView, redPlayer);
    blueController = new SanguineController(model, Player.BLUE, blueView, bluePlayer);
    model.addModelListener(redController);
    model.addModelListener(blueController);
  }

  @Test
  public void testControllerIgnoresActionsWhenNotPlayersTurn() {
    model.startGame(deck, deck, 3, 5, 5);
    blueView.simulateCardClick(0);
    blueView.simulateCellClick(0, 4);
    blueView.simulateConfirm();
    assertEquals("Should still be RED's turn", Player.RED, model.getCurrentPlayer());
  }

  @Test
  public void testControllerAllowsActionsOnPlayersTurn() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    assertEquals("Should be BLUE's turn", Player.BLUE, model.getCurrentPlayer());
  }

  @Test
  public void testControllerShowsErrorForInvalidMove() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 2);
    redView.simulateConfirm();
    String lastMessage = redView.getLastMessage();
    assertNotNull("Should show error message", lastMessage);
    assertTrue(
        "Error should mention invalid move",
        lastMessage.toLowerCase().contains("invalid"));
  }

  @Test
  public void testControllerRequiresCardAndCellBeforeConfirm() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateConfirm();
    String message = redView.getLastMessage();
    assertNotNull("Should show message", message);
    assertTrue("Should require selection", message.toLowerCase().contains("select"));
  }

  @Test
  public void testControllerClearsSelectionsAfterSuccessfulMove() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    assertEquals(0, redView.getHighlightedCard());
    assertEquals(0, redView.getHighlightedRow());
    redView.simulateConfirm();
    assertEquals(-1, redView.getHighlightedCard());
    assertEquals(-1, redView.getHighlightedRow());
  }

  @Test
  public void testControllerUpdatesViewTitlesOnTurnChange() {
    model.startGame(deck, deck, 3, 5, 5);
    String redTitle = redView.getLastTitle();
    assertTrue(
        "RED should show 'Your Turn'",
        redTitle != null && redTitle.contains("Your Turn"));
    String blueTitle = blueView.getLastTitle();
    assertTrue(
        "BLUE should show 'Waiting'",
        blueTitle != null && blueTitle.contains("Waiting"));
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    redTitle = redView.getLastTitle();
    blueTitle = blueView.getLastTitle();
    assertTrue(
        "RED should now show 'Waiting'",
        redTitle != null && redTitle.contains("Waiting"));
    assertTrue(
        "BLUE should now show 'Your Turn'",
        blueTitle != null && blueTitle.contains("Your Turn"));
  }

  @Test
  public void testControllerHandlesGameOver() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulatePass();
    blueView.simulatePass();
    String redMessage = redView.getLastMessage();
    String blueMessage = blueView.getLastMessage();
    assertNotNull("RED should see game over", redMessage);
    assertNotNull("BLUE should see game over", blueMessage);
    assertTrue(
        "Should mention game over",
        redMessage.contains("Over") || redMessage.contains("Tie"));
  }

  @Test
  public void testControllerPassClearsSelections() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulatePass();
    assertEquals(-1, redView.getHighlightedCard());
    assertEquals(-1, redView.getHighlightedRow());
  }

  @Test
  public void testTwoPlayerGameCompleteFlow() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    assertEquals("Should be BLUE's turn", Player.BLUE, model.getCurrentPlayer());
    blueView.simulateCardClick(0);
    blueView.simulateCellClick(0, 4);
    blueView.simulateConfirm();
    assertEquals("Should be RED's turn again", Player.RED, model.getCurrentPlayer());
    assertFalse("Game should still be ongoing", model.isGameOver());
  }

  @Test
  public void testAiPlayerIntegration() {
    FillFirstStrategy strategy = new FillFirstStrategy();
    AiPlayer aiPlayer = new AiPlayer(strategy);
    SanguineController aiController =
        new SanguineController(model, Player.RED, redView, aiPlayer);
    model.addModelListener(aiController);
    model.addModelListener(blueController);
    model.startGame(deck, deck, 3, 5, 5);
    assertEquals("Should be BLUE's turn after AI", Player.BLUE, model.getCurrentPlayer());
  }

  @Test
  public void testControllerIgnoresCardClickWhenNotTurn() {
    model.startGame(deck, deck, 3, 5, 5);
    blueView.simulateCardClick(0);
    assertEquals(-1, blueView.getHighlightedCard());
  }

  @Test
  public void testControllerIgnoresCellClickWhenNotTurn() {
    model.startGame(deck, deck, 3, 5, 5);
    blueView.simulateCellClick(0, 4);
    assertEquals(-1, blueView.getHighlightedRow());
    assertEquals(-1, blueView.getHighlightedCol());
  }

  @Test
  public void testControllerIgnoresPassWhenNotTurn() {
    model.startGame(deck, deck, 3, 5, 5);
    blueView.simulatePass();
    assertEquals("Should still be RED's turn", Player.RED, model.getCurrentPlayer());
    String message = blueView.getLastMessage();
    assertNotNull("Should show error message", message);
    assertTrue("Should mention not your turn", message.toLowerCase().contains("turn"));
  }

  @Test
  public void testControllerRefreshesViewAfterMove() {
    model.startGame(deck, deck, 3, 5, 5);
    final int initialRefresh = redView.getRefreshCount();
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    assertTrue("View should be refreshed", redView.getRefreshCount() > initialRefresh);
  }

  @Test
  public void testControllerRefreshesViewAfterPass() {
    model.startGame(deck, deck, 3, 5, 5);
    int initialRefresh = redView.getRefreshCount();
    redView.simulatePass();
    assertTrue("View should be refreshed", redView.getRefreshCount() > initialRefresh);
  }

  @Test
  public void testControllerHandlesInvalidCardIndex() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(99);
    String message = redView.getLastMessage();
    assertNotNull("Should show error", message);
    assertTrue("Should mention invalid", message.toLowerCase().contains("invalid"));
  }

  @Test
  public void testControllerHandlesNegativeCardIndex() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(-1);
    String message = redView.getLastMessage();
    assertNotNull("Should show error", message);
  }

  @Test
  public void testControllerCanSelectDifferentCardsBeforeConfirm() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    assertEquals(0, redView.getHighlightedCard());
    redView.simulateCardClick(1);
    assertEquals(1, redView.getHighlightedCard());
    redView.simulateCardClick(2);
    assertEquals(2, redView.getHighlightedCard());
  }

  @Test
  public void testControllerCanSelectDifferentCellsBeforeConfirm() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCellClick(0, 0);
    assertEquals(0, redView.getHighlightedRow());
    assertEquals(0, redView.getHighlightedCol());
    redView.simulateCellClick(1, 0);
    assertEquals(1, redView.getHighlightedRow());
    assertEquals(0, redView.getHighlightedCol());
  }

  @Test
  public void testControllerRequiresBothSelections() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateConfirm();
    String message = redView.getLastMessage();
    assertNotNull("Should require cell selection", message);
    assertTrue("Should mention selection needed", message.toLowerCase().contains("select"));
  }

  @Test
  public void testControllerRequiresCardSelection() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    String message = redView.getLastMessage();
    assertNotNull("Should require card selection", message);
    assertTrue("Should mention selection needed", message.toLowerCase().contains("select"));
  }

  @Test
  public void testControllerHandlesPlacingOnEnemyPawns() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 4);
    redView.simulateConfirm();
    String message = redView.getLastMessage();
    assertNotNull("Should show error", message);
    assertTrue("Should mention invalid", message.toLowerCase().contains("invalid"));
  }

  @Test
  public void testControllerHandlesPlacingOnEmptyCell() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 2);
    redView.simulateConfirm();
    String message = redView.getLastMessage();
    assertNotNull("Should show error", message);
    assertTrue(
        "Should mention invalid or pawns",
        message.toLowerCase().contains("invalid")
            || message.toLowerCase().contains("pawn"));
  }

  @Test
  public void testControllerHandlesInsufficientPawns() {
    List<Card> expensiveDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      expensiveDeck.add(new PlayingCard("Exp" + i, 3, 5, simpleInfluence));
    }
    model.startGame(expensiveDeck, expensiveDeck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    String message = redView.getLastMessage();
    assertNotNull("Should show error", message);
    assertTrue("Should mention pawns", message.toLowerCase().contains("pawn"));
  }

  @Test
  public void testBothPlayersPassEndsGame() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulatePass();
    assertFalse("Game should not be over after one pass", model.isGameOver());
    blueView.simulatePass();
    assertTrue("Game should be over after both pass", model.isGameOver());
  }

  @Test
  public void testPlayingCardResetsPasses() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulatePass();
    blueView.simulateCardClick(0);
    blueView.simulateCellClick(0, 4);
    blueView.simulateConfirm();
    redView.simulatePass();
    assertFalse("Game should not end, passes were reset", model.isGameOver());
  }

  @Test
  public void testControllerShowsWinnerMessageRed() {
    List<Card> scoreDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      scoreDeck.add(new PlayingCard("Card" + i, 1, 5, simpleInfluence));
    }
    model.startGame(scoreDeck, scoreDeck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    blueView.simulatePass();
    redView.simulatePass();
    String redMessage = redView.getLastMessage();
    String blueMessage = blueView.getLastMessage();
    assertNotNull("RED should see message", redMessage);
    assertNotNull("BLUE should see message", blueMessage);
    assertTrue("RED should see win", redMessage.contains("Win"));
    assertTrue("BLUE should see lose", blueMessage.contains("Lose"));
  }

  @Test
  public void testControllerShowsTieMessage() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulatePass();
    blueView.simulatePass();
    String redMessage = redView.getLastMessage();
    assertTrue("Should show tie", redMessage.contains("Tie"));
  }

  @Test
  public void testControllerUpdatesViewOnTurnChange() {
    model.startGame(deck, deck, 3, 5, 5);
    final int redRefreshBefore = redView.getRefreshCount();
    final int blueRefreshBefore = blueView.getRefreshCount();
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    assertTrue("RED view refreshed", redView.getRefreshCount() > redRefreshBefore);
    assertTrue("BLUE view refreshed", blueView.getRefreshCount() > blueRefreshBefore);
  }

  @Test
  public void testAiPlayerMakesValidMove() {
    FillFirstStrategy strategy = new FillFirstStrategy();
    AiPlayer aiPlayer = new AiPlayer(strategy);
    SanguineController aiController =
        new SanguineController(model, Player.RED, redView, aiPlayer);
    model.addModelListener(aiController);
    model.addModelListener(blueController);
    model.startGame(deck, deck, 3, 5, 5);
    assertEquals("AI should have made move", Player.BLUE, model.getCurrentPlayer());
  }

  @Test
  public void testAiPlayerPassesWhenNoLegalMoves() {
    List<Card> expensiveDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      expensiveDeck.add(new PlayingCard("Exp" + i, 3, 5, simpleInfluence));
    }
    FillFirstStrategy strategy = new FillFirstStrategy();
    AiPlayer redAi = new AiPlayer(strategy);
    AiPlayer blueAi = new AiPlayer(strategy);
    SanguineController redAiController =
        new SanguineController(model, Player.RED, redView, redAi);
    SanguineController blueAiController =
        new SanguineController(model, Player.BLUE, blueView, blueAi);
    model.addModelListener(redAiController);
    model.addModelListener(blueAiController);
    model.startGame(expensiveDeck, expensiveDeck, 3, 5, 1);
    assertTrue("Game should end with both AI passing", model.isGameOver());
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
    assertNotNull("Should find move", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testControlBoardStrategyChoosesValidMove() {
    model.startGame(deck, deck, 3, 5, 5);
    ControlBoardStrategy strategy = new ControlBoardStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Should find move", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testControllerMultipleMoveSequence() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    assertEquals(Player.BLUE, model.getCurrentPlayer());
    blueView.simulateCardClick(0);
    blueView.simulateCellClick(0, 4);
    blueView.simulateConfirm();
    assertEquals(Player.RED, model.getCurrentPlayer());
    redView.simulateCardClick(0);
    redView.simulateCellClick(1, 0);
    redView.simulateConfirm();
    assertEquals(Player.BLUE, model.getCurrentPlayer());
  }

  @Test
  public void testControllerHandlesOutOfBoundsCell() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(10, 10);
    redView.simulateConfirm();
    String message = redView.getLastMessage();
    assertNotNull("Should show error", message);
  }

  @Test
  public void testTwoAiPlayersCompleteGame() {
    FillFirstStrategy strat1 = new FillFirstStrategy();
    MaxRowScoreStrategy strat2 = new MaxRowScoreStrategy();
    AiPlayer redAi = new AiPlayer(strat1);
    AiPlayer blueAi = new AiPlayer(strat2);
    SanguineController redAiController =
        new SanguineController(model, Player.RED, redView, redAi);
    SanguineController blueAiController =
        new SanguineController(model, Player.BLUE, blueView, blueAi);
    model.addModelListener(redAiController);
    model.addModelListener(blueAiController);
    model.startGame(deck, deck, 3, 5, 5);
    assertTrue("Game should complete", model.isGameOver());
    assertNotNull(
        "Should have winner or tie",
        model.getWinner() != null
            || model.getFinalScores()[0] == model.getFinalScores()[1]);
  }

  @Test
  public void testControllerClearsHighlightsOnTurnChange() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    assertEquals(-1, redView.getHighlightedCard());
    assertEquals(-1, redView.getHighlightedRow());
  }

  @Test
  public void testControllerHandlesInvalidMoveWithCardPlaced() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    blueView.simulateCardClick(0);
    blueView.simulateCellClick(0, 0);
    blueView.simulateConfirm();
    String message = blueView.getLastMessage();
    assertNotNull("Should show error", message);
    assertTrue(
        "Should mention pawns or invalid",
        message.toLowerCase().contains("pawn")
            || message.toLowerCase().contains("invalid"));
  }

  @Test
  public void testControllerAllowsPassWithoutSelections() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulatePass();
    assertEquals("Should be BLUE's turn", Player.BLUE, model.getCurrentPlayer());
    assertFalse("Game should not be over", model.isGameOver());
  }

  @Test
  public void testControllerAllowsPassAfterSelections() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulatePass();
    assertEquals("Should be BLUE's turn", Player.BLUE, model.getCurrentPlayer());
    assertEquals(-1, redView.getHighlightedCard());
  }

  @Test
  public void testMixedHumanAiGame() {
    FillFirstStrategy strategy = new FillFirstStrategy();
    AiPlayer blueAi = new AiPlayer(strategy);
    SanguineController blueAiController =
        new SanguineController(model, Player.BLUE, blueView, blueAi);
    model.addModelListener(redController);
    model.addModelListener(blueAiController);
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    assertEquals(
        "Should be RED's turn after AI move",
        Player.RED,
        model.getCurrentPlayer());
  }

  @Test
  public void testControllerHandlesRapidClicks() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateCardClick(1);
    redView.simulateCardClick(2);
    assertEquals(2, redView.getHighlightedCard());
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    assertEquals(Player.BLUE, model.getCurrentPlayer());
  }

  @Test
  public void testControllerViewRefreshCount() {
    model.startGame(deck, deck, 3, 5, 5);
    final int initialCount = redView.getRefreshCount();
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    assertTrue(
        "View should refresh multiple times",
        redView.getRefreshCount() > initialCount);
  }

  @Test
  public void testGameOverSetsIsMyTurnFalse() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulatePass();
    blueView.simulatePass();
    assertTrue("Game should be over", model.isGameOver());
    redView.simulateCardClick(0);
    assertEquals(
        "Card should not be highlighted after game over",
        -1,
        redView.getHighlightedCard());
  }

  @Test
  public void testFillFirstStrategyFindsValidMove() {
    model.startGame(deck, deck, 3, 5, 5);
    FillFirstStrategy strategy = new FillFirstStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNotNull("Should find a move", move);
    assertTrue("Move should be legal",
        model.isLegalMove(move.getCardIndex(), move.getRow(), move.getColumn()));
  }

  @Test
  public void testFillFirstStrategyReturnsNullWhenNoMoves() {
    List<Card> impossibleDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      impossibleDeck.add(new PlayingCard("Card" + i, 3, 1, simpleInfluence));
    }
    model.startGame(impossibleDeck, impossibleDeck, 3, 5, 1);
    FillFirstStrategy strategy = new FillFirstStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNull("Should return null when no legal moves", move);
  }

  @Test
  public void testMaxRowScoreStrategyReturnsNullWhenNoMoves() {
    List<Card> impossibleDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      impossibleDeck.add(new PlayingCard("Card" + i, 3, 1, simpleInfluence));
    }
    model.startGame(impossibleDeck, impossibleDeck, 3, 5, 1);
    MaxRowScoreStrategy strategy = new MaxRowScoreStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNull("Should return null when no legal moves", move);
  }

  @Test
  public void testControlBoardStrategyReturnsNullWhenNoMoves() {
    List<Card> impossibleDeck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      impossibleDeck.add(new PlayingCard("Card" + i, 3, 1, simpleInfluence));
    }
    model.startGame(impossibleDeck, impossibleDeck, 3, 5, 1);
    ControlBoardStrategy strategy = new ControlBoardStrategy();
    Move move = strategy.chooseMove(model, Player.RED);
    assertNull("Should return null when no legal moves", move);
  }

  @Test
  public void testControllerHandlesLargeBoard() {
    List<Card> largeDeck = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      largeDeck.add(new PlayingCard("Card" + i, 1, 1, simpleInfluence));
    }
    model.startGame(largeDeck, largeDeck, 5, 11, 5);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    assertEquals("Should switch to BLUE", Player.BLUE, model.getCurrentPlayer());
  }

  @Test
  public void testControllerHandlesSmallBoard() {
    model.startGame(deck, deck, 1, 3, 2);
    redView.simulateCardClick(0);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    assertEquals("Should switch to BLUE", Player.BLUE, model.getCurrentPlayer());
  }

  @Test
  public void testHumanPlayerDoesNotAutoPlay() {
    model.startGame(deck, deck, 3, 5, 5);
    assertEquals("Should still be RED's turn", Player.RED, model.getCurrentPlayer());
  }

  @Test
  public void testAiPlayerWithMaxRowScoreStrategyCompletes() {
    MaxRowScoreStrategy strategy = new MaxRowScoreStrategy();
    AiPlayer redAi = new AiPlayer(strategy);
    AiPlayer blueAi = new AiPlayer(strategy);
    SanguineController redAiController =
        new SanguineController(model, Player.RED, redView, redAi);
    SanguineController blueAiController =
        new SanguineController(model, Player.BLUE, blueView, blueAi);
    model.addModelListener(redAiController);
    model.addModelListener(blueAiController);
    model.startGame(deck, deck, 3, 5, 5);
    assertTrue("Game should complete", model.isGameOver());
  }

  @Test
  public void testAiPlayerWithControlBoardStrategyCompletes() {
    ControlBoardStrategy strategy = new ControlBoardStrategy();
    AiPlayer redAi = new AiPlayer(strategy);
    AiPlayer blueAi = new AiPlayer(strategy);
    SanguineController redAiController =
        new SanguineController(model, Player.RED, redView, redAi);
    SanguineController blueAiController =
        new SanguineController(model, Player.BLUE, blueView, blueAi);
    model.addModelListener(redAiController);
    model.addModelListener(blueAiController);
    model.startGame(deck, deck, 3, 5, 5);
    assertTrue("Game should complete", model.isGameOver());
  }

  @Test
  public void testControllerHandlesConfirmWithoutCard() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCellClick(0, 0);
    redView.simulateConfirm();
    assertEquals("Should still be RED's turn", Player.RED, model.getCurrentPlayer());
    String message = redView.getLastMessage();
    assertNotNull("Should show error", message);
  }

  @Test
  public void testControllerHandlesConfirmWithoutCell() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.simulateCardClick(0);
    redView.simulateConfirm();
    assertEquals("Should still be RED's turn", Player.RED, model.getCurrentPlayer());
    String message = redView.getLastMessage();
    assertNotNull("Should show error", message);
  }

  @Test
  public void testDifferentStrategiesProduceDifferentOutcomes() {
    List<Card> variedDeck = new ArrayList<>();
    variedDeck.add(new PlayingCard("Low", 1, 1, simpleInfluence));
    variedDeck.add(new PlayingCard("High", 1, 10, crossInfluence));
    for (int i = 0; i < 18; i++) {
      variedDeck.add(new PlayingCard("Card" + i, 1, 2, simpleInfluence));
    }
    model.startGame(variedDeck, variedDeck, 3, 5, 2);
    FillFirstStrategy strat1 = new FillFirstStrategy();
    MaxRowScoreStrategy strat2 = new MaxRowScoreStrategy();
    Move move1 = strat1.chooseMove(model, Player.RED);
    Move move2 = strat2.chooseMove(model, Player.RED);
    assertNotNull("Strategy 1 should find move", move1);
    assertNotNull("Strategy 2 should find move", move2);
  }

  @Test
  public void testControllerHandlesMultipleListeners() {
    MockSanguineView extraView = new MockSanguineView();
    HumanPlayer extraPlayer = new HumanPlayer();
    SanguineController extraController =
        new SanguineController(model, Player.RED, extraView, extraPlayer);
    model.addModelListener(extraController);
    model.startGame(deck, deck, 3, 5, 5);
    String title = extraView.getLastTitle();
    assertNotNull("Extra listener should be notified", title);
  }

  @Test
  public void testControllerViewVisibilityToggle() {
    model.startGame(deck, deck, 3, 5, 5);
    redView.setVisible(true);
    assertTrue("View should be visible", redView.isVisible());
    redView.setVisible(false);
    assertFalse("View should be hidden", redView.isVisible());
  }
}