package sanguine.strategy;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;

/**
 * A composite strategy that chains multiple strategies together.
 * When the first strategy returns multiple equally-good moves (or a single move),
 * the next strategy in the chain is used to break ties.
 * This allows for sophisticated combinations like "Fill First, then Control Board".
 */
public class CompositeStrategy implements PlayerStrategy {
  private final List<PlayerStrategy> strategies;

  /**
   * Constructs a composite strategy from a list of strategies.
   * Strategies are applied in order to break ties.
   *
   * @param strategies the list of strategies to chain together
   */
  public CompositeStrategy(List<PlayerStrategy> strategies) {
    if (strategies == null || strategies.isEmpty()) {
      throw new IllegalArgumentException("Must provide at least one strategy");
    }
    this.strategies = new ArrayList<>(strategies);
  }

  /**
   * Constructs a composite strategy from varargs.
   *
   * @param strategies the strategies to chain together
   */
  public CompositeStrategy(PlayerStrategy... strategies) {
    if (strategies == null || strategies.length == 0) {
      throw new IllegalArgumentException("Must provide at least one strategy");
    }
    this.strategies = new ArrayList<>();
    this.strategies.addAll(Arrays.asList(strategies));
  }

  /**
   * Chooses a move by applying strategies in sequence.
   * If the first strategy returns a move, that move is used.
   * If the first strategy returns null, the next strategy is tried.
   *
   * @param model the read-only model to analyze
   * @param player the player to choose a move for
   * @return a Move chosen by the first strategy that returns non-null, or null if all fail
   */
  @Override
  public Move chooseMove(ReadOnlySanguineModel model, Player player) {
    for (PlayerStrategy strategy : strategies) {
      Move move = strategy.chooseMove(model, player);
      if (move != null) {
        return move;
      }
    }
    return null;
  }
}