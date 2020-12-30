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
		//might want to change this name to differentiate PvM and PvP
		while ((player.Health > 0 && monsters.length > 0)) {
			if (isPlayerTurn) {
				System.out.printf("%n[Player %d](%dHP) is in battle%n", player.getId(), player.Health);
				System.out.println("1. Attack");
				System.out.println("2. Use Item");
				System.out.printf("3. Flee(%d%c)%n", player.Agility, '%');

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
							System.out.printf("%d. Attack Lv%d %s(%dHP)%n",
								i + 2, monster.Level, monster.Type, monster.Health);
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
							continue;
						}

						// Attack monster
						int monsterIndex = monsterChoice - 2;
						Monster monsterToAttack = monsters[monsterIndex];

						//monster dodge attack probability 0~20%
						if (Math.random() * 500 > monsterToAttack.Agility) {

							int damageToMonster = player.Strength * 100 / (100 + monsterToAttack.Defense);
							monsterToAttack.Health -= Math.min(monsterToAttack.Health, damageToMonster);
							System.out.printf("%n[Player %d](%dHP) inflicted %dHP damage on Lv%d %sd(%dHP)%n",
								player.getId(), player.Health, damageToMonster, monsterToAttack.Level, monsterToAttack.Type, monsterToAttack.Health);
						}
						else {
							//dodged
							System.out.printf("%nLv %d %s(%dHP) dodged [Player %d](%dHP)'s attack%n",
								monsterToAttack.Level, monsterToAttack.Type, monsterToAttack.Health, player.getId(), player.Health);
						}

						//monster defeated
						if (monsterToAttack.Health <= 0) {
							System.out.printf("%n[Player %d](%dHP) defeated Lv%d %s(%dHP)%n",
								player.getId(), player.Health, monsterToAttack.Level, monsterToAttack.Type, monsterToAttack.Health);
							//remove monsterToAttack from monsters
							Monster[] tempArrayMonsters = new Monster[monsters.length - 1];

							//player gets gold
							player.Gold += monsterToAttack.Gold;
							System.out.printf("%n[Player %d](%dHP) gained %d Gold%n",
							player.getId(), player.Health, monsterToAttack.Gold);

							//player gains exp
							player.Exp += monsterToAttack.Exp;
							System.out.printf("%n[Player %d](%dHP) gained %d EXP%n",
								player.getId(), player.Health, monsterToAttack.Exp);

							//check for level up
							player.levelUp();

							for (int i = 0, k = 0; i < monsters.length; i++) {
								//copy all monsters except monsterToAttack
								if (i != monsterIndex) {
									tempArrayMonsters[k++] = monsters[i];
								}
							}
							monsters = tempArrayMonsters.clone();
						}

						break;
					case 2:
						board.draw();
						System.out.printf("%nNo idea how to do item using. Leave it for later%n");
						continue;
					case 3:
						board.draw();

						if (Math.random() * 100 < player.Agility) {
							System.out.printf("%n[Player %d] fled the battle%n",
								player.getId());
							return;
						}
						else {
							System.out.printf("%n[Player %d] flee unsuccessful%n",
								player.getId());
						}

						break;
				}
			}
			else {
				// Monsters turn
				System.out.printf("%nMonsters' turn to attack%n");

				for (int monsterIndex = 0; monsterIndex < monsters.length; monsterIndex++) {
					//each monster attack in turn
					Monster attackingMonster = monsters[monsterIndex];
					//player dodge attack probability 0~45%
					if (Math.random() * 200 > player.Agility) {
						//dodge failed
						int damageToPlayer = attackingMonster.Strength * 100 / (100 + player.Defense);
						player.Health -= Math.min(player.Health, damageToPlayer);
						System.out.printf("%nLv%d %s(%dHP) inflicted %dHP damage on [Player %d](%dHP)%n",
							attackingMonster.Level, attackingMonster.Type, attackingMonster.Health, damageToPlayer, player.getId(), player.Health);
						if (player.Health <= 0) {
							//player defeated
							System.out.printf("%n[Player %d](%dHP) was defeated by Lv %d %s(%dHP)%n",
								player.getId(), player.Health, attackingMonster.Level, attackingMonster.Type, attackingMonster.Health);
						}
					}
					else {
						//dodged
						System.out.printf("%n[Player %d](%dHP) dodged Lv%d %s(%dHP)'s attack%n",
							player.getId(), player.Health, attackingMonster.Level,attackingMonster.Type, attackingMonster.Health);
					}
				}

			}

			// Toggle turn
			isPlayerTurn = !isPlayerTurn;
		}

	}
}

