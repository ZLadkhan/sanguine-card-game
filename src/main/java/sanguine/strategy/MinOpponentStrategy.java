package sanguine.strategy;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;


/**
 * A minimax strategy that chooses moves to minimize the opponent's best response.
 * This strategy evaluates each possible move by assuming the opponent will make
 * their best counter-move, and chooses the move that leaves the opponent with
 * the worst best option. In case of ties, chooses uppermost-leftmost position,
 * then leftmost card.
 */
public class MinOpponentStrategy implements PlayerStrategy {

  /**
   * Constructs a minimax strategy that assumes the opponent uses the given strategy.
   *
   * @param opponentStrategy the strategy assumed to be used by the opponent
   */
  public MinOpponentStrategy(PlayerStrategy opponentStrategy) {
  }

  /**
   * Chooses a move that minimizes the opponent's best response.
   * For each possible move, simulates what the opponent's best move would be,
   * and chooses the move that leaves the opponent with the worst outcome.
   * Breaks ties by choosing uppermost-leftmost position, then leftmost card.
   *
   * @param model  the read-only model to analyze
   * @param player the player to choose a move for
   * @return a Move that minimizes opponent advantage, or null if no valid move exists
   */
  @Override
  public Move chooseMove(ReadOnlySanguineModel model, Player player) {
    List<Card> hand = model.getCurrentPlayerHand();
    int[] dims = model.getGameBoardDimensions();
    int rows = dims[0];
    int cols = dims[1];

    Move bestMove = null;
    int minOpponentAdvantage = Integer.MAX_VALUE;

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        for (int cardIndex = 0; cardIndex < hand.size(); cardIndex++) {
          if (model.isLegalMove(cardIndex, row, col)) {
            int opponentAdvantage = evaluateOpponentResponse(model,
                cardIndex, row, col, player);

            if (opponentAdvantage < minOpponentAdvantage) {
              minOpponentAdvantage = opponentAdvantage;
              bestMove = new Move(cardIndex, row, col);
            }
          }
        }
      }
    }

    return bestMove;
  }

  /**
   * Evaluates how good the opponent's response would be after this move.
   * Uses a heuristic based on the opponent's potential score gain.
   *
   * @param model     the read-only model
   * @param cardIndex the card index for the potential move
   * @param row       the row for the potential move
   * @param col       the column for the potential move
   * @param player    the current player
   * @return a score representing opponent's advantage (lower is better for us)
   */
  private int evaluateOpponentResponse(ReadOnlySanguineModel model, int cardIndex,
                                       int row, int col, Player player) {
    Player opponent = player.getOtherPlayer();
    int[] currentScores = model.getFinalScores();
    int currentOpponentScore = opponent == Player.RED ? currentScores[0] : currentScores[1];

    List<Card> hand = model.getCurrentPlayerHand();
    Card card = hand.get(cardIndex);

    return currentOpponentScore + estimateOpponentGain(model, card, row, col, opponent);
  }

  /**
   * Estimates the potential score gain for the opponent after we make our move.
   *
   * @param model    the read-only model
   * @param ourCard  the card we're considering playing
   * @param ourRow   the row where we'd play
   * @param ourCol   the column where we'd play
   * @param opponent the opponent player
   * @return estimated score the opponent could gain
   */
  private int estimateOpponentGain(ReadOnlySanguineModel model, Card ourCard,
                                   int ourRow, int ourCol, Player opponent) {
    int[] rowScores = model.getRowScore(ourRow);
    int ourNewScore = (opponent == Player.RED ? rowScores[1] : rowScores[0]) + ourCard.getValue();
    int opponentScore = opponent == Player.RED ? rowScores[0] : rowScores[1];

    if (ourNewScore > opponentScore) {
      return 0;
    } else {
      return opponentScore - ourNewScore;
    }
  }
}