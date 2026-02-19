package sanguine.strategy;

/**
 * Represents a move in Sanguine consisting of a card index and board position.
 */
public class Move {

  private final int cardIndex;
  private final int row;
  private final int column;

  /**
   * Constructs a move with the specified card and position.
   *
   * @param cardIndex the index of the card in the player's hand
   * @param row the target row on the board
   * @param column the target column on the board
   */
  public Move(int cardIndex, int row, int column) {
    this.cardIndex = cardIndex;
    this.row = row;
    this.column = column;
  }

  /**
   * Gets the card index for this move.
   *
   * @return the card index
   */
  public int getCardIndex() {
    return cardIndex;
  }

  /**
   * Gets the row for this move.
   *
   * @return the row
   */
  public int getRow() {
    return row;
  }

  /**
   * Gets the column for this move.
   *
   * @return the column
   */
  public int getColumn() {
    return column;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Move that)) {
      return false;
    }
    return this.cardIndex == that.cardIndex
        && this.row == that.row
        && this.column == that.column;
  }

  @Override
  public int hashCode() {
    return cardIndex * 1000 + row * 100 + column;
  }

  @Override
  public String toString() {
    return "Move{card=" + cardIndex + ", row=" + row + ", col=" + column + "}";
  }
}
