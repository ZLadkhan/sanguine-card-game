package sanguine.controller;

import sanguine.model.Player;

/**
 * Represents the features interface for model status notifications.
 * Listeners can be notified when game state changes occur.
 */
public interface ModelFeatures {

  /**
   * Represents being called when the active player changes.
   *
   * @param currentPlayer the player whose turn it now is
   */
  void onTurnChanged(Player currentPlayer);

  /**
   * Represents being called when the game ends.
   */
  void onGameOver();
}
