package sanguine.strategy;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.Cell;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;


/**
 * A strategy that chooses moves to maximize cell ownership on the board.
 * This strategy evaluates all possible moves and selects the one that gives
 * the player control of the most cells. In case of ties, chooses uppermost-leftmost
 * position first, then leftmost card.
 */
public class ControlBoardStrategy implements PlayerStrategy {

  /**
   * Chooses a move that maximizes the number of cells owned by the player.
   * Considers all valid moves and picks the one resulting in the most cells owned.
   * Breaks ties by choosing uppermost-leftmost position, then leftmost card.
   *
   * @param model the read-only model to analyze
   * @param player the player to choose a move for
   * @return a Move that maximizes cell control, or null if no valid move exists
   */
  @Override
  public Move chooseMove(ReadOnlySanguineModel model, Player player) {
    List<Card> hand = model.getCurrentPlayerHand();
    int[] dims = model.getGameBoardDimensions();
    int rows = dims[0];
    int cols = dims[1];

    Move bestMove = null;
    int maxCellsControlled = -1;

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        for (int cardIndex = 0; cardIndex < hand.size(); cardIndex++) {
          if (model.isLegalMove(cardIndex, row, col)) {
            int cellsControlled = estimateCellsControlled(model, cardIndex,
                row, col, player);

            if (cellsControlled > maxCellsControlled) {
              maxCellsControlled = cellsControlled;
              bestMove = new Move(cardIndex, row, col);
            } else if (cellsControlled == maxCellsControlled && bestMove != null) {
              if (isPreferredMove(row, col, cardIndex, bestMove)) {
                bestMove = new Move(cardIndex, row, col);
              }
            }
          }
        }
      }
    }

    return bestMove;
  }

  /**
   * Determines if the new move is preferred over the current best move in case of a tie.
   * Prefers uppermost-leftmost position first, then leftmost card.
   *
   * @param newRow the row of the new move
   * @param newCol the column of the new move
   * @param newCardIndex the card index of the new move
   * @param currentBest the current best move
   * @return true if the new move should replace the current best
   */
  private boolean isPreferredMove(int newRow, int newCol, int newCardIndex, Move currentBest) {
    if (newRow < currentBest.getRow()) {
      return true;
    } else if (newRow == currentBest.getRow()) {
      if (newCol < currentBest.getColumn()) {
        return true;
      } else if (newCol == currentBest.getColumn()) {
        return newCardIndex < currentBest.getCardIndex();
      }
    }
    return false;
  }

  /**
   * Estimates the number of cells the player would control after making this move.
   * This is a heuristic that counts cells with player's cards and pawns,
   * including those that would be influenced by the placed card.
   *
   * @param model the read-only model
   * @param cardIndex the card being considered
   * @param moveRow the row of the potential move
   * @param moveCol the column of the potential move
   * @param player the player making the move
   * @return estimated number of cells controlled
   */
  private int estimateCellsControlled(ReadOnlySanguineModel model, int cardIndex,
                                      int moveRow, int moveCol, Player player) {
    int[] dims = model.getGameBoardDimensions();
    int rows = dims[0];
    int cols = dims[1];
    List<Card> hand = model.getCurrentPlayerHand();
    Card card = hand.get(cardIndex);

    int cellsControlled = 0;

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        Cell cell = model.getCellAt(row, col);

        if (row == moveRow && col == moveCol) {
          cellsControlled++;
        } else if (wouldInfluenceCell(card, moveRow, moveCol, row, col, player)) {
          if (!cell.hasPawns() && cell.getCard() == null) {
            cellsControlled++;
          } else if (cell.hasPawns() && cell.getPawnOwner() != player && cell.getCard() == null) {
            cellsControlled++;
          } else if (cell.hasPawns() && cell.getPawnOwner() == player && cell.getCard() == null) {
            cellsControlled++;
          }
        }
      }
    }
    return cellsControlled;
  }

  /**
   * Checks if placing a card would influence a specific cell.
   *
   * @param card the card being placed
   * @param cardRow the row where card is placed
   * @param cardCol the column where card is placed
   * @param targetRow the row of the cell to check
   * @param targetCol the column of the cell to check
   * @param player the player placing the card
   * @return true if the card would influence the target cell
   */
  private boolean wouldInfluenceCell(Card card, int cardRow, int cardCol,
                                     int targetRow, int targetCol, Player player) {
    int influenceRow = targetRow - cardRow + 2;
    int influenceCol = targetCol - cardCol + 2;

    if (influenceRow < 0 || influenceRow >= 5 || influenceCol < 0 || influenceCol >= 5) {
      return false;
    }

    return card.hasInfluenceAt(influenceRow, influenceCol, player == Player.BLUE);
  }
}