package sanguine.view;

import java.util.ArrayList;
import java.util.List;
import sanguine.controller.Features;

/**
 * Represents the mock view for testing the controller without using the GUI.
 */
public class MockSanguineView implements SanguineGuiView {

  private final List<Features> listeners;
  private final List<String> messages;
  private final List<String> titles;

  private int highlightedCard = -1;
  private int highlightedRow = -1;
  private int highlightedCol = -1;
  private int refreshCount = 0;
  private boolean visible = false;

  /**
   * Represents creating the mock sanguine view.
   */
  public MockSanguineView() {
    this.listeners = new ArrayList<>();
    this.messages = new ArrayList<>();
    this.titles = new ArrayList<>();
  }

  @Override
  public void addFeaturesListener(Features features) {
    listeners.add(features);
  }

  @Override
  public void refresh() {
    refreshCount++;
  }

  @Override
  public void showMessage(String message) {
    messages.add(message);
  }

  @Override
  public void highlightCard(int cardIndex) {
    highlightedCard = cardIndex;
  }

  @Override
  public void highlightCell(int row, int col) {
    highlightedRow = row;
    highlightedCol = col;
  }

  @Override
  public void clearHighlights() {
    highlightedCard = -1;
    highlightedRow = -1;
    highlightedCol = -1;
  }

  @Override
  public void setTitle(String title) {
    titles.add(title);
  }

  /**
   * Represents simulating when a player clicks a card.
   */
  public void simulateCardClick(int cardIndex) {
    for (Features listener : listeners) {
      listener.handleCardClick(cardIndex);
    }
  }

  /**
   * Represents simulating when a player clicks a cell.
   */
  public void simulateCellClick(int row, int col) {
    for (Features listener : listeners) {
      listener.handleCellClick(row, col);
    }
  }

  /**
   * Represents simulating when a player locks in their move.
   */
  public void simulateConfirm() {
    for (Features listener : listeners) {
      listener.handleConfirm();
    }
  }

  /**
   * Represents simulating when a player passes during their turn.
   */
  public void simulatePass() {
    for (Features listener : listeners) {
      listener.handlePass();
    }
  }

  public List<String> getMessages() {
    return new ArrayList<>(messages);
  }

  public String getLastMessage() {
    return messages.isEmpty() ? null : messages.get(messages.size() - 1);
  }

  public String getLastTitle() {
    return titles.isEmpty() ? null : titles.get(titles.size() - 1);
  }

  public int getHighlightedCard() {
    return highlightedCard;
  }

  public int getHighlightedRow() {
    return highlightedRow;
  }

  public int getHighlightedCol() {
    return highlightedCol;
  }

  public int getRefreshCount() {
    return refreshCount;
  }

  public boolean isVisible() {
    return visible;
  }

  @Override
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Represents clearing messages, titles, and all highlights.
   */
  public void clearTracking() {
    messages.clear();
    titles.clear();
    refreshCount = 0;
    clearHighlights();
  }
}
