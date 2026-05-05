## Main game vision

There is no full vision for every element yet, but the presentation and loop are anchored in three references (not literal clones, but clear north stars):

- **Fallout (classic)** — lonely exploration, grounded environments, and a **visual tone** that leans cold, worn, and readable: desaturated colours, harsh winter light, Nordic-noir mood.
- **Final Fantasy–style isometric overworld** — the player moves on a **diamond-shaped tiled map** (not a hex grid), with clear readability for paths, cover, and interactable spots.
- **Heroes of Might and Magic–style battle view** — when combat starts, the game switches to a **dedicated tactical battle screen** (party vs enemies, turn order, positioning) rather than resolving fights only on the exploration map.

This is **not** a visual-novel-first game: story and investigation are delivered through **exploration, locations, NPCs, and UI** (dossier / clues / dialogue), not a slide-based VN pipeline.

Setting stays mostly **winter**: snow, short days, wind, cold — colours and lighting should sell that.

Note for AI: If you ever be unsure about direction in which you should go, ask user for clarification.

### Basic game mechanics

The whole gameplay logic should implement main rules of Call of Cthulhu (7th edition) RPG system, such as characters statistics and skills, used to calculating dice rolls results under the hood.
The player will control the character by walking around the isometric tiled map using mouse; on encounters, the game transitions to a **turn-based tactical battle** (HoMM-style battle view) where resolution follows the CoC RPG battle rules. Exploration and interior locations use the same broad paradigm: **tiles, movement, and interaction**, not a separate VN scene stack as the primary mode.
Player can also "enter" specific locations (with more detailed tiled map) where he can speak with NPCs, explore the enviroment and conduct an investigation to push the plot further.
Player should have inventory system where he can store his items that will surely be needed in his adventures.
Player should also have investigation system, where he can store and review clues and notes found through the story.
All player's progress and choices should be saved and later use in the story plot, to show that his choices have real consequences that are affecting the gameplay.

### Other technical details

Technically, the application window is limited to have minimal resolution of 1280x720 px (to make sure UI element have minimal space to not overflow themselves), and it's layout (UI/UX elements) should be responsive to changes in window size.

The whole application is internally divided into two parts - custom-made game engine (cthulhuEngine) that should be able to handle all neccessary functionalities that are not dependant on specific story; and the game itself (greatDreamerStories) that will use the posibilities of the engine and adding own functionalities on top of it, specific for its gameplay.

Game should have custom yet simple layout system that will provide easy placement of game elements (e.g. UI elements).

The game should use state manager that is resposible for managing and displaying proper view for any of the game states.

All elements of the program that could be reused, should have their own classes in separate files (managed in proper folder structure), to enable good longterm maintenance and best practices in terms of coding.