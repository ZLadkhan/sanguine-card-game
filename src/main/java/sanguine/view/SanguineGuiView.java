package sanguine.view;

import sanguine.controller.Features;

/**
 * Represents a GUI view for the Sanguine game.
 */
public interface SanguineGuiView {

  /**
   * Adds a features/controller listener to handle user interactions.
   *
   * @param features the controller that will handle events
   */
  void addFeaturesListener(Features features);

  /**
   * Refreshes the view to reflect current model state.
   */
  void refresh();

  /**
   * Displays a message to the user.
   *
   * @param message the message to display
   */
  void showMessage(String message);

  /**
   * Highlights a card in the hand display.
   *
   * @param cardIndex the index of the card to highlight
   */
  void highlightCard(int cardIndex);

  /**
   * Highlights a cell on the board.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   */
  void highlightCell(int row, int col);

  /**
   * Clears all current highlights.
   */
  void clearHighlights();

  /**
   * Sets the title of the view window.
   *
   * @param title the title to display
   */
  void setTitle(String title);

  /**
   * Makes the view visible or invisible.
   *
   * @param visible true to show, false to hide
   */
  void setVisible(boolean visible);
}
