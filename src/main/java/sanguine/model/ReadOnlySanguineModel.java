package sanguine.model;

import java.util.List;

/**
 * Represents a read-only view of the Sanguine game model.
 * This interface provides observation methods to query the game state
 * without allowing any modifications to the model, ensuring that views
 * cannot accidentally mutate the game state.
 */
public interface ReadOnlySanguineModel {

  /**
   * Represents getting the Sanguine game board dimensions.
   *
   * @return array where rows are zero indexed and columns are one indexed
   * @throws IllegalStateException if the game has not begun yet
   */
  int[] getGameBoardDimensions();

  /**
   * Represents getting the current state of a specified cell on the game board.
   * The coordinate system uses row-first ordering: (row, column).
   *
   * @param row represents the zero indexed row of the game board
   * @param column represents the one indexed column of the game board
   * @return Cell which is comprised of the contents of the cell
   * @throws IllegalStateException if the game has not begun yet
   * @throws IllegalArgumentException if the cell coordinates are not valid
   */
  Cell getCellAt(int row, int column);

  /**
   * Represents getting the total scores for the Red and Blue players across rows.
   * System for winning works such that the player with the highest row score gets all points.
   * The player with the lower score for that row gets zero total points for that row.
   * If the players have a tied score for the row, then both players get zero points for that row.
   *
   * @return array where index of zero is the red score and index of one is the blue score
   */
  int[] getFinalScores();

  /**
   * Represents checking if the game of Sanguine has concluded.
   * Note the game only ends when both of the players pass their turns in adjacent motions.
   *
   * @return true if both of the players pass their turns one after another, else return false
   */
  boolean isGameOver();

  /**
   * Represents getting the player that won the game of Sanguine.
   *
   * @return RED if the red player won or BLUE if the blue player won, else return null for ties
   */
  Player getWinner();

  /**
   * Represents getting the player whose turn it is currently.
   *
   * @return RED if it is the Red Player's turn, else return BLUE
   * @throws IllegalStateException if the game has not begun yet or has been concluded
   */
  Player getCurrentPlayer();

  /**
   * Represents getting the hand of the current player.
   *
   * @return the hand of the current player as a defensive copy
   * @throws IllegalStateException if the game has not begun yet or has been concluded
   */
  List<Card> getCurrentPlayerHand();

  /**
   * Represents getting the number of remaining cards in the deck of the current player.
   *
   * @return the deck size, which is either zero or a positive integer value
   * @throws IllegalStateException if the game has not begun yet or has been concluded
   */
  int getCurrentDeckSize();

  /**
   * Represents checking if it is legal for the current player to play a card at the given position.
   * The coordinate system uses row-first ordering: (row, column).
   *
   * @param handIndex the index of the card in the current player's hand
   * @param row the target zero-indexed row on the board
   * @param column the target zero-indexed column on the board
   * @return true if the move is legal, false otherwise
   */
  boolean isLegalMove(int handIndex, int row, int column);

  /**
   * Represents getting the score for both players in a specific row.
   * The coordinate system uses row-first ordering.
   *
   * @param row the zero-indexed row of the game board
   * @return array where index 0 is the red player's score and index 1 is the blue player's score
   * @throws IllegalStateException if the game has not started
   * @throws IllegalArgumentException if the row index is invalid
   */
  int[] getRowScore(int row);
}
