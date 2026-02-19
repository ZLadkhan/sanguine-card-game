package sanguine.strategy;

import sanguine.model.Card;
import sanguine.model.Cell;
import sanguine.model.Player;

/**
 * Configurable mock cell for testing strategies.
 */
public class MockCell implements Cell {
  private final Card card;
  private final Player cardOwner;
  private final int pawnCount;
  private final Player pawnOwner;

  /**
   * Constructs an empty mock cell.
   */
  public MockCell() {
    this(null, null, 0, null);
  }

  /**
   * Constructs a mock cell with specified properties.
   *
   * @param card the card in the cell
   * @param cardOwner the owner of the card
   * @param pawnCount the number of pawns
   * @param pawnOwner the owner of the pawns
   */
  public MockCell(Card card, Player cardOwner, int pawnCount, Player pawnOwner) {
    this.card = card;
    this.cardOwner = cardOwner;
    this.pawnCount = pawnCount;
    this.pawnOwner = pawnOwner;
  }

  @Override
  public boolean isCellEmpty() {
    return card == null && pawnCount == 0;
  }

  @Override
  public boolean hasPawns() {
    return pawnCount > 0;
  }

  @Override
  public int getPawnCount() {
    return pawnCount;
  }

  @Override
  public Player getPawnOwner() {
    return pawnOwner;
  }

  @Override
  public Card getCard() {
    return card;
  }

  @Override
  public Player getCardOwner() {
    return cardOwner;
  }
}