package sanguine.model;

import java.util.ArrayList;
import java.util.List;
import sanguine.controller.ModelFeatures;

/**
 * Represents a basic implementation of the Sanguine game model.
 * This class manages the game state including the board, players' decks and hands,
 * turn order, and game rules for placing cards and applying influence.
 */
public class BasicSanguine implements SanguineModel {

  private GameCell[][] gameBoard;
  private int rows;
  private int columns;
  private List<Card> redDeck;
  private List<Card> blueDeck;
  private List<Card> redHand;
  private List<Card> blueHand;
  private List<ModelFeatures> modelListeners;
  private Player currentPlayer;
  private boolean gameStarted;
  private boolean gameOver;
  private boolean redPassed;
  private boolean bluePassed;
  private boolean drewCardThisTurn;

  /**
   * Represents creating a new game of Sanguine.
   */
  public BasicSanguine() {
    this.gameStarted = false;
    this.gameOver = false;
    this.modelListeners = new ArrayList<>();
  }


  /**
   * Represents starting a game of Sanguine with two players and their decks.
   *
   * @param redDeck  represents the deck of the red player, who starts on the left side
   * @param blueDeck represents the deck of the blue player, who starts on the blue side
   * @param rows     represents the number of rows in the game board
   * @param columns  represents the number of columns in the game board
   * @param handSize represents the size of a hand each player is initially given
   * @throws IllegalStateException    if the game has already started
   * @throws IllegalArgumentException if the parameters are invalid
   */
  @Override
  public void startGame(List<Card> redDeck, List<Card> blueDeck, int rows, int columns,
                        int handSize) {
    if (this.gameStarted) {
      throw new IllegalStateException("Game has already started");
    }
    if (rows <= 0) {
      throw new IllegalArgumentException("Rows must be greater than 0");
    }
    if (columns <= 1 || columns % 2 == 0) {
      throw new IllegalArgumentException("Columns must be greater than 1 and odd");
    }
    if (handSize > redDeck.size() / 3 || handSize > blueDeck.size() / 3) {
      throw new IllegalArgumentException("Hand size too large");
    }

    this.rows = rows;
    this.columns = columns;
    this.redDeck = new ArrayList<>(redDeck);
    this.blueDeck = new ArrayList<>(blueDeck);
    this.redHand = new ArrayList<>();
    this.blueHand = new ArrayList<>();

    initializeBoard();
    dealInitialHands(handSize);

    this.currentPlayer = Player.RED;
    this.gameStarted = true;
    this.redPassed = false;
    this.bluePassed = false;
    this.drewCardThisTurn = false;

    notifyTurnChanged();
  }

  /**
   * Represents the game board which is initially empty and has only initial pawn placement.
   * The First column gets red pawns and the last column gets blue pawns.
   */
  private void initializeBoard() {
    gameBoard = new GameCell[rows][columns];
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        gameBoard[row][column] = new GameCell();
        if (column == 0) {
          gameBoard[row][column].addPawn(Player.RED);
        } else if (column == columns - 1) {
          gameBoard[row][column].addPawn(Player.BLUE);
        }
      }
    }
  }

  /**
   * Represents dealing the initial hand to players in the game from their decks.
   *
   * @param handSize represents the number of cards being dealt to each player
   */
  private void dealInitialHands(int handSize) {
    for (int i = 0; i < handSize; i++) {
      this.redHand.add(redDeck.removeFirst());
      this.blueHand.add(blueDeck.removeFirst());
    }
  }

  /**
   * Represents getting the hand of the current player.
   *
   * @return the hand of the current player in list format
   */
  private List<Card> getCurrentHand() {
    if (currentPlayer == Player.RED) {
      return redHand;
    } else {
      return blueHand;
    }
  }

  /**
   * Represents checking if the game is being played and has not concluded.
   *
   * @throws IllegalStateException if the game has not started or has been concluded
   */
  private void checkGameState() {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    if (gameOver) {
      throw new IllegalStateException("Game over");
    }
  }

  /**
   * Validates if a move is legal and throws descriptive exception if not.
   * The coordinate system uses row-first ordering: (row, column).
   *
   * @param handIndex the card index in the current player's hand
   * @param row       the zero-indexed row of the board (vertical position)
   * @param column    the zero-indexed column of the board (horizontal position)
   * @throws IllegalArgumentException with descriptive message if move is invalid
   */
  private void validateMove(int handIndex, int row, int column) {
    List<Card> hand = getCurrentHand();

    if (handIndex < 0 || handIndex >= hand.size()) {
      throw new IllegalArgumentException("The current hand index is not valid.");
    }

    if (row < 0 || row >= rows || column >= columns || column < 0) {
      throw new IllegalArgumentException("The coordinates of the game board are not valid.");
    }

    GameCell cell = gameBoard[row][column];

    if (!cell.hasPawns()) {
      throw new IllegalArgumentException("The cell has no pawns in it.");
    }

    if (cell.getPawnOwner() != currentPlayer) {
      throw new IllegalArgumentException("The pawns do not belong to the current player.");
    }

    Card card = hand.get(handIndex);
    if (cell.getPawnCount() < card.getCost()) {
      throw new IllegalArgumentException(
          "There are not a sufficient amount of pawns to play the card.");
    }
  }

  /**
   * Represents placing a card in a specified cell.
   * The coordinate system uses row-first ordering: (row, column).
   * Rows are zero-indexed from top to bottom.
   * Columns are zero-indexed from left to right.
   *
   * @param currentHandIndex represents the card index of the card in the player hand
   * @param row              represents the zero-indexed row of the board (vertical position)
   * @param column           represents the zero-indexed column of the board (horizontal position)
   * @throws IllegalStateException    if the game has not begun yet or has been concluded
   * @throws IllegalArgumentException if the move initiated by the player is not valid
   */
  @Override
  public void placeCard(int currentHandIndex, int row, int column) {
    checkGameState();

    if (!drewCardThisTurn) {
      drawCard();
      drewCardThisTurn = true;
    }

    validateMove(currentHandIndex, row, column);

    List<Card> hand = getCurrentHand();
    GameCell cell = gameBoard[row][column];
    Card card = hand.get(currentHandIndex);
    Player originalCardOwner = this.currentPlayer;

    cell.placeCard(card, currentPlayer);
    hand.remove(currentHandIndex);

    applyInfluence(card, row, column, originalCardOwner);

    this.redPassed = false;
    this.bluePassed = false;

    currentPlayer = getCurrentPlayer().getOtherPlayer();
    drewCardThisTurn = false;

    notifyTurnChanged();
  }

  /**
   * Applies influence from a placed card to surrounding cells on the board.
   * The coordinate system uses row-first ordering: (row, column).
   *
   * @param card              the card that was placed
   * @param cardRow           the zero-indexed row where the card was placed
   * @param cardColumn        the zero-indexed column where the card was placed
   * @param originalCardOwner the player who placed the card
   */
  private void applyInfluence(Card card, int cardRow, int cardColumn, Player originalCardOwner) {
    boolean isBlue = originalCardOwner == Player.BLUE;

    for (int influenceRow = 0; influenceRow < 5; influenceRow++) {
      for (int influenceColumn = 0; influenceColumn < 5; influenceColumn++) {
        int boardRow = cardRow + influenceRow - 2;
        int boardColumn = cardColumn + influenceColumn - 2;

        if (card.hasInfluenceAt(influenceRow, influenceColumn, isBlue)) {
          if (boardRow >= 0 && boardRow < rows && boardColumn >= 0
              && boardColumn < columns) {
            GameCell target = gameBoard[boardRow][boardColumn];
            applyInfluenceToCell(target, originalCardOwner);
          }
        }
      }
    }
  }

  /**
   * Applies influence to a single cell by adding or converting pawns.
   *
   * @param cell       the cell to apply influence to
   * @param influencer the player applying the influence
   */
  private void applyInfluenceToCell(GameCell cell, Player influencer) {
    if (cell.getCard() != null) {
      return;
    }

    if (!cell.hasPawns()) {
      cell.addPawn(influencer);
    } else {
      if (cell.getPawnOwner() == influencer) {
        cell.addPawn(influencer);
      } else {
        cell.convertPawns(influencer);
      }
    }
  }

  /**
   * Represents drawing a card from the deck of the current player.
   */
  private void drawCard() {
    List<Card> deck;
    List<Card> hand = getCurrentHand();

    if (this.currentPlayer == Player.RED) {
      deck = this.redDeck;
    } else {
      deck = this.blueDeck;
    }

    if (!deck.isEmpty()) {
      hand.add(deck.removeFirst());
    }
  }

  /**
   * Represents a player passing their turn.
   *
   * @throws IllegalStateException if the game has not begun yet or has been concluded
   */
  @Override
  public void passTurn() {
    checkGameState();

    if (!drewCardThisTurn) {
      drawCard();
      drewCardThisTurn = true;
    }

    if (currentPlayer == Player.RED) {
      redPassed = true;
    } else {
      bluePassed = true;
    }

    if (redPassed && bluePassed) {
      gameOver = true;
      notifyGameOver();
      return;
    }

    currentPlayer = currentPlayer.getOtherPlayer();
    drewCardThisTurn = false;
    notifyTurnChanged();
  }

  /**
   * Represents getting the current player.
   *
   * @return the current player
   * @throws IllegalStateException if the game has not begun yet or has been concluded
   */
  @Override
  public Player getCurrentPlayer() {
    checkGameState();
    return this.currentPlayer;
  }

  /**
   * Represents ending the current game.
   *
   * @return true if the game is over, false otherwise
   */
  @Override
  public boolean isGameOver() {
    return gameOver;
  }

  /**
   * Represents getting the winning player of the current game.
   *
   * @return the winning player in the current game of Sanguine
   */
  @Override
  public Player getWinningPlayer() {
    if (!gameOver) {
      return null;
    }
    int[] scores = getFinalScores();
    if (scores[0] > scores[1]) {
      return Player.RED;
    } else if (scores[1] > scores[0]) {
      return Player.BLUE;
    }
    return null;
  }

  /**
   * Represents getting the hand of the current player.
   *
   * @return arraylist of the hand of the current player
   * @throws IllegalStateException if the game has not begun yet or has been concluded
   */
  @Override
  public List<Card> getCurrentPlayerHand() {
    checkGameState();
    return new ArrayList<>(getCurrentHand());
  }

  /**
   * Represents getting the size of the deck depending on the current player.
   *
   * @return the size of the deck of the current player
   * @throws IllegalStateException if the game has not begun yet or has been concluded
   */
  @Override
  public int getCurrentDeckSize() {
    checkGameState();

    if (this.currentPlayer == Player.RED) {
      return redDeck.size();
    } else {
      return blueDeck.size();
    }
  }

  /**
   * Represents getting the game board dimensions.
   *
   * @return the dimensions of the game board as [rows, columns]
   * @throws IllegalStateException if the game has not begun yet
   */
  @Override
  public int[] getGameBoardDimensions() {
    if (!gameStarted) {
      throw new IllegalStateException("The game has not begun.");
    }

    return new int[] {rows, columns};
  }

  /**
   * Represents getting the cell at a specified row and column.
   * The coordinate system uses row-first ordering: (row, column).
   *
   * @param row    represents the zero-indexed row of the game board
   * @param column represents the zero-indexed column of the game board
   * @return the specified cell based on the provided row and column
   * @throws IllegalStateException    if the game has not begun
   * @throws IllegalArgumentException if the cell coordinates are not valid
   */
  @Override
  public Cell getCellAt(int row, int column) {
    if (!gameStarted) {
      throw new IllegalStateException("The game has not begun.");
    }

    if (row < 0 || row >= rows || column < 0 || column >= columns) {
      throw new IllegalArgumentException("The cell coordinates are not valid.");
    }

    return gameBoard[row][column];
  }

  /**
   * Represents getting the final sum of all row scores for each player.
   *
   * @return the final scores after the game has concluded for the red and blue player
   */
  @Override
  public int[] getFinalScores() {
    int redScore = 0;
    int blueScore = 0;

    for (int row = 0; row < rows; row++) {
      int redRow = 0;
      int blueRow = 0;

      for (int column = 0; column < columns; column++) {
        Card card = gameBoard[row][column].getCard();
        if (card != null) {
          if (gameBoard[row][column].getCardOwner() == Player.RED) {
            redRow += card.getValue();
          } else {
            blueRow += card.getValue();
          }
        }
      }

      if (redRow > blueRow) {
        redScore += redRow;
      } else if (blueRow > redRow) {
        blueScore += blueRow;
      }
    }
    return new int[] {redScore, blueScore};
  }

  /**
   * Represents getting the winning player, who is either the red player or blue player.
   *
   * @return the winning player for the current game of Sanguine
   */
  @Override
  public Player getWinner() {
    return getWinningPlayer();
  }

  /**
   * Represents checking if it is legal for the current player to play a card at the given position.
   * The coordinate system uses row-first ordering: (row, column).
   *
   * @param handIndex the index of the card in the current player's hand
   * @param row       the target zero-indexed row on the board
   * @param column    the target zero-indexed column on the board
   * @return true if the move is legal, false otherwise
   */
  @Override
  public boolean isLegalMove(int handIndex, int row, int column) {
    if (!gameStarted || gameOver) {
      return false;
    }

    try {
      validateMove(handIndex, row, column);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Represents getting the score for both players in a specific row.
   * The coordinate system uses row-first ordering.
   *
   * @param row the zero-indexed row of the game board
   * @return array where index 0 is the red player's score and index 1 is the blue player's score
   * @throws IllegalStateException    if the game has not started
   * @throws IllegalArgumentException if the row index is invalid
   */
  @Override
  public int[] getRowScore(int row) {
    if (!gameStarted) {
      throw new IllegalStateException("The game has not begun.");
    }

    if (row < 0 || row >= rows) {
      throw new IllegalArgumentException("The row index is not valid.");
    }

    int redScore = 0;
    int blueScore = 0;

    for (int column = 0; column < columns; column++) {
      Cell cell = gameBoard[row][column];
      if (cell.getCard() != null) {
        Card card = cell.getCard();
        if (cell.getCardOwner() == Player.RED) {
          redScore += card.getValue();
        } else {
          blueScore += card.getValue();
        }
      }
    }

    return new int[] {redScore, blueScore};
  }

  @Override
  public void addModelListener(ModelFeatures listener) {
    this.modelListeners.add(listener);
  }

  private void notifyTurnChanged() {
    Player current = this.currentPlayer;
    for (ModelFeatures listener : modelListeners) {
      listener.onTurnChanged(current);
    }
  }

  private void notifyGameOver() {
    for (ModelFeatures listener : modelListeners) {
      listener.onGameOver();
    }
  }
}
