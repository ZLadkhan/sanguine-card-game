package sanguine.view;

import java.io.IOException;
import sanguine.model.Cell;
import sanguine.model.Player;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Represents the textual view for rendering a game of Sanguine.
 * This view displays the game board as text with row scores and cell contents.
 */
public class SanguineTextualView implements GameTextualView {

  private final ReadOnlySanguineModel model;

  /**
   * Constructs a textual view for Sanguine using the ReadOnly model.
   *
   * @param model represents the ReadOnly model being passed into the textual view
   * @throws IllegalArgumentException if the model is null
   */
  public SanguineTextualView(ReadOnlySanguineModel model) {
    if (model == null) {
      throw new IllegalArgumentException("The Sanguine model cannot be null!");
    }

    this.model = model;
  }

  /**
   * Renders the Sanguine game board in a string format.
   * Each row is rendered with the red player score against the blue player score.
   * Rows are separated using new lines for readability purposes.
   *
   * @return the string representation of the Sanguine game board
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    int[] dims = model.getGameBoardDimensions();

    for (int row = 0; row < dims[0]; row++) {
      int[] rowScores = model.getRowScore(row);
      sb.append(rowScores[0]).append(" ");

      for (int col = 0; col < dims[1]; col++) {
        Cell cell = model.getCellAt(row, col);
        sb.append(renderCell(cell));
      }

      sb.append(" ").append(rowScores[1]);

      if (row < dims[0] - 1) {
        sb.append("\n");
      }
    }

    return sb.toString();
  }

  /**
   * Renders the game board to a specified output area.
   *
   * @param output represents where to write the rendering to
   * @throws IOException if there is an input or output error when writing
   */
  @Override
  public void render(Appendable output) throws IOException {
    output.append(this.toString());
  }

  /**
   * Renders a single cell as a string character.
   *
   * @param cell the cell to render
   * @return a string representation of the cell
   */
  private String renderCell(Cell cell) {
    if (cell.getCard() != null) {
      if (cell.getCardOwner() == Player.RED) {
        return "R";
      } else {
        return "B";
      }
    }
    if (cell.hasPawns()) {
      return String.valueOf(cell.getPawnCount());
    }
    return "_";
  }
}
