package sanguine.view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import sanguine.controller.Features;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Implementation of the GUI view for Sanguine using Java Swing.
 */
public class SanguineGuiViewImpl extends JFrame implements SanguineGuiView {
  private final ReadOnlySanguineModel model;
  private final BoardPanel boardPanel;
  private final HandPanel handPanel;

  /**
   * Constructs a GUI view for the Sanguine game.
   *
   * @param model the read-only model to display
   */
  public SanguineGuiViewImpl(ReadOnlySanguineModel model) {
    super("Sanguine Game");

    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }

    this.model = model;

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLayout(new BorderLayout());

    this.boardPanel = new BoardPanel(model);
    this.add(boardPanel, BorderLayout.CENTER);

    this.handPanel = new HandPanel(model);
    this.add(handPanel, BorderLayout.SOUTH);
  }

  @Override
  public void setTitle(String title) {
    super.setTitle(title);
  }

  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
  }

  @Override
  public void refresh() {
    this.boardPanel.repaint();
    this.handPanel.repaint();
  }

  @Override
  public void addFeaturesListener(Features features) {
    this.boardPanel.addFeaturesListener(features);
    this.handPanel.addFeaturesListener(features);
  }

  @Override
  public void showMessage(String message) {
    JOptionPane.showMessageDialog(this, message, "Sanguine",
        JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void highlightCard(int cardIndex) {
    handPanel.requestFocus();
    handPanel.repaint();
  }

  @Override
  public void highlightCell(int row, int col) {
    boardPanel.repaint();
  }

  @Override
  public void clearHighlights() {
    boardPanel.repaint();
    handPanel.repaint();
  }
}
