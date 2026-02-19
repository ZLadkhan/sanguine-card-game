package sanguine.strategy;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.PlayingCard;

/**
 * Represents tests for generating transcripts to verify strategy behavior.
 */
public class TranscriptTests {
  private boolean[][] simpleInfluence;

  /**
   * Sets up tests.
   */
  @Before
  public void setUp() {
    simpleInfluence = new boolean[5][5];
    simpleInfluence[2][2] = true;
    simpleInfluence[2][3] = true;
  }

  @Test
  public void generateTranscriptForFillFirst() {
    List<Card> hand = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      hand.add(new PlayingCard("Card" + i, 1, 1, simpleInfluence));
    }

    boolean[][] legalMoves = new boolean[3][5];
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 5; c++) {
        legalMoves[r][c] = false;
      }
    }
    legalMoves[2][4] = true;

    int[][] rowScores = {{0, 0}, {0, 0}, {0, 0}};

    MockSanguineModel mock = new MockSanguineModel(hand, 3, 5, Player.RED,
        legalMoves, rowScores);

    PlayerStrategy strategy = new FillFirstStrategy();
    Move move = strategy.chooseMove(mock, Player.RED);

    System.out.println("=== FillFirstStrategy Transcript ===");
    for (String call : mock.getTranscript()) {
      System.out.println(call);
    }
    System.out.println("\nChosen move: " + move);

    assertNotNull(move);
  }

  @Test
  public void generateTranscriptForMaxRowScore() {
    List<Card> hand = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      hand.add(new PlayingCard("Card" + i, 1, 5, simpleInfluence));
    }

    boolean[][] legalMoves = new boolean[3][5];
    legalMoves[2][4] = true;

    int[][] rowScores = {{10, 0}, {10, 0}, {0, 3}};

    MockSanguineModel mock = new MockSanguineModel(hand, 3, 5, Player.RED,
        legalMoves, rowScores);

    PlayerStrategy strategy = new MaxRowScoreStrategy();
    Move move = strategy.chooseMove(mock, Player.RED);

    System.out.println("=== MaxRowScoreStrategy Transcript ===");
    for (String call : mock.getTranscript()) {
      System.out.println(call);
    }
    System.out.println("\nChosen move: " + move);

    assertNotNull(move);
  }
}