package sanguine.view;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import javax.swing.JPanel;
import sanguine.controller.Features;
import sanguine.model.Card;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;


/**
 * Panel for displaying the current player's hand of cards.
 */
public class HandPanel extends JPanel implements SanguinePanel {
  private static final int DEFAULT_CARD_WIDTH = 150;
  private static final int DEFAULT_CARD_HEIGHT = 250;
  private static final int CARD_SPACING = 10;
  private static final int INFLUENCE_GRID_SIZE = 15;
  private static final int INFLUENCE_GRID_ROWS = 5;
  private static final int INFLUENCE_GRID_COLS = 5;
  private static final int TEXT_OFFSET_X = 10;
  private static final int NAME_OFFSET_Y = 20;
  private static final int COST_OFFSET_Y = 40;
  private static final int VALUE_OFFSET_Y = 60;
  private static final int GRID_START_Y = 80;
  private static final int HIGHLIGHT_BORDER = 5;
  private static final Color RED_BACKGROUND = Color.PINK;
  private static final Color BLUE_BACKGROUND = Color.CYAN;
  private static final Color HIGHLIGHT_COLOR = Color.YELLOW;
  private static final Color BORDER_COLOR = Color.BLACK;
  private static final Color INFLUENCE_COLOR = Color.CYAN;
  private static final Color CENTER_COLOR = Color.ORANGE;
  private static final Color NO_INFLUENCE_COLOR = Color.DARK_GRAY;
  private final ReadOnlySanguineModel model;
  private Features features;
  private int selectedCardIndex;

  /**
   * Constructs a hand panel for displaying the current player's hand.
   *
   * @param model the read-only model to display
   * @throws NullPointerException if model is null
   */
  public HandPanel(ReadOnlySanguineModel model) {
    this.model = Objects.requireNonNull(model);
    this.selectedCardIndex = -1;
    this.setBackground(Color.WHITE);
    this.setFocusable(true);

    MouseEventsListener mouseListener = new MouseEventsListener();
    this.addMouseListener(mouseListener);

    KeyboardListener keyListener = new KeyboardListener();
    this.addKeyListener(keyListener);
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

    Player currentPlayer = model.getCurrentPlayer();
    List<Card> hand = model.getCurrentPlayerHand();

    setBackgroundForPlayer(currentPlayer);

    for (int i = 0; i < hand.size(); i++) {
      int x = calculateCardX(i);
      int y = CARD_SPACING;

      if (isCardSelected(i)) {
        drawCardHighlight(g2d, x, y);
      }

      drawCard(g2d, hand.get(i), x, y, currentPlayer);
    }

    g2d.dispose();
  }

  /**
   * Sets the background color based on the current player.
   *
   * @param player the current player
   */
  private void setBackgroundForPlayer(Player player) {
    Color bgColor = player == Player.RED ? RED_BACKGROUND : BLUE_BACKGROUND;
    this.setBackground(bgColor);
  }

  /**
   * Calculates the x-coordinate for a card at the given index.
   *
   * @param cardIndex the index of the card
   * @return the x-coordinate
   */
  private int calculateCardX(int cardIndex) {
    return CARD_SPACING + cardIndex * (getCardWidth() + CARD_SPACING);
  }

  /**
   * Checks if a card at the given index is currently selected.
   *
   * @param cardIndex the index of the card
   * @return true if the card is selected, false otherwise
   */
  private boolean isCardSelected(int cardIndex) {
    return cardIndex == selectedCardIndex;
  }

  /**
   * Draws a highlight background for a selected card.
   *
   * @param g2d the graphics context
   * @param x   the x-coordinate of the card
   * @param y   the y-coordinate of the card
   */
  private void drawCardHighlight(Graphics2D g2d, int x, int y) {
    g2d.setColor(HIGHLIGHT_COLOR);
    g2d.fillRect(x - HIGHLIGHT_BORDER, y - HIGHLIGHT_BORDER,
        getCardWidth() + 2 * HIGHLIGHT_BORDER, getCardHeight() + 2 * HIGHLIGHT_BORDER);
  }

  /**
   * Draws an individual card with its information and influence grid.
   *
   * @param g2d    the graphics context
   * @param card   the card to draw
   * @param x      the x-coordinate
   * @param y      the y-coordinate
   * @param player the player who owns the card
   */
  private void drawCard(Graphics2D g2d, Card card, int x, int y, Player player) {
    drawCardBackground(g2d, x, y, player);
    drawCardBorder(g2d, x, y);
    drawCardInfo(g2d, card, x, y);
    drawCardInfluenceGrid(g2d, card, x, y, player);
  }

  /**
   * Draws the background color of a card based on player ownership.
   *
   * @param g2d    the graphics context
   * @param x      the x-coordinate
   * @param y      the y-coordinate
   * @param player the player who owns the card
   */
  private void drawCardBackground(Graphics2D g2d, int x, int y, Player player) {
    Color cardColor = player == Player.RED ? RED_BACKGROUND : BLUE_BACKGROUND;
    g2d.setColor(cardColor);
    g2d.fillRect(x, y, getCardWidth(), getCardHeight());
  }

  /**
   * Draws the border around a card.
   *
   * @param g2d the graphics context
   * @param x   the x-coordinate
   * @param y   the y-coordinate
   */
  private void drawCardBorder(Graphics2D g2d, int x, int y) {
    g2d.setColor(BORDER_COLOR);
    g2d.drawRect(x, y, getCardWidth(), getCardHeight());
  }

  /**
   * Draws the text information for a card (name, cost, value).
   *
   * @param g2d  the graphics context
   * @param card the card whose information to draw
   * @param x    the x-coordinate of the card
   * @param y    the y-coordinate of the card
   */
  private void drawCardInfo(Graphics2D g2d, Card card, int x, int y) {
    g2d.setFont(getScaledFont());
    g2d.setColor(BORDER_COLOR);
    g2d.drawString(card.getName(), x + TEXT_OFFSET_X, y + NAME_OFFSET_Y);
    g2d.drawString("Cost: " + card.getCost(), x + TEXT_OFFSET_X, y + COST_OFFSET_Y);
    g2d.drawString("Value: " + card.getValue(), x + TEXT_OFFSET_X, y + VALUE_OFFSET_Y);
  }

  /**
   * Draws the influence grid for a card.
   *
   * @param g2d    the graphics context
   * @param card   the card whose influence to draw
   * @param cardX  the x-coordinate of the card
   * @param cardY  the y-coordinate of the card
   * @param player the player who owns the card
   */
  private void drawCardInfluenceGrid(Graphics2D g2d, Card card, int cardX, int cardY,
                                     Player player) {
    int gridStartX = cardX + (getCardWidth() - INFLUENCE_GRID_COLS * INFLUENCE_GRID_SIZE) / 2;
    int gridStartY = cardY + GRID_START_Y;
    boolean isBluePlayer = player == Player.BLUE;

    for (int row = 0; row < INFLUENCE_GRID_ROWS; row++) {
      for (int col = 0; col < INFLUENCE_GRID_COLS; col++) {
        drawInfluenceCell(g2d, card, row, col, gridStartX, gridStartY, isBluePlayer);
      }
    }
  }

  /**
   * Draws a single cell in the influence grid.
   *
   * @param g2d          the graphics context
   * @param card         the card whose influence to check
   * @param row          the row in the influence grid
   * @param col          the column in the influence grid
   * @param gridStartX   the starting x-coordinate of the grid
   * @param gridStartY   the starting y-coordinate of the grid
   * @param isBluePlayer whether the card belongs to the blue player
   */
  private void drawInfluenceCell(Graphics2D g2d, Card card, int row, int col,
                                 int gridStartX, int gridStartY, boolean isBluePlayer) {
    int x = gridStartX + col * INFLUENCE_GRID_SIZE;
    int y = gridStartY + row * INFLUENCE_GRID_SIZE;

    Color cellColor = determineInfluenceCellColor(card, row, col, isBluePlayer);
    g2d.setColor(cellColor);
    g2d.fillRect(x, y, INFLUENCE_GRID_SIZE, INFLUENCE_GRID_SIZE);

    g2d.setColor(BORDER_COLOR);
    g2d.drawRect(x, y, INFLUENCE_GRID_SIZE, INFLUENCE_GRID_SIZE);
  }

  /**
   * Determines the color for an influence grid cell.
   *
   * @param card         the card to check
   * @param row          the row in the influence grid
   * @param col          the column in the influence grid
   * @param isBluePlayer whether the card belongs to the blue player
   * @return the color for the cell
   */
  private Color determineInfluenceCellColor(Card card, int row, int col, boolean isBluePlayer) {
    if (row == 2 && col == 2) {
      return CENTER_COLOR;
    } else if (card.hasInfluenceAt(row, col, isBluePlayer)) {
      return INFLUENCE_COLOR;
    } else {
      return NO_INFLUENCE_COLOR;
    }
  }

  /**
   * Converts physical mouse coordinates to a card index.
   *
   * @param mouseX the physical x-coordinate
   * @param mouseY the physical y-coordinate
   * @return the card index or -1 if no card was clicked
   */
  private int physicalToCardIndex(int mouseX, int mouseY) {
    List<Card> hand = model.getCurrentPlayerHand();

    for (int i = 0; i < hand.size(); i++) {
      int cardX = calculateCardX(i);
      int cardY = CARD_SPACING;

      if (isPointInCard(mouseX, mouseY, cardX, cardY)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Checks if a point is within the bounds of a card.
   *
   * @param pointX the x-coordinate of the point
   * @param pointY the y-coordinate of the point
   * @param cardX  the x-coordinate of the card
   * @param cardY  the y-coordinate of the card
   * @return true if the point is within the card bounds
   */
  private boolean isPointInCard(int pointX, int pointY, int cardX, int cardY) {
    return pointX >= cardX && pointX <= cardX + getCardWidth()
        && pointY >= cardY && pointY <= cardY + getCardHeight();
  }

  /**
   * Handles a card click by toggling selection and notifying the controller.
   *
   * @param cardIndex the index of the clicked card
   */
  private void handleCardClick(int cardIndex) {
    if (isCardSelected(cardIndex)) {
      selectedCardIndex = -1;
    } else {
      selectedCardIndex = cardIndex;
      if (features != null) {
        features.handleCardClick(cardIndex);
      }
    }
    repaint();
  }

  @Override
  public Dimension getPreferredSize() {
    List<Card> hand = model.getCurrentPlayerHand();
    int width = CARD_SPACING + hand.size() * (DEFAULT_CARD_WIDTH + CARD_SPACING);
    int height = DEFAULT_CARD_HEIGHT + 2 * CARD_SPACING;
    return new Dimension(width, height);
  }

  /**
   * Calculates appropriate font size based on card dimensions.
   *
   * @return a Font object with scaled size
   */
  private Font getScaledFont() {
    int fontSize = Math.max(10, getCardWidth() / 12);
    return new Font("SansSerif", Font.PLAIN, fontSize);
  }

  /**
   * Calculates card width based on panel dimensions.
   *
   * @return the dynamic card width
   */
  private int getCardWidth() {
    if (getWidth() <= 0) {
      return DEFAULT_CARD_WIDTH;
    }

    List<Card> hand = model.getCurrentPlayerHand();
    if (hand.isEmpty()) {
      return DEFAULT_CARD_WIDTH;
    }

    int availableWidth = getWidth() - (CARD_SPACING * (hand.size() + 1));
    int cardWidth = availableWidth / hand.size();
    return Math.max(80, Math.min(cardWidth, 200));
  }

  /**
   * Calculates card height based on card width.
   *
   * @return the dynamic card height
   */
  private int getCardHeight() {
    if (getWidth() <= 0) {
      return DEFAULT_CARD_HEIGHT;
    }
    return (int) (getCardWidth() * 1.67);
  }

  /**
   * Mouse event listener for handling clicks on cards.
   */
  private class MouseEventsListener extends MouseAdapter {

    @Override
    public void mousePressed(MouseEvent e) {
      int cardIndex = physicalToCardIndex(e.getX(), e.getY());
      if (cardIndex != -1) {
        handleCardClick(cardIndex);
      }
    }
  }

  /**
   * Keyboard event listener for handling confirm and pass actions.
   */
  private class KeyboardListener extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {
      if (features == null) {
        return;
      }

      if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
        features.handleConfirm();
      } else if (e.getKeyCode() == KeyEvent.VK_P) {
        features.handlePass();
      }
    }
  }
}
