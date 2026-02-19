package sanguine.controller;

import sanguine.model.ReadOnlySanguineModel;

/**
 * Represents a strategy for choosing moves in Sanguine.
 */
public interface PlayerStrategy {

  /**
   * Represents choosing the next move based on the current game state.
   *
   * @param model the current read-only model
   * @return move representing the card index and target position, else null
   */
  Move chooseMove(ReadOnlySanguineModel model);

  /**
   * Represents a move in the game.
   */
  class Move {
    public final int cardIndex;
    public final int row;
    public final int col;

    public Move(int cardIndex, int row, int col) {
      this.cardIndex = cardIndex;
      this.row = row;
      this.col = col;
    }
  }
}
