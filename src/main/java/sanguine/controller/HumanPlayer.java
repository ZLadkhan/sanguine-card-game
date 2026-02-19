package sanguine.controller;

import java.util.ArrayList;
import java.util.List;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Represents a human player who interacts through the GUI.
 * This does not actually emit any events since all of the player
 * actions come from the view when the human player interacts with the game.
 */
public class HumanPlayer implements PlayerComponent {

  private final List<Features> listeners;

  /**
   * Creates a new human player component.
   */
  public HumanPlayer() {
    this.listeners = new ArrayList<>();
  }

  @Override
  public void addFeaturesListener(Features listener) {
    this.listeners.add(listener);
  }

  @Override
  public void notifyTurn(ReadOnlySanguineModel model) {
  }
}
