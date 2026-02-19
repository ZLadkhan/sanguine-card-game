package sanguine.strategy;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;


/**
 * A strategy that chooses the first valid card and location.
 * This strategy iterates through cards from left to right in the hand,
 * and for each card, checks board positions from top to bottom, left to right.
 */
public class FillFirstStrategy implements PlayerStrategy {

  /**
   * Chooses the first valid move by checking cards and positions in order.
   * Iterates through cards left to right, and for each card checks positions
   * top to bottom, left to right.
   *
   * @param model the read-only model to analyze
   * @param player the player to choose a move for
   * @return the first valid Move, or null if no valid move exists
   */
  @Override
  public Move chooseMove(ReadOnlySanguineModel model, Player player) {
    List<Card> hand = model.getCurrentPlayerHand();
    int[] dims = model.getGameBoardDimensions();
    int rows = dims[0];
    int cols = dims[1];

    for (int cardIndex = 0; cardIndex < hand.size(); cardIndex++) {
      for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols; col++) {
          if (model.isLegalMove(cardIndex, row, col)) {
            return new Move(cardIndex, row, col);
          }
        }
      }
    }

    return null;
  }
}