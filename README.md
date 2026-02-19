# Sanguine Card Game

A strategic two-player card game built with Java featuring AI opponents, comprehensive game strategies, and a full Model-View-Controller architecture with GUI implementation.

**Developed as part of:** CS3100 Object-Oriented Design (Northeastern University)  
**Team Project:** Collaborative effort with teammate throughout design, implementation, and testing

## Overview

Sanguine is a turn-based strategy card game where players compete to control a grid-based board using cards with unique influence patterns. The game features multiple AI difficulty levels, real-time visual feedback, and sophisticated strategy implementations.

## Features

- Strategic card placement on grid-based board with pawn influence system
- Full Java Swing GUI with interactive board and card displays
- Three AI difficulty levels with distinct strategies
- Model-View-Controller architecture with observer pattern
- Keyboard and mouse controls with visual feedback
- Comprehensive testing suite with mock objects

## Quick Start

### Prerequisites

- Java JDK 11 or higher
- JUnit for running tests

### Running the Game

**GUI Mode:**
```bash
java -jar sanguine-card-game.jar 3 3 docs/example.deck docs/example.deck human strategy3
```

**Text Mode:**
```bash
java sanguine.Sanguine docs/example.deck
```

### Player Types

- `human` - Interactive player
- `strategy1` - Beginner AI (first legal move)
- `strategy2` - Intermediate AI (high-value prioritization)
- `strategy3` - Advanced AI (maximum influence)

## Technical Implementation

### Technologies
- Java with Swing for GUI
- JUnit for testing
- Object-oriented design principles

### Architecture
- **MVC Pattern:** Separate model, view, and controller layers
- **Observer Pattern:** Model state change notifications
- **Strategy Pattern:** Pluggable AI decision-making
- **Immutable Cards:** Thread-safe and prevents state corruption

### Project Structure
```
src/
├── main/java/sanguine/
│   ├── model/          # Game logic and rules
│   ├── view/           # GUI rendering
│   ├── controller/     # Input handling
│   └── strategy/       # AI implementations
└── test/java/sanguine/
    ├── model/          # Model tests
    ├── strategy/       # Strategy tests
    └── view/           # View tests
```

## AI Strategy Implementation

**Strategy 1 (Beginner):**
- Plays first valid card placement found
- Simple iterative approach

**Strategy 2 (Intermediate):**
- Prioritizes high-value cards
- Maximizes immediate scoring potential

**Strategy 3 (Advanced):**
- Optimizes board control through influence calculations
- Balances influence spreading with card value

## My Contributions

As part of a two-person team, I contributed to:
- Game logic implementation and card validation system
- AI strategy design with multiple difficulty levels
- MVC architecture and observer pattern implementation
- Comprehensive unit testing with mock objects
- Java Swing GUI development with interactive elements

## Game Rules

Players alternate turns placing cards from their hand onto the board. Each card has:
- **Cost:** Pawns required to play
- **Value:** Contribution to row scoring
- **Influence Pattern:** Defines pawn placement effects

**Objective:** Control rows by maximizing row scores relative to opponent.

**Winning:** Game ends when both players pass consecutively. Highest total score wins.

## Testing

Run tests:
```bash
mvn test
```

Key test suites:
- Model validation and game logic
- AI strategy behavior verification
- Mock-based isolated component testing

## Future Enhancements

- Network multiplayer support
- Game state persistence
- Machine learning-based AI strategies
- Tournament mode with rankings
- Custom deck builder

## License

This project was developed for educational purposes as part of CS3100 at Northeastern University.

## Acknowledgments

- Northeastern University CS3100 course staff
- Collaborative team development with partner
- Java Swing and testing framework documentation

---

**Note:** Team project demonstrating software design patterns and clean architecture in Java.