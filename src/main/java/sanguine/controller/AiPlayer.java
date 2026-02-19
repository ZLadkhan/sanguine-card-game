package sanguine.controller;

import java.util.ArrayList;
import java.util.List;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.strategy.Move;
import sanguine.strategy.PlayerStrategy;

/**
 * Represents an AI player that uses a strategy to make decisions.
 */
public class AiPlayer implements PlayerComponent {

  private final List<Features> listeners;
  private final PlayerStrategy strategy;

  /**
   * Creates a new AI player with the given strategy.
   *
   * @param strategy the strategy to use for making moves
   */
  public AiPlayer(PlayerStrategy strategy) {
    this.listeners = new ArrayList<>();
    this.strategy = strategy;
  }

  @Override
  public void addFeaturesListener(Features listener) {
    this.listeners.add(listener);
  }

  @Override
  public void notifyTurn(ReadOnlySanguineModel model) {
    Move move = strategy.chooseMove(model, model.getCurrentPlayer());

    if (move == null) {
      for (Features listener : listeners) {
        listener.handlePass();
      }
    } else {
      for (Features listener : listeners) {
        listener.handleCardClick(move.getCardIndex());
        listener.handleCellClick(move.getRow(), move.getColumn());
        listener.handleConfirm();
      }
    }
  }
}