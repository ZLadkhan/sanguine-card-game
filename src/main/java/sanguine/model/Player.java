package sanguine.model;

/**
 * Represents an arbitrary player for one of the two players playing Sanguine.
 * Red represents the player who goes first and begins on the left side of the board
 * Blue represents the player who goes second and begins on the right side of the board
 */
public enum Player {
  RED, BLUE;

  /**
   * Represents returning the opponent of the current player.
   *
   * @return RED if the current player is BLUE, else return BLUE
   */
  public Player getOtherPlayer() {
    if (this == BLUE) {
      return RED;
    } else {
      return BLUE;
    }
  }

  /**
   * Represents returning the string representation of the current player.
   *
   * @return "Red" if the current player is RED, else return "Blue"
   */
  @Override
  public String toString() {
    if (this == RED) {
      return "Red";
    } else {
      return "Blue";
    }
  }

  /**
   * Represents returning the color of the player in all lowercase.
   * Used mainly for displaying the player type on any views.
   *
   * @return "red" if player is red, otherwise returns "blue"
   */
  public String getNameOfColor() {
    return this.name().toLowerCase();
  }

  /**
   * Represents returning the first character for use in textual views.
   *
   * @return 'R' if player is RED, otherwise returns 'B' for BLUE
   */
  public char getFirstCharOfColor() {
    if (this == RED) {
      return 'R';
    } else {
      return 'B';
    }
  }

  /**
   * Represents returning boolean true if the player begins on the left side of the board.
   * If the player is RED, then this should return boolean true, else return boolean false.
   *
   * @return true if the player is RED, else return boolean false
   */
  public boolean beginsLeftSide() {
    return this == RED;
  }

  /**
   * Represents returning boolean true if the player begins on the right side of the board.
   * If the player is BLUE, then this should return boolean true, else return boolean false.
   *
   * @return true if the player is BLUE, else return boolean false
   */
  public boolean beginsRightSide() {
    return this == BLUE;
  }
}
