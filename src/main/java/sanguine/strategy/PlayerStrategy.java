package sanguine.strategy;

import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Represents a strategy for choosing moves in Sanguine.
 * Strategies analyze the game state and recommend moves for a player.
 */
public interface PlayerStrategy {

  /**
   * Chooses a move for the given player based on this strategy.
   * Returns null if no valid move exists (player should pass).
   *
   * @param model the read-only model to analyze
   * @param player the player to choose a move for
   * @return a Move object representing the chosen card and position, or null if no valid move
   */
  Move chooseMove(ReadOnlySanguineModel model, Player player);
}