package sanguine.model;

/**
 * Represents the read only state of an arbitrary cell in the game board of Sanguine.
 * Cells can contain pawns with values one and three inclusive, a card, or can be empty.
 * Cells with cards cannot change ownership, and cells with pawns can change ownership.
 */
public interface Cell {

  /**
   * Represents checking if the cell contents are empty.
   * Cells are empty if they have no pawns and no cards.
   *
   * @return true if the cell contents are empty, otherwise return boolean false
   */
  boolean isCellEmpty();

  /**
   * Represents checking if the cell contains pawns.
   *
   * @return true if the cell contains one or more pawns, else return boolean false
   */
  boolean hasPawns();

  /**
   * Represents checking the amount of pawns in a cell.
   *
   * @return pawn count between 1 and 3 inclusive, 0 if no pawns
   */
  int getPawnCount();

  /**
   * Represents getting the owner of the pawns in the current cell.
   *
   * @return RED if the pawns belong to the red player, else BLUE if the pawns belong to BLUE player
   */
  Player getPawnOwner();

  /**
   * Represents checking if the cell has a card.
   *
   * @return Card that is of interest to us
   */
  Card getCard();

  /**
   * Represents getting the owner of the card in the current cell.
   *
   * @return RED if the cell has a card belonging to the red player else BLUE for blue player
   */
  Player getCardOwner();
}
