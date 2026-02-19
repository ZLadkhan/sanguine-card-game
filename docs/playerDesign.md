**Overview:**

The user-player would represent a being, either human or AI,
that makes real time decisions in the game. This would be different
from the Player enum (RED/BLUE) which represent game entities within
the model.


**User-Player Interface Design:**

A GamePlayer would have a method to choose an action given the current game state.
The method would take in a ReadOnlySanguineModel to observe the board without modifying it.
Then the player would return an action (PlaceCard or Pass).

There would be Action objects that contain what to do.
PlaceCardAction would store which card to play and where.
PassAction would pass the turn. Actions would execute on the model
when instructed to do so.


**Player Types:**

There will be multiple player types as stated before.
- Human
- Primitive AI
- Advanced AI

**Human:**
A Human Player would display the board and hand while also reading the
inputs and responding accordingly.

**Primitive AI:**
A Primitive AI would try to place the first valid card at any spot possible
and pass if no cards fit.

**Advanced AI:**
An Advanced AI would survey the board and evaluate all possible moves with regards to influence
and row control. After evaulation, it would pick the best move.

**Controller:**
A controller would be used to manage both the player instances and the model.
It would ask the player for an action, execute said action on the model, update the view,
and repeat this process until the game finishes.