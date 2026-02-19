package sanguine;

import java.io.File;
import java.util.List;
import sanguine.controller.AiPlayer;
import sanguine.controller.HumanPlayer;
import sanguine.controller.PlayerComponent;
import sanguine.controller.SanguineController;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.SanguineModel;
import sanguine.strategy.ControlBoardStrategy;
import sanguine.strategy.FillFirstStrategy;
import sanguine.strategy.MaxRowScoreStrategy;
import sanguine.view.SanguineGuiView;
import sanguine.view.SanguineGuiViewImpl;

/**
 * Represents the main class for running a game of Sanguine.
 * Supports different board sizes, decks, and human/AI players.
 */
public final class SanguineGame {

  /**
   * Main method to start a Sanguine game with GUI.
   * Command-line arguments:
   * 1. rows - number of rows on the board
   * 2. columns - number of columns on the board
   * 3. redDeckPath - path to Red player's deck file
   * 4. blueDeckPath - path to Blue player's deck file
   * 5. redPlayerType - "human", "strategy1", "strategy2", or "strategy3"
   * 6. bluePlayerType - "human", "strategy1", "strategy2", or "strategy3"
   *
   * @param args represents the command-line arguments being passed
   */
  public static void main(String[] args) {
    if (args.length < 6) {
      System.out.println("Usage: java SanguineGame <rows> <columns> "
          + "<redDeck> <blueDeck> <redPlayer> <bluePlayer>");
      System.out.println("Player types: human, strategy1, strategy2, strategy3");
      System.out.println("  strategy1 = Fill First");
      System.out.println("  strategy2 = Maximize Row Score");
      System.out.println("  strategy3 = Control Board");
      System.out.println("Example: java SanguineGame 3 5 "
          + "docs/example.deck docs/example.deck human strategy1");
      return;
    }

    try {
      String redDeckPath = args[2];
      String blueDeckPath = args[3];

      if (!new File(redDeckPath).exists()) {
        System.out.println("Error: Red deck file not found at " + redDeckPath);
        return;
      }
      if (!new File(blueDeckPath).exists()) {
        System.out.println("Error: Blue deck file not found at " + blueDeckPath);
        return;
      }

      DeckReader reader = new SanguineDeckReader();
      List<Card> redDeck = reader.readDeck(redDeckPath);
      List<Card> blueDeck = reader.readDeck(blueDeckPath);

      int handSize = Math.min(redDeck.size() / 3, blueDeck.size() / 3);

      if (handSize < 1) {
        System.out.println("Error: Decks too small. Need at least 3 cards per deck.");
        return;
      }

      SanguineModel model = new BasicSanguine();

      SanguineGuiView redView = new SanguineGuiViewImpl(model);
      SanguineGuiView blueView = new SanguineGuiViewImpl(model);

      String redPlayerType = args[4].toLowerCase();
      String bluePlayerType = args[5].toLowerCase();

      PlayerComponent redPlayer = createPlayer(redPlayerType);
      PlayerComponent bluePlayer = createPlayer(bluePlayerType);

      SanguineController redController = new SanguineController(
          model, Player.RED, redView, redPlayer);
      SanguineController blueController = new SanguineController(
          model, Player.BLUE, blueView, bluePlayer);

      model.addModelListener(redController);
      model.addModelListener(blueController);

      int rows = Integer.parseInt(args[0]);
      int columns = Integer.parseInt(args[1]);

      model.startGame(redDeck, blueDeck, rows, columns, handSize);

      redView.setVisible(true);
      blueView.setVisible(true);

      System.out.println("Game started successfully!");
      System.out.println("Board: " + rows + " rows x " + columns + " columns");
      System.out.println("Red player: " + redPlayerType);
      System.out.println("Blue player: " + bluePlayerType);
      System.out.println("\nControls:");
      System.out.println("- Click card to select");
      System.out.println("- Click cell to select");
      System.out.println("- Press ENTER to confirm move");
      System.out.println("- Press P to pass turn");

    } catch (NumberFormatException e) {
      System.out.println("Error: Rows and columns must be integers");
    } catch (Exception e) {
      System.out.println("Error starting game: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Represents creating a player component based on the type of player.
   *
   * @param playerType the type of player ("human", "strategy1", "strategy2", "strategy3")
   * @return the appropriate PlayerComponent
   */
  private static PlayerComponent createPlayer(String playerType) {
    switch (playerType) {
      case "human":
        return new HumanPlayer();
      case "strategy1":
        return new AiPlayer(new FillFirstStrategy());
      case "strategy2":
        return new AiPlayer(new MaxRowScoreStrategy());
      case "strategy3":
        return new AiPlayer(new ControlBoardStrategy());
      default:
        System.out.println("Warning: Unknown player type '" + playerType
            + "', defaulting to human");
        return new HumanPlayer();
    }
  }
}