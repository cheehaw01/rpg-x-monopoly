package main;

import core.Game;

public class Main {

	// Conventions / tips
	// 1. Prefer raw arrays over collections (list, map) when storing primitive types
	// 	  as to avoid problems with boxing and unboxing
	// 2. Use camelCase for variable names and functions (Java convention)

	// Features to leave for later
	// 1. Battles with other players

	public static void main(String[] args) {
		Game game = new Game();
		game.run();
	}
}
