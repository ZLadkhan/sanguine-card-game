package sanguine.view;

import sanguine.controller.Features;

/**
 * Interface for panels in the Sanguine GUI view.
 */
public interface SanguinePanel {
  /**
   * Adds a features listener to handle user interactions.
   *
   * @param features the controller to handle events
   */
  void addFeaturesListener(Features features);
}
