package core;

public class Battle {
	Player player;
	Monster[] monsters;

	boolean isPlayerTurn = true;

	public Battle(Player player, int monsterCount) {
		this.player = player;
		monsters = new Monster[monsterCount];

		for (int i = 0; i < monsters.length; i++) {
			monsters[i] = new Monster();
		}
	}

	public void start(Board board) {
		while ((player.Health > 0 && monsters.length > 0)) {
			if (isPlayerTurn) {
				System.out.printf("%n[Player %d](%dHP) is in battle%n", player.getId(), player.Health);
				System.out.println("1. Attack");
				System.out.println("2. Use Item");
				System.out.println("3. Flee");

				int choice = CommandParser.readInt(new int[]{1, 2, 3});

				switch (choice) {
					case 1:
						board.draw();
						System.out.printf("%n[Player %d](%dHP) chose monster to attack%n",
							player.getId(), player.Health);

						System.out.println("1. Back");
						// List all monsters
						for (int i = 0; i < monsters.length; i++) {
							Monster monster = monsters[i];
							System.out.printf("%d. Attack Monster%d (%dHP)%n",
								i + 2, i + 1, monster.Health);
						}

						// Dynamically populate choices based on monsters
						int[] monsterChoices = new int[monsters.length + 1];
						for (int i = 0; i < monsterChoices.length; i++) {
							monsterChoices[i] = i + 1;
						}

						int monsterChoice = CommandParser.readInt(monsterChoices);

						board.draw();

						// Return to battle menu
						if (monsterChoice == 1) {
							break;
						}

						// Attack monster
						int monsterIndex = monsterChoice - 2;
						Monster monsterToAttack = monsters[monsterIndex];

						// TODO: Monster attack logic

						System.out.printf("%n[Player %d](%dHP) attacked Monster%d(%dHP)%n",
							player.getId(), player.Health, monsterChoice, monsterToAttack.Health);

						break;
					case 2:
						System.out.println("No idea how to do item using. Leave it for later");
						break;
					case 3:
						// TODO: Flee battle algorithm based on player agility
						board.draw();

						if (Math.random() < 0.5) {
							System.out.printf("%n[Player %d] fled the battle%n",
								player.getId());
							return;
						}

						break;
				}
			}
			else {
				// Monsters turn

				// TODO: Monster attacking
			}

			// Toggle turn
			isPlayerTurn = !isPlayerTurn;
		}
	}
}

