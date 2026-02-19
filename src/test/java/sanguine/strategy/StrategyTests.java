package sanguine.strategy;

import static org.junit.Assert.assertEquals;
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


/**
 * Tests for all strategy implementations including mocking tests.
 */
public class StrategyTests {
  private boolean[][] simpleInfluence;
  private boolean[][] crossInfluence;

  /**
   * Sets up tests.
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
  }

  @Test
  public void testFillFirstStrategyChoosesFirstValidMove() {
    List<Card> deck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      deck.add(new PlayingCard("Card" + i, 1, 1, simpleInfluence));
    }

    BasicSanguine model = new BasicSanguine();
    model.startGame(deck, deck, 3, 5, 5);

    PlayerStrategy strategy = new FillFirstStrategy();
    Move move = strategy.chooseMove(model, Player.RED);

    assertNotNull(move);
    assertEquals(0, move.getCardIndex());
    assertEquals(0, move.getRow());
    assertEquals(0, move.getColumn());
  }

  @Test
  public void testFillFirstStrategyWithMockVerifiesOrder() {
    List<Card> hand = new ArrayList<>();
    hand.add(new PlayingCard("Card1", 1, 1, simpleInfluence));
    hand.add(new PlayingCard("Card2", 1, 1, simpleInfluence));

    boolean[][] legalMoves = new boolean[3][3];
    legalMoves[1][2] = true;

    int[][] rowScores = {{0, 0}, {0, 0}, {0, 0}};

    MockSanguineModel mock = new MockSanguineModel(hand, 3, 3, Player.RED,
        legalMoves, rowScores);

    PlayerStrategy strategy = new FillFirstStrategy();
    Move move = strategy.chooseMove(mock, Player.RED);

    assertNotNull(move);
    assertEquals(0, move.getCardIndex());
    assertEquals(1, move.getRow());
    assertEquals(2, move.getColumn());

    List<String> transcript = mock.getTranscript();
    assertTrue(transcript.contains("isLegalMove(0, 0, 0)"));
    assertTrue(transcript.contains("isLegalMove(0, 0, 1)"));
    assertTrue(transcript.contains("isLegalMove(0, 0, 2)"));
    assertTrue(transcript.contains("isLegalMove(0, 1, 0)"));
    assertTrue(transcript.contains("isLegalMove(0, 1, 1)"));
    assertTrue(transcript.contains("isLegalMove(0, 1, 2)"));
  }

  @Test
  public void testFillFirstStrategyReturnsNullWhenNoValidMoves() {
    List<Card> hand = new ArrayList<>();
    hand.add(new PlayingCard("Card1", 3, 1, simpleInfluence));

    boolean[][] legalMoves = new boolean[3][3];

    int[][] rowScores = {{0, 0}, {0, 0}, {0, 0}};

    MockSanguineModel mock = new MockSanguineModel(hand, 3, 3, Player.RED,
        legalMoves, rowScores);

    PlayerStrategy strategy = new FillFirstStrategy();
    Move move = strategy.chooseMove(mock, Player.RED);

    assertNull(move);
  }

  @Test
  public void testMaxRowScoreStrategyChoosesRowWinningMove() {
    List<Card> deck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      deck.add(new PlayingCard("Card" + i, 1, 3, simpleInfluence));
    }

    BasicSanguine model = new BasicSanguine();
    model.startGame(deck, deck, 3, 5, 5);

    model.placeCard(0, 0, 0);
    model.placeCard(0, 1, 4);

    PlayerStrategy strategy = new MaxRowScoreStrategy();
    Move move = strategy.chooseMove(model, Player.RED);

    assertNotNull(move);
    assertEquals(2, move.getRow());
  }

  @Test
  public void testMaxRowScoreStrategyVisitsRowsTopToBottom() {
    List<Card> hand = new ArrayList<>();
    hand.add(new PlayingCard("Card1", 1, 5, simpleInfluence));

    boolean[][] legalMoves = new boolean[3][3];
    legalMoves[0][0] = true;
    legalMoves[1][0] = true;
    legalMoves[2][0] = true;

    int[][] rowScores = {{2, 5}, {2, 5}, {2, 5}};

    MockSanguineModel mock = new MockSanguineModel(hand, 3, 3, Player.RED,
        legalMoves, rowScores);

    PlayerStrategy strategy = new MaxRowScoreStrategy();
    Move move = strategy.chooseMove(mock, Player.RED);

    assertNotNull(move);
    assertEquals(0, move.getRow());

    List<String> transcript = mock.getTranscript();

    int row0Index = -1;
    int row1Index = -1;

    for (int i = 0; i < transcript.size(); i++) {
      if (transcript.get(i).contains("getRowScore(0)") && row0Index == -1) {
        row0Index = i;
      }
      if (transcript.get(i).contains("getRowScore(1)") && row1Index == -1) {
        row1Index = i;
      }
    }

    assertTrue("Row 0 should be checked", row0Index != -1);
    assertTrue("Row 0 should be checked before row 1", row0Index < row1Index || row1Index == -1);
  }

  @Test
  public void testMaxRowScoreStrategySkipsRowsAlreadyWinning() {
    List<Card> hand = new ArrayList<>();
    hand.add(new PlayingCard("Card1", 1, 5, simpleInfluence));

    boolean[][] legalMoves = new boolean[3][3];
    legalMoves[0][0] = true;
    legalMoves[1][0] = true;

    int[][] rowScores = {{10, 5}, {2, 5}, {2, 5}};

    MockSanguineModel mock = new MockSanguineModel(hand, 3, 3, Player.RED,
        legalMoves, rowScores);

    PlayerStrategy strategy = new MaxRowScoreStrategy();
    Move move = strategy.chooseMove(mock, Player.RED);

    assertNotNull(move);
    assertEquals(1, move.getRow());
  }

  @Test
  public void testMaxRowScoreStrategyReturnsNullWhenCannotWinAnyRow() {
    List<Card> hand = new ArrayList<>();
    hand.add(new PlayingCard("Card1", 1, 1, simpleInfluence));

    boolean[][] legalMoves = new boolean[3][3];
    legalMoves[0][0] = true;
    legalMoves[1][0] = true;
    legalMoves[2][0] = true;

    int[][] rowScores = {{0, 10}, {0, 10}, {0, 10}};

    MockSanguineModel mock = new MockSanguineModel(hand, 3, 3, Player.RED,
        legalMoves, rowScores);

    PlayerStrategy strategy = new MaxRowScoreStrategy();
    Move move = strategy.chooseMove(mock, Player.RED);

    assertNull(move);
  }

  @Test
  public void testControlBoardStrategyChoosesMaxCellControl() {
    List<Card> deck = new ArrayList<>();
    deck.add(new PlayingCard("Weak", 1, 1, simpleInfluence));
    deck.add(new PlayingCard("Strong", 1, 1, crossInfluence));
    for (int i = 0; i < 18; i++) {
      deck.add(new PlayingCard("Card" + i, 1, 1, simpleInfluence));
    }

    BasicSanguine model = new BasicSanguine();
    model.startGame(deck, deck, 3, 5, 5);

    PlayerStrategy strategy = new ControlBoardStrategy();
    Move move = strategy.chooseMove(model, Player.RED);

    assertNotNull(move);
    assertEquals(1, move.getCardIndex());
  }

  @Test
  public void testControlBoardStrategyTieBreakingPosition() {
    List<Card> hand = new ArrayList<>();
    hand.add(new PlayingCard("Card1", 1, 1, simpleInfluence));

    boolean[][] legalMoves = new boolean[3][3];
    legalMoves[0][2] = true;
    legalMoves[1][1] = true;
    legalMoves[2][0] = true;

    int[][] rowScores = {{0, 0}, {0, 0}, {0, 0}};

    MockSanguineModel mock = new MockSanguineModel(hand, 3, 3, Player.RED,
        legalMoves, rowScores);

    PlayerStrategy strategy = new ControlBoardStrategy();
    Move move = strategy.chooseMove(mock, Player.RED);

    assertNotNull(move);
    assertEquals(1, move.getRow());
    assertEquals(1, move.getColumn());
  }

  @Test
  public void testControlBoardStrategyTieBreakingCard() {
    List<Card> hand = new ArrayList<>();
    hand.add(new PlayingCard("Card1", 1, 1, simpleInfluence));
    hand.add(new PlayingCard("Card2", 1, 1, simpleInfluence));

    boolean[][] legalMoves = new boolean[3][3];
    legalMoves[1][1] = true;

    int[][] rowScores = {{0, 0}, {0, 0}, {0, 0}};

    MockSanguineModel mock = new MockSanguineModel(hand, 3, 3, Player.RED,
        legalMoves, rowScores);

    PlayerStrategy strategy = new ControlBoardStrategy();
    Move move = strategy.chooseMove(mock, Player.RED);

    assertNotNull(move);
    assertEquals(0, move.getCardIndex());
  }

  @Test
  public void testCompositeStrategyFallsBackToSecondStrategy() {
    List<Card> hand = new ArrayList<>();
    hand.add(new PlayingCard("Card1", 1, 1, simpleInfluence));

    boolean[][] legalMoves = new boolean[3][3];
    legalMoves[1][1] = true;

    int[][] rowScores = {{0, 10}, {0, 10}, {0, 10}};

    MockSanguineModel mock = new MockSanguineModel(hand, 3, 3, Player.RED,
        legalMoves, rowScores);

    PlayerStrategy composite = new CompositeStrategy(
        new MaxRowScoreStrategy(),
        new FillFirstStrategy()
    );

    Move move = composite.chooseMove(mock, Player.RED);

    assertNotNull(move);
    assertEquals(1, move.getRow());
    assertEquals(1, move.getColumn());
  }

  @Test
  public void testCompositeStrategyUsesFirstWhenAvailable() {
    List<Card> hand = new ArrayList<>();
    hand.add(new PlayingCard("Card1", 1, 5, simpleInfluence));

    boolean[][] legalMoves = new boolean[3][3];
    legalMoves[0][0] = true;
    legalMoves[1][1] = true;

    int[][] rowScores = {{2, 5}, {2, 5}, {2, 5}};

    MockSanguineModel mock = new MockSanguineModel(hand, 3, 3, Player.RED,
        legalMoves, rowScores);

    PlayerStrategy composite = new CompositeStrategy(
        new MaxRowScoreStrategy(),
        new FillFirstStrategy()
    );

    Move move = composite.chooseMove(mock, Player.RED);

    assertNotNull(move);
    assertEquals(0, move.getRow());
  }

  @Test
  public void testMinOpponentStrategyChoosesMinimizingMove() {
    List<Card> hand = new ArrayList<>();
    hand.add(new PlayingCard("Card1", 1, 1, simpleInfluence));

    boolean[][] legalMoves = new boolean[3][3];
    legalMoves[0][0] = true;
    legalMoves[1][0] = true;

    int[][] rowScores = {{0, 0}, {0, 10}, {0, 0}};

    MockSanguineModel mock = new MockSanguineModel(hand, 3, 3, Player.RED,
        legalMoves, rowScores);

    PlayerStrategy minimax = new MinOpponentStrategy(new MaxRowScoreStrategy());
    Move move = minimax.chooseMove(mock, Player.RED);

    assertNotNull(move);
    assertEquals(0, move.getRow());
  }

  @Test
  public void testStrategiesDifferOnSameBoard() {
    List<Card> deck = new ArrayList<>();
    deck.add(new PlayingCard("Weak", 1, 1, simpleInfluence));
    deck.add(new PlayingCard("Strong", 1, 5, crossInfluence));
    for (int i = 0; i < 18; i++) {
      deck.add(new PlayingCard("Card" + i, 1, 1, simpleInfluence));
    }

    BasicSanguine model = new BasicSanguine();
    model.startGame(deck, deck, 3, 5, 5);

    PlayerStrategy fillFirst = new FillFirstStrategy();
    PlayerStrategy maxRow = new MaxRowScoreStrategy();
    PlayerStrategy control = new ControlBoardStrategy();

    Move move1 = fillFirst.chooseMove(model, Player.RED);
    Move move2 = maxRow.chooseMove(model, Player.RED);
    Move move3 = control.chooseMove(model, Player.RED);

    assertNotNull(move1);
    assertNotNull(move2);
    assertNotNull(move3);
  }

  @Test
  public void testFillFirstChoosesUppermostLeftmost() {
    List<Card> deck = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      deck.add(new PlayingCard("Card" + i, 1, 1, simpleInfluence));
    }

    BasicSanguine model = new BasicSanguine();
    model.startGame(deck, deck, 3, 5, 5);

    PlayerStrategy strategy = new FillFirstStrategy();
    Move move = strategy.chooseMove(model, Player.RED);

    assertNotNull(move);
    assertEquals(0, move.getCardIndex());
    assertEquals(0, move.getRow());
    assertEquals(0, move.getColumn());
  }

  @Test
  public void testMaxRowScoreChoosesFirstWinningMove() {
    List<Card> hand = new ArrayList<>();
    hand.add(new PlayingCard("Card1", 1, 5, simpleInfluence));
    hand.add(new PlayingCard("Card2", 1, 5, simpleInfluence));

    boolean[][] legalMoves = new boolean[3][3];
    legalMoves[1][0] = true;
    legalMoves[1][1] = true;
    legalMoves[1][2] = true;

    int[][] rowScores = {{10, 0}, {0, 3}, {10, 0}};

    MockSanguineModel mock = new MockSanguineModel(hand, 3, 3, Player.RED,
        legalMoves, rowScores);

    PlayerStrategy strategy = new MaxRowScoreStrategy();
    Move move = strategy.chooseMove(mock, Player.RED);

    assertNotNull(move);
    assertEquals(1, move.getRow());
    assertEquals(0, move.getCardIndex());
    assertEquals(0, move.getColumn());
  }
}