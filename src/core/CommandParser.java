package core;

import util.Util;

import java.util.Scanner;

// Mostly dealing with int inputs
// ex.
// 1. Option A
// 2. Option B
// 3. Option C
// > [option number]

public class CommandParser {
	private final Scanner scn;

	public CommandParser() {
		scn = new Scanner(System.in);
	}

	public int readInt(int[] choices) {
		while (true) {
			int v = getIntInput();

			if (Util.intArrayContains(choices, v))
				return v;

			System.out.println("Invalid choice. Chose again.");
		}
	}

	// TODO : Repeatedly get input and check format (try-catch)
	private int getIntInput() {
			System.out.print("> ");
			return scn.nextInt();
	}
}
