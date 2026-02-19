package sanguine.view;

import sanguine.controller.Features;

/**
 * Represents the view interface for Sanguine.
 * Views are responsible for displaying the game state and
 * capturing user interactions.
 */
public interface SanguineView {

  /**
   * Represents adding a features listener to the view.
   *
   * @param features the listener for player actions
   */
  void addFeaturesListener(Features features);

  /**
   * Represents refreshing the view to reflect the actual model state.
   */
  void refresh();

  /**
   * Represents displaying a message to the player.
   *
   * @param message the message to display
   */
  void showMessage(String message);

  /**
   * Represents highlighting a card in the hand display.
   *
   * @param cardIndex the index of the card to highlight
   */
  void highlightCard(int cardIndex);

  /**
   * Represents highlighting a cell on the board.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   */
  void highlightCell(int row, int col);

  /**
   * Represents clearing all of the current highlights.
   */
  void clearHighlights();

  /**
   * Represents the setter for the title of the view window.
   *
   * @param title the title to display
   */
  void setTitle(String title);

  /**
   * Represents making the view either visible or invisible.
   *
   * @param visible true to show the view, else false for invisible
   */
  void setVisible(boolean visible);
}
