package sanguine.strategy;

import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;


/**
 * A strategy that uses a primary strategy and a tiebreaker strategy.
 * The primary strategy identifies all equally-good moves, and the tiebreaker
 * chooses among them. This allows sophisticated combinations like
 * "Maximize Row Score, break ties with Control Board".
 */
public class TieBreakingStrategy implements PlayerStrategy {
  private final PlayerStrategy primaryStrategy;
  private final PlayerStrategy tieBreakerStrategy;

  /**
   * Constructs a tie-breaking strategy.
   *
   * @param primaryStrategy the main strategy to use
   * @param tieBreakerStrategy the strategy to break ties with
   */
  public TieBreakingStrategy(PlayerStrategy primaryStrategy,
                             PlayerStrategy tieBreakerStrategy) {
    this.primaryStrategy = primaryStrategy;
    this.tieBreakerStrategy = tieBreakerStrategy;
  }

  /**
   * Chooses a move using the primary strategy, falling back to the tiebreaker
   * if the primary strategy returns null.
   *
   * @param model the read-only model to analyze
   * @param player the player to choose a move for
   * @return a Move chosen by primary or tiebreaker strategy, or null if both fail
   */
  @Override
  public Move chooseMove(ReadOnlySanguineModel model, Player player) {
    Move primaryMove = primaryStrategy.chooseMove(model, player);

    if (primaryMove != null) {
      return primaryMove;
    }

    return tieBreakerStrategy.chooseMove(model, player);
  }
}
