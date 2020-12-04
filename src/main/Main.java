package main;

import core.Game;

public class Main {

    // Conventions / tips
    // 1. Prefer raw arrays over collections (list, map) when storing primitive types
	// 	  as to avoid problems with boxing and unboxing

	// Features to leave for later
	// 1. Game board drawing
	// 2. Battle system
	// 3. Item special abilities (ex. Smoke bomb)
    // 4. Console clearing

    public static void main(String[] args) {
        Game game = new Game();
        game.run();
    }
}
