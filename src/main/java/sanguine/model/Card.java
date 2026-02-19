package sanguine.model;

/**
 * Represents an arbitrary card in Sanguine.
 * Cards in Sanguine have a name, cost, value, and a specified influence.
 */
public interface Card {

  /**
   * Represents getting the name of this card.
   *
   * @return the cards name as a string
   */
  String getName();

  /**
   * Represents getting the associated cost to play the card in terms of pawns.
   *
   * @return the cost in pawns, which is between one and three inclusive
   */
  int getCost();

  /**
   * Represents getting the current value score of the card for calculating final scores.
   *
   * @return the positive value score of the card, since value score is a positive integer
   */
  int getValue();

  /**
   * Represents validating if this card has influence at the given position.
   *
   * @param row          represents the relative row in the 5x5 grid labeled from 0-4
   * @param col          represents the relative column in 5x5 grid labeled from 0-4
   * @param isBluePlayer represents whether to mirror influence for blue player
   * @return true if the card influences that position, otherwise return false
   * @throws IllegalArgumentException if the row or column is not in range 0-4 inclusive
   */
  boolean hasInfluenceAt(int row, int col, boolean isBluePlayer);

  /**
   * Represents checking if the two cards are equivalent to one another.
   * We pronounce cards as being equivalent if they have the same properties:
   * - name, cost, value, and influence
   *
   * @param other represents the other card being checked against this card
   * @return true if this card and the other card are equivalent
   */
  @Override
  boolean equals(Object other);

  /**
   * Represents returning the hashCode for the current card.
   *
   * @return hashCode based on the name, cost, and influence of the card
   */
  @Override
  int hashCode();

  /**
   * Represents returning the card in string format.
   *
   * @return cardName where cardName is in the format "Cost: A, Value: B"
   */
  @Override
  String toString();
}

