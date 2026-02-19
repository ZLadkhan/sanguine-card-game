package sanguine.model;

/**
 * Represents a cell in the game board whose contents can change over time.
 */
public class GameCell implements Cell {
  private int pawnCount;
  private Player pawnOwner;
  private Card card;
  private Player cardOwner;

  /**
   * Represents creating a cell in the game board of Sanguine.
   */
  public GameCell() {
    this.pawnCount = 0;
    this.pawnOwner = null;
    this.card = null;
    this.cardOwner = null;
  }

  /**
   * Represents checking to see if the specified cell is empty and contains no pawns or cards.
   *
   * @return true if the cell has no contents, else return boolean false
   */
  @Override
  public boolean isCellEmpty() {
    return this.pawnCount == 0 && this.card == null;
  }

  /**
   * Represents checking to see if the cell contains pawns.
   *
   * @return true if the cell contains a pawn, else return boolean false
   */
  @Override
  public boolean hasPawns() {
    return this.pawnCount > 0;
  }

  /**
   * Represents getting the pawn count for a particular cell.
   *
   * @return the number of pawns in a specified cell
   */
  @Override
  public int getPawnCount() {
    return this.pawnCount;
  }

  /**
   * Represents getting the player who owns the pawn in a particular cell.
   *
   * @return BLUE if the blue player owns the pawn in the cell, else return RED for the red player
   */
  @Override
  public Player getPawnOwner() {
    return this.pawnOwner;
  }

  /**
   * Represents getting the card specified in the current cell.
   *
   * @return the card that is in the cell
   */
  @Override
  public Card getCard() {
    return this.card;
  }

  /**
   * Represents getting the owner of the card in a particular cell.
   *
   * @return RED if the card belongs to the RED player, else return BLUE for the blue player
   */
  @Override
  public Player getCardOwner() {
    return this.cardOwner;
  }

  /**
   * Represents adding one pawn for either the red or blue player in a particular cell.
   * If the cell already has a maximum pawn count of three, then this action is not valid.
   *
   * @param newOwner represents the player who owns the pawn which is being added to the cell
   */
  public void addPawn(Player newOwner) {
    if (this.pawnCount < 3) {
      this.pawnCount++;
    }
    this.pawnOwner = newOwner;
  }

  /**
   * Represents transferring ownership of pawns in an existing cell to the other player.
   *
   * @param newOwner represents the other player who now has ownership over the pawns
   */
  public void convertPawns(Player newOwner) {
    if (this.pawnCount > 0) {
      this.pawnOwner = newOwner;
    }
  }

  /**
   * Represents placing a card inside the cell and discarding any pawns previously inside the cell.
   *
   * @param card  represents the card which is being placed inside the cell
   * @param owner represents the player that owns the card whcih is being placed inside the cell
   */
  public void placeCard(Card card, Player owner) {
    this.card = card;
    this.cardOwner = owner;
    this.pawnCount = 0;
    this.pawnOwner = null;
  }
}
