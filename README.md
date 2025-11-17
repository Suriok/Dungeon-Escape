# Dungeon Escape

## ℹ️ About the Game
Dungeon Escape is a 2D dungeon-crawling game focused on exploring dark dungeons, fighting enemies, and overcoming traps and puzzles. The player finds themself in a dark dungeon, where each level is a separate labyrinth filled with dangers and combat.

The goal of the game is to find keys to progress to new levels and ultimately face the final boss. In addition to combat, the player will collect weapons, armor, and potions to help them survive and grow stronger.

---

## 🎮 UI and Controls

### Main Menu
After launching the game, the main menu is displayed with the following options:
<img width="958" height="574" alt="main menu" src="https://github.com/user-attachments/assets/ba6d2c84-894d-4d73-9d87-e79c46fb0268" />

* **Start Game** – Starts the game from the beginning.
* **Start Saved Game** – Allows continuing from the last saved position.
* **Exit** – Closes the application.

### Player Controls
The player controls the character using the keyboard:

* **WASD** – Character movement in four directions.
* **Left Mouse Button** – Attack (if the player has a weapon).
* **E** – Interact (open doors, chests).
* **Q** – Open workbench (crafting).

The player cannot move diagonally, only in the four cardinal directions. Jumping, running, or swimming are not part of the game's mechanics.

---

## 💾 Saving and Loading
The game features automatic saving at the end of each level. The save system records the following information:

* **Player Position** – The character's location on the map.
* **Player Health** – Current health amount.
* **Inventory** – A list of all items held by the player.

If the player dies, the game loads from the last saved position, allowing them to continue from where they left off.

---

## ⚔️ Enemies and Combat System
The game features **zombies** <img width="16" height="16" alt="zombie_front1" src="https://github.com/user-attachments/assets/77604678-489e-4fa8-85b7-24558f2260f8" />, **slimes** <img width="16" height="16" alt="slime_1" src="https://github.com/user-attachments/assets/595d13a8-3ed2-4d92-b8a0-9f4fe1e6e26d" />, and **skeletons** <img width="16" height="16" alt="skeleton_down1" src="https://github.com/user-attachments/assets/6de62d96-650f-47a6-a61a-6e6481fea92b" />, which are placed throughout the map. At the end of each mini-level, there is also a unique **boss**.
