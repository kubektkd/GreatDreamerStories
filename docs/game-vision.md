## Main game vision

There is no full vision for all of the game's elements at this point, but the rough idea is to go in the direction of 2.5D isometric retro RPG style games (like firsts Dragon Quest or Final Fantasy games) with cold, desaturated colors that will capture the Nordic-Noire vibe well.
Setting will mostly be at the winter conditions - snowy, short daytime, windy and cold.

Note for AI: If you ever be unsure about direction in which you should go, ask user for clarification.

### Basic game mechanics

The whole gameplay logic should implement main rules of Call of Cthulhu (7th edition) RPG system, such as characters statistics and skills, used to calculating dice rolls results under the hood.
The player will control the character by walking around a tiled map where he may encounter enemies, in that case the game will change to turn based battle state - similar to Final Fantasy style - where the battle will be conducted according to the rules of the RPG system.
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