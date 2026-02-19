package sanguine;

import java.io.IOException;
import java.util.List;
import sanguine.model.Card;

/**
 * Represents a reader for loading deck configurations from files.
 * Implementations of this interface parse deck files and create lists of cards
 * that can be used to start a game of Sanguine.
 */
public interface DeckReader {
  /**
   * Represents reading the deck file and creating a list of cards from it.
   *
   * @param file represents the file
   * @return the list of cards in the order they appear in the deck file
   * @throws IOException if the file is not readable
   * @throws IllegalArgumentException if the file is not in a valid format
   */
  List<Card> readDeck(String file) throws IOException;
}
