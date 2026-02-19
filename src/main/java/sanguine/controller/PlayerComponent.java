package sanguine.controller;

/**
 * Represents a component that can emit player actions.
 * This abstracts human players (who act through views) from
 * AI players (who use strategies to decide actions).
 */
public interface PlayerComponent {

  /**
   * Represents adding a listener for player action events from this component.
   *
   * @param listener the Features listener to add
   */
  void addFeaturesListener(Features listener);

  /**
   * Represents notifying this player component that it is their turn.
   * For AI players, this triggers the strategy to compute a move.
   *
   * @param model the current read-only model state
   */
  void notifyTurn(sanguine.model.ReadOnlySanguineModel model);
}
