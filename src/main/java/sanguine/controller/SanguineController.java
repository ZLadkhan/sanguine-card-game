package sanguine.controller;

import sanguine.model.Player;
import sanguine.model.SanguineModel;
import sanguine.view.SanguineGuiView;

/**
 * Represents the controller for the game of Sanguine.
 * Each controller is responsible for the actions of one player.
 */
public class SanguineController implements Features, ModelFeatures {

  private final SanguineModel model;
  private final Player playerColor;
  private final SanguineGuiView view;
  private final PlayerComponent player;
  private Integer selectedCardIndex;
  private Integer selectedRow;
  private Integer selectedCol;
  private boolean isMyTurn;

  /**
   * Represents creating a new controller for a specific player.
   *
   * @param model       the game model
   * @param playerColor which player this controller represents (RED or BLUE)
   * @param view        the view for this player
   * @param player      the player component (human or AI)
   */
  public SanguineController(SanguineModel model, Player playerColor,
                            SanguineGuiView view, PlayerComponent player) {
    this.model = model;
    this.playerColor = playerColor;
    this.view = view;
    this.player = player;
    this.selectedCardIndex = null;
    this.selectedRow = null;
    this.selectedCol = null;
    this.isMyTurn = false;

    view.addFeaturesListener(this);
    player.addFeaturesListener(this);
  }

  @Override
  public void handleCardClick(int cardIndex) {
    if (!isMyTurn) {
      return;
    }

    try {
      if (cardIndex < 0 || cardIndex >= model.getCurrentPlayerHand().size()) {
        view.showMessage("Invalid card selection");
        return;
      }

      selectedCardIndex = cardIndex;
      view.highlightCard(cardIndex);
    } catch (Exception e) {
      view.showMessage("Error selecting card: " + e.getMessage());
    }
  }

  @Override
  public void handleCellClick(int row, int col) {
    if (!isMyTurn) {
      return;
    }

    selectedRow = row;
    selectedCol = col;
    view.highlightCell(row, col);
  }

  @Override
  public void handleConfirm() {
    if (!isMyTurn) {
      view.showMessage("It's not your turn!");
      return;
    }

    if (selectedCardIndex == null || selectedRow == null || selectedCol == null) {
      view.showMessage("Please select a card and a cell before confirming");
      return;
    }

    try {
      model.placeCard(selectedCardIndex, selectedRow, selectedCol);
      clearSelections();
      view.refresh();
    } catch (IllegalArgumentException | IllegalStateException e) {
      view.showMessage("Invalid move: " + e.getMessage());
    }
  }

  @Override
  public void handlePass() {
    if (!isMyTurn) {
      view.showMessage("It's not your turn!");
      return;
    }

    try {
      clearSelections();
      model.passTurn();
      view.refresh();
    } catch (IllegalStateException e) {
      view.showMessage("Cannot pass: " + e.getMessage());
    }
  }

  @Override
  public void onTurnChanged(Player currentPlayer) {
    isMyTurn = (currentPlayer == playerColor);

    if (isMyTurn) {
      view.setTitle(playerColor + " - Your Turn");
      clearSelections();
      view.refresh();

      player.notifyTurn(model);
    } else {
      view.setTitle(playerColor + " - Waiting...");
      view.refresh();
    }
  }

  @Override
  public void onGameOver() {
    isMyTurn = false;

    Player winner = model.getWinner();
    int[] scores = model.getFinalScores();

    String message;
    if (winner == null) {
      message = String.format("Game Over - Tie!\nRed: %d, Blue: %d",
          scores[0], scores[1]);
    } else if (winner == playerColor) {
      message = String.format("You Win!\nRed: %d, Blue: %d",
          scores[0], scores[1]);
    } else {
      message = String.format("You Lose!\nRed: %d, Blue: %d",
          scores[0], scores[1]);
    }

    view.setTitle(playerColor + " - Game Over");
    view.refresh();
    view.showMessage(message);
  }

  /**
   * Represents clearing all current selections.
   */
  private void clearSelections() {
    selectedCardIndex = null;
    selectedRow = null;
    selectedCol = null;
    view.clearHighlights();
  }
}
