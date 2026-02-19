package sanguine.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a playing card in the game of Sanguine.
 * Card cannot change its contents, fixed with name, cost, value, and influence.
 */
public class PlayingCard implements Card {
  private final String name;
  private final int cost;
  private final int value;
  private final boolean[][] influenceGrid;

  /**
   * Represents the creation of a playing card in the game of Sanguine.
   *
   * @param name          represents the name of the card
   * @param cost          represents the cost of a pawn from one to three inclusive
   * @param value         represents the value of a card as a positive integer
   * @param influenceGrid represents the 5x5 grid of the current game
   * @throws IllegalArgumentException if any of the above params are not valid
   */
  public PlayingCard(String name, int cost, int value, boolean[][] influenceGrid) {
    if (cost < 1 || cost > 3) {
      throw new IllegalArgumentException("Cost must be between 1 and 3 inclusive");
    }

    if (value <= 0) {
      throw new IllegalArgumentException("Value must be positive");
    }

    if (influenceGrid.length != 5) {
      throw new IllegalArgumentException("Grid must be 5x5");
    }

    for (int i = 0; i < 5; i++) {
      if (influenceGrid[i] == null || influenceGrid[i].length != 5) {
        throw new IllegalArgumentException("The grid must have 5x5 dimensions.");
      }
    }

    if (!influenceGrid[2][2]) {
      throw new IllegalArgumentException("The card in the center needs to have an influence.");
    }

    this.name = name;
    this.cost = cost;
    this.value = value;
    this.influenceGrid = deepCopyOfGrid(influenceGrid);
  }

  /**
   * Represents making a deep copy of a two-dimensional boolean array.
   *
   * @param original represents the array we make a deep copy of
   * @return array which is the new array with the same values as the original array
   */
  private static boolean[][] deepCopyOfGrid(boolean[][] original) {
    boolean[][] deepCopy = new boolean[5][5];
    for (int i = 0; i < 5; i++) {
      deepCopy[i] = Arrays.copyOf(original[i], 5);
    }

    return deepCopy;
  }

  /**
   * Represents getting the name of a particular card.
   *
   * @return the name of the card
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Represents getting the specific cost associated with a particular card.
   *
   * @return the associated cost with a card
   */
  @Override
  public int getCost() {
    return cost;
  }

  /**
   * Represents getting the value score associated with a particular card.
   *
   * @return the value score of a specified card
   */
  @Override
  public int getValue() {
    return value;
  }

  /**
   * Represents validating whether the card has influence at a particular location.
   *
   * @param row          represents the relative row in the 5x5 grid labeled
   *                     from zero to four inclusive
   * @param col          represents the relative column in 5x5 grid labeled from
   *                     zero to four inclusive
   * @param isBluePlayer represents whether to mirror influence for blue player
   * @return true if there is influence at the particular grid location, else return boolean false
   */
  @Override
  public boolean hasInfluenceAt(int row, int col, boolean isBluePlayer) {
    if (row < 0 || row > 4) {
      throw new IllegalArgumentException("Rows must be between one and four inclusive.");
    }

    if (col < 0 || col > 4) {
      throw new IllegalArgumentException("Columns must be between one and four inclusive.");
    }

    if (isBluePlayer) {
      col = 4 - col;
    }

    return influenceGrid[row][col];
  }

  /**
   * Represents checking if this card is equivalent to that card.
   *
   * @param other represents the other card being checked against this card
   * @return true if the two cards have equivalent contents, else return boolean false
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (!(other instanceof Card that)) {
      return false;
    }

    if (!this.name.equals(that.getName())
        || this.cost != that.getCost()
        || this.value != that.getValue()) {
      return false;
    }

    return compareInfluenceGrids(that);
  }

  /**
   * Represents calculating the hash code for a particular card.
   *
   * @return the hash code value for this card
   */
  @Override
  public int hashCode() {
    int result = Objects.hash(name, cost, value);
    result = 31 * result + Arrays.deepHashCode(influenceGrid);

    return result;
  }

  /**
   * Represents reformatting a card with specified name, cost, and value score.
   *
   * @return the card parameters reformatted in string format
   */
  @Override
  public String toString() {
    return String.format("%s (Cost: %d, Value: %d)", name, cost, value);
  }

  /**
   * Represents comparing the influence grid of this card versus another.
   *
   * @param other represents the other card
   * @return true if the influence patterns of this and the other card are similar
   */
  private boolean compareInfluenceGrids(Card other) {
    for (int row = 0; row < 5; row++) {
      for (int column = 0; column < 5; column++) {
        if (this.influenceGrid[row][column] != other.hasInfluenceAt(row, column, false)) {
          return false;
        }
      }
    }

    return true;
  }
}
