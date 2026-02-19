package sanguine.controller;

/**
 * Represents the features and actions that can
 * be performed in Sanguine.
 */
public interface Features {

  /**
   * Represents calling a card in the hand is clicked.
   *
   * @param cardIndex the index of the card clicked
   */
  void handleCardClick(int cardIndex);

  /**
   * Represents situation where a cell on the board is clicked.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   */
  void handleCellClick(int row, int col);

  /**
   * Represents a call when user confirms their move.
   */
  void handleConfirm();

  /**
   * Represents a call when user passes their turn.
   */
  void handlePass();
}
