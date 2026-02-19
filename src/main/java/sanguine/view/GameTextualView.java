package sanguine.view;

import java.io.IOException;

/**
 * Represents rendering the game of Sanguine.
 */
public interface GameTextualView {

  /**
   * Represents rendering the current game state using string format.
   *
   * @return the game board using a string representation
   */
  String toString();

  /**
   * Represents the state of Sanguine in a string format.
   *
   * @param output where to write the rendering to
   * @throws IOException if the writing does not work
   */
  void render(Appendable output) throws IOException;
}
