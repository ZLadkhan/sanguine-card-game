package sanguine.strategy;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.Cell;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;

/**
 * A strategy that attempts to maximize row scores by winning rows.
 * This strategy visits rows from top to bottom and attempts to find a move
 * that will give the current player a higher row-score than their opponent.
 * For each row, checks cards left to right, then positions left to right.
 */
public class MaxRowScoreStrategy implements PlayerStrategy {

  /**
   * Chooses a move that maximizes row score by winning a row.
   * Visits rows from top to bottom. For each row where the player's score
   * is less than or equal to the opponent's, finds the first valid move
   * that would make the player's score higher than the opponent's.
   * Checks cards in hand order (left to right), then positions left to right.
   *
   * @param model  the read-only model to analyze
   * @param player the player to choose a move for
   * @return a Move that wins a row, or null if no such move exists
   */
  @Override
  public Move chooseMove(ReadOnlySanguineModel model, Player player) {
    List<Card> hand = model.getCurrentPlayerHand();
    int[] dims = model.getGameBoardDimensions();
    int rows = dims[0];
    int cols = dims[1];

    for (int row = 0; row < rows; row++) {
      int[] rowScores = model.getRowScore(row);
      int playerScore = player == Player.RED ? rowScores[0] : rowScores[1];
      int opponentScore = player == Player.RED ? rowScores[1] : rowScores[0];

      if (playerScore <= opponentScore) {
        for (int cardIndex = 0; cardIndex < hand.size(); cardIndex++) {
          Card card = hand.get(cardIndex);

          for (int col = 0; col < cols; col++) {
            if (model.isLegalMove(cardIndex, row, col)) {
              if (wouldWinRow(model, card, row, col, playerScore,
                  opponentScore, player)) {
                return new Move(cardIndex, row, col);
              }
            }
          }
        }
      }
    }

    return null;
  }

  /**
   * Determines if placing a card at this cell would win the row.
   *
   * @param model                the read-only model
   * @param card                 the card to place
   * @param row                  the row being considered
   * @param col                  the column being considered
   * @param currentPlayerScore   the player's current score in the row
   * @param currentOpponentScore the opponent's current score in the row
   * @param player               the player making the move
   * @return true if this move would win the row
   */
  private boolean wouldWinRow(ReadOnlySanguineModel model, Card card, int row, int col,
                              int currentPlayerScore, int currentOpponentScore, Player player) {
    Cell cell = model.getCellAt(row, col);
    int newPlayerScore = currentPlayerScore + card.getValue();

    if (cell.getCard() != null && cell.getCardOwner() != player) {
      newPlayerScore = currentPlayerScore + card.getValue() - cell.getCard().getValue();
    }

    return newPlayerScore > currentOpponentScore;
  }
}