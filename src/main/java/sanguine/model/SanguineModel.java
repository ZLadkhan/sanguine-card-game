package sanguine.model;

import java.util.List;

/**
 * Represents an interface for the game of Sanguine.
 */
public interface SanguineModel extends ReadOnlySanguineModel {

  /**
   * Represents starting the game of Sanguine.
   *
   * @throws IllegalStateException if the game has already started
   */
  void startGame(List<Card> redDeck, List<Card> blueDeck, int rows, int columns,
                 int handSize);

  /**
   * Represents the player who currently has a turn to place a card in the game.
   *
   * @param currentHandIndex represents the card index of the card in the player's hand
   * @param row              represents the current board row
   * @param column           represents the current column row
   * @throws IllegalStateException    if the game has not begun yet or has concluded
   * @throws IllegalArgumentException if the move initiated by the player is not valid
   */
  void placeCard(int currentHandIndex, int row, int column);

  /**
   * Represents the current player choosing to pass their turn.
   *
   * @throws IllegalStateException if the game has not begun yet or has concluded
   */
  void passTurn();

  /**
   * Represents getting the current player that has a turn in real time.
   * Returns null if the game has not concluded or is tied.
   */
  Player getCurrentPlayer();

  /**
   * Represents checking to see if the game has concluded.
   *
   * @throws IllegalStateException if the game has not begun yet or has concluded
   */
  boolean isGameOver();

  /**
   * Represents getting the winner of the game.
   * Returns null if the game has not concluded yet or if the players tied.
   */
  Player getWinningPlayer();

  /**
   * Represents getting the current hand of the player.
   *
   * @throws IllegalStateException if the game has not begun yet or has concluded
   */
  List<Card> getCurrentPlayerHand();

  /**
   * Represents getting the number of cards left in deck of the current player.
   */
  int getCurrentDeckSize();

  /**
   * Adds a listener for model status events.
   *
   * @param listener the ModelFeatures listener to add
   */
  void addModelListener(sanguine.controller.ModelFeatures listener);
}
