package sanguine.strategy;

import java.util.ArrayList;
import java.util.List;
import sanguine.model.Card;
import sanguine.model.Cell;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;


/**
 * A mock implementation of ReadOnlySanguineModel that records method calls.
 * Used for testing strategy behavior without depending on a full model implementation.
 */
public class MockSanguineModel implements ReadOnlySanguineModel {
  private final List<String> transcript;
  private final List<Card> hand;
  private final int rows;
  private final int cols;
  private final Player currentPlayer;
  private final boolean[][] legalMoves;
  private final int[][] rowScores;

  /**
   * Constructs a mock model with specified configuration.
   *
   * @param hand the player's hand
   * @param rows number of rows
   * @param cols number of columns
   * @param currentPlayer the current player
   * @param legalMoves 2D array indicating legal moves [row][col]
   * @param rowScores scores for each row [row][0=red, 1=blue]
   */
  public MockSanguineModel(List<Card> hand, int rows, int cols, Player currentPlayer,
                           boolean[][] legalMoves, int[][] rowScores) {
    this.transcript = new ArrayList<>();
    this.hand = hand;
    this.rows = rows;
    this.cols = cols;
    this.currentPlayer = currentPlayer;
    this.legalMoves = legalMoves;
    this.rowScores = rowScores;
  }

  /**
   * Gets the transcript of method calls.
   *
   * @return list of method calls recorded
   */
  public List<String> getTranscript() {
    return new ArrayList<>(transcript);
  }

  /**
   * Clears the transcript.
   */
  public void clearTranscript() {
    transcript.clear();
  }

  @Override
  public int[] getGameBoardDimensions() {
    transcript.add("getGameBoardDimensions()");
    return new int[]{rows, cols};
  }

  @Override
  public Cell getCellAt(int row, int column) {
    transcript.add("getCellAt(" + row + ", " + column + ")");
    return new MockCell();
  }

  @Override
  public int[] getFinalScores() {
    transcript.add("getFinalScores()");
    return new int[]{0, 0};
  }

  @Override
  public boolean isGameOver() {
    transcript.add("isGameOver()");
    return false;
  }

  @Override
  public Player getWinner() {
    transcript.add("getWinner()");
    return null;
  }

  @Override
  public Player getCurrentPlayer() {
    transcript.add("getCurrentPlayer()");
    return currentPlayer;
  }

  @Override
  public List<Card> getCurrentPlayerHand() {
    transcript.add("getCurrentPlayerHand()");
    return new ArrayList<>(hand);
  }

  @Override
  public int getCurrentDeckSize() {
    transcript.add("getCurrentDeckSize()");
    return 0;
  }

  @Override
  public boolean isLegalMove(int handIndex, int row, int column) {
    transcript.add("isLegalMove(" + handIndex + ", " + row + ", " + column + ")");
    if (row >= 0 && row < rows && column >= 0 && column < cols) {
      return legalMoves[row][column];
    }
    return false;
  }

  @Override
  public int[] getRowScore(int row) {
    transcript.add("getRowScore(" + row + ")");
    if (row >= 0 && row < rowScores.length) {
      return rowScores[row].clone();
    }
    return new int[]{0, 0};
  }
}