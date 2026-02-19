package sanguine.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import javax.swing.JPanel;
import sanguine.controller.Features;
import sanguine.model.Cell;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;


/**
 * Panel for displaying the Sanguine game board with cells, pawns, and cards.
 */
public class BoardPanel extends JPanel implements SanguinePanel {
  private static final int ROW_SCORE_WIDTH = 50;
  private static final int PAWN_DIAMETER = 20;
  private static final int PAWN_SPACING = 20;
  private static final Color EMPTY_CELL_COLOR = Color.GRAY;
  private static final Color RED_CARD_COLOR = Color.PINK;
  private static final Color BLUE_CARD_COLOR = Color.CYAN;
  private static final Color RED_PAWN_COLOR = Color.PINK;
  private static final Color BLUE_PAWN_COLOR = Color.CYAN;
  private static final Color HIGHLIGHT_COLOR = Color.ORANGE;
  private static final Color BORDER_COLOR = Color.BLACK;
  private final ReadOnlySanguineModel model;
  private Features features;
  private int selectedRow;
  private int selectedCol;

  /**
   * Constructs a board panel for displaying the game board.
   *
   * @param model the read-only model to display
   * @throws NullPointerException if model is null
   */
  public BoardPanel(ReadOnlySanguineModel model) {
    this.model = Objects.requireNonNull(model);
    this.selectedRow = -1;
    this.selectedCol = -1;
    this.setBackground(Color.LIGHT_GRAY);

    MouseEventsListener listener = new MouseEventsListener();
    this.addMouseListener(listener);
  }

  /**
   * Adds a features listener to handle user interactions.
   *
   * @param features the controller to handle events
   * @throws NullPointerException if features is null
   */
  public void addFeaturesListener(Features features) {
    this.features = Objects.requireNonNull(features);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g.create();

    int[] dims = model.getGameBoardDimensions();
    int rows = dims[0];
    int cols = dims[1];

    for (int cellRow = 0; cellRow < rows; cellRow++) {
      drawRowScore(g2d, cellRow, true);

      for (int cellCol = 0; cellCol < cols; cellCol++) {
        int dimensionOfx = ROW_SCORE_WIDTH + cellCol * getCellSize();
        int dimensionOfy = cellRow * getCellSize();

        Cell cell = model.getCellAt(cellRow, cellCol);

        drawCell(g2d, cell, dimensionOfx, dimensionOfy);
        drawCellBorder(g2d, dimensionOfx, dimensionOfy);

        if (isCellSelected(cellRow, cellCol)) {
          drawHighlight(g2d, dimensionOfx, dimensionOfy);
        }
      }

      drawRowScore(g2d, cellRow, false);
    }

    g2d.dispose();
  }

  /**
   * Draws the row score for a given row on either the left or right side.
   *
   * @param g2d        the graphics context
   * @param row        the row index
   * @param isLeftSide true for left side, false for right side
   */
  private void drawRowScore(Graphics2D g2d, int row, boolean isLeftSide) {
    int[] rowScore = model.getRowScore(row);
    int score = isLeftSide ? rowScore[0] : rowScore[1];

    int[] dims = model.getGameBoardDimensions();
    int x = isLeftSide ? 10 : (ROW_SCORE_WIDTH + dims[1] * getCellSize() + 10);
    int y = row * getCellSize() + getCellSize() / 2;

    g2d.setFont(getScaledFont());
    g2d.setColor(BORDER_COLOR);
    g2d.drawString(String.valueOf(score), x, y);
  }

  /**
   * Checks if a cell at the given position is currently selected.
   *
   * @param row the row index
   * @param col the column index
   * @return true if the cell is selected, false otherwise
   */
  private boolean isCellSelected(int row, int col) {
    return row == selectedRow && col == selectedCol;
  }

  /**
   * Draws a highlight background for a selected cell.
   *
   * @param g2d the graphics context
   * @param x   the x-coordinate
   * @param y   the y-coordinate
   */
  private void drawHighlight(Graphics2D g2d, int x, int y) {
    g2d.setColor(HIGHLIGHT_COLOR);
    BasicStroke highlightStroke = new BasicStroke(4);
    g2d.setStroke(highlightStroke);
    g2d.drawRect(x, y, getCellSize(), getCellSize());
    g2d.setStroke(new BasicStroke(1));
  }

  /**
   * Draws an individual cell based on its contents.
   *
   * @param g2d  the graphics context
   * @param cell the cell to draw
   * @param x    the x-coordinate
   * @param y    the y-coordinate
   */
  private void drawCell(Graphics2D g2d, Cell cell, int x, int y) {
    if (cell.getCard() != null) {
      drawCardCell(g2d, cell, x, y);
    } else if (cell.hasPawns()) {
      drawPawnCell(g2d, cell, x, y);
    } else {
      drawEmptyCell(g2d, x, y);
    }
  }

  /**
   * Draws a cell containing a card.
   *
   * @param g2d  the graphics context
   * @param cell the cell with a card
   * @param x    the x-coordinate
   * @param y    the y-coordinate
   */
  private void drawCardCell(Graphics2D g2d, Cell cell, int x, int y) {
    Color cardColor = cell.getCardOwner() == Player.RED
        ? RED_CARD_COLOR : BLUE_CARD_COLOR;
    g2d.setColor(cardColor);
    g2d.fillRect(x, y, getCellSize(), getCellSize());

    g2d.setFont(getScaledFont());
    g2d.setColor(BORDER_COLOR);
    String value = String.valueOf(cell.getCard().getValue());
    int textX = x + getCellSize() / 2 - 5;
    int textY = y + getCellSize() / 2;
    g2d.drawString(value, textX, textY);
  }

  /**
   * Draws a cell containing pawns.
   *
   * @param g2d  the graphics context
   * @param cell the cell with pawns
   * @param x    the x-coordinate
   * @param y    the y-coordinate
   */
  private void drawPawnCell(Graphics2D g2d, Cell cell, int x, int y) {
    g2d.setColor(EMPTY_CELL_COLOR);
    g2d.fillRect(x, y, getCellSize(), getCellSize());

    Color pawnColor = cell.getPawnOwner() == Player.RED
        ? RED_PAWN_COLOR : BLUE_PAWN_COLOR;

    int pawnCount = cell.getPawnCount();
    for (int i = 0; i < pawnCount; i++) {
      drawPawn(g2d, pawnColor, x, y, i);
    }
  }

  /**
   * Draws a single pawn at the specified position.
   *
   * @param g2d       the graphics context
   * @param pawnColor the color of the pawn
   * @param cellX     the cell x-coordinate
   * @param cellY     the cell y-coordinate
   * @param pawnIndex the index of the pawn within the cell
   */
  private void drawPawn(Graphics2D g2d, Color pawnColor, int cellX, int cellY, int pawnIndex) {
    g2d.setColor(pawnColor);
    int pawnX = cellX + 20 + (pawnIndex * PAWN_SPACING);
    int pawnY = cellY + getCellSize() / 2 - PAWN_DIAMETER / 2;
    g2d.fillOval(pawnX, pawnY, PAWN_DIAMETER, PAWN_DIAMETER);
  }

  /**
   * Draws an empty cell.
   *
   * @param g2d the graphics context
   * @param x   the x-coordinate
   * @param y   the y-coordinate
   */
  private void drawEmptyCell(Graphics2D g2d, int x, int y) {
    g2d.setColor(EMPTY_CELL_COLOR);
    g2d.fillRect(x, y, getCellSize(), getCellSize());
  }

  /**
   * Draws the border around a cell.
   *
   * @param g2d the graphics context
   * @param x   the x-coordinate
   * @param y   the y-coordinate
   */
  private void drawCellBorder(Graphics2D g2d, int x, int y) {
    g2d.setColor(BORDER_COLOR);
    g2d.drawRect(x, y, getCellSize(), getCellSize());
  }

  /**
   * Converts physical mouse coordinates to logical grid coordinates.
   *
   * @param mouseX the physical x-coordinate
   * @param mouseY the physical y-coordinate
   * @return an array containing [row, col] or null if out of bounds
   */
  private int[] physicalToLogicalCoordinates(int mouseX, int mouseY) {
    int col = (mouseX - ROW_SCORE_WIDTH) / getCellSize();
    int row = mouseY / getCellSize();

    int[] dims = model.getGameBoardDimensions();
    int rows = dims[0];
    int cols = dims[1];

    if (row >= 0 && row < rows && col >= 0 && col < cols) {
      return new int[] {row, col};
    }
    return null;
  }

  /**
   * Handles a cell click by toggling selection and notifying the controller.
   *
   * @param row the row of the clicked cell
   * @param col the column of the clicked cell
   */
  private void handleCellClick(int row, int col) {
    if (isCellSelected(row, col)) {
      selectedRow = -1;
      selectedCol = -1;
    } else {
      selectedRow = row;
      selectedCol = col;
      if (features != null) {
        features.handleCellClick(row, col);
      }
    }
    repaint();
  }

  /**
   * Calculates appropriate font size based on cell size.
   *
   * @return a Font object with scaled size
   */
  private Font getScaledFont() {
    int fontSize = Math.max(12, getCellSize() / 8);
    return new Font("SansSerif", Font.PLAIN, fontSize);
  }

  /**
   * Calculates cell size based on current panel dimensions.
   *
   * @return the dynamic cell size
   */
  private int getCellSize() {
    if (getWidth() <= 0 || getHeight() <= 0) {
      return 100;
    }

    int[] dims = model.getGameBoardDimensions();
    int availableHeight = getHeight();
    int availableWidth = getWidth() - (2 * ROW_SCORE_WIDTH);

    int cellHeight = availableHeight / dims[0];
    int cellWidth = availableWidth / dims[1];

    return Math.min(cellHeight, cellWidth);
  }

  @Override
  public Dimension getPreferredSize() {
    int[] dims = model.getGameBoardDimensions();
    int cellSize = 100; // Use fixed size for initial layout
    int width = ROW_SCORE_WIDTH * 2 + dims[1] * cellSize + 20;
    int height = dims[0] * cellSize;
    return new Dimension(width, height);
  }

  /**
   * Mouse event listener for handling clicks on the board.
   */
  private class MouseEventsListener extends MouseAdapter {

    @Override
    public void mousePressed(MouseEvent e) {
      int[] coords = physicalToLogicalCoordinates(e.getX(), e.getY());
      if (coords != null) {
        handleCellClick(coords[0], coords[1]);
      }
    }
  }
}
