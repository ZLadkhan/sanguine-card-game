package sanguine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import sanguine.model.Card;
import sanguine.model.PlayingCard;

/**
 * Represents a class for reading a deck in the game of Sanguine.
 */
public class SanguineDeckReader implements DeckReader {

  /**
   * Represents reading a file with the specified deck configuration and creates a list of cards.
   * There is a specific format in which the file format expects cards to be provided.
   * - There is one line with "NAME COST VALUE"
   * - Five distinct lines with five characters that represent the influence grid
   * - 'X' denotes no influence, 'I' denotes influence, and 'C' denotes the center card
   *
   * @param pathToFile represents the path to the file for the deck configuration
   * @return list of cards which are being read from the file
   * @throws FileNotFoundException    if the file does not exist at the specified path
   * @throws IOException              if there is an error when reading the file
   * @throws IllegalArgumentException if the format of the file is not valid
   */
  @Override
  public List<Card> readDeck(String pathToFile) throws IOException {
    File file = new File(pathToFile);
    if (!file.exists()) {
      throw new FileNotFoundException("The path to the file could not be found.");
    }

    List<Card> deck = new ArrayList<>();

    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        Card card = readCard(scanner);
        deck.add(card);
      }
    }

    return deck;
  }

  private Card readCard(Scanner scanner) {
    String line = scanner.nextLine().trim();
    String[] parts = line.split("\\s+");

    if (parts.length != 3) {
      throw new IllegalArgumentException("THe card header is not valid.");
    }

    String name = parts[0];
    int cost = Integer.parseInt(parts[1]);
    int value = Integer.parseInt(parts[2]);

    boolean[][] pattern = new boolean[5][5];
    for (int row = 0; row < 5; row++) {
      String rowLine = scanner.nextLine().trim();
      if (rowLine.length() != 5) {
        throw new IllegalArgumentException("The influence row has to be five characters.");
      }

      for (int column = 0; column < 5; column++) {
        char character = rowLine.charAt(column);
        if (character == 'I' || character == 'C') {
          pattern[row][column] = true;
        } else if (character != 'X') {
          throw new IllegalArgumentException("The influence character is not valid.");
        }
      }
    }

    return new PlayingCard(name, cost, value, pattern);
  }
}
