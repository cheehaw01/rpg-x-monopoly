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
	private final static Scanner scn = new Scanner(System.in);
	
	public static int readInt(int[] choices, BoardPanel boardPanel) {
		while (true) {
			try {
				int v = getIntInput();

				if (Util.intArrayContains(choices, v)) {
					return v;
				}
				else {
					String InvalidC = "Invalid choice. Chose again.";
                    boardPanel.getMessage().setText(InvalidC);
                    System.out.println(InvalidC);
					
				}
			} catch (Exception ignored) {
				String InvalidF = "Invalid choice. Chose again.";
					boardPanel.getMessage().setText(InvalidF);
					System.out.println(InvalidF);
			
				scn.next();
			}
		}
	}

	private static int getIntInput() {
		System.out.print("> ");
		return scn.nextInt();
	}
}
