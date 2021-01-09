package core;

import util.Util;

import java.util.ArrayList;

public class Battle {
	//instance variables for PvM
	Player player;
	Monster[] monsters;

	//instance variables for PvP
	Player player1, player2;

	boolean isPlayerTurn = true;

	public Battle(Player player, int monsterCount) {
		this.player = player;
		monsters = new Monster[monsterCount];

		for (int i = 0; i < monsters.length; i++) {
			monsters[i] = new Monster(player);
		}
	}

	public Battle(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
	}

	public void start(Board board) {
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
							System.out.printf("%n[Player %d](%dHP) inflicted %dHP damage on Lv%d %s(%dHP)%n",
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

							//player gets item
							int dropId = 0;
							int selectDrop = monsterToAttack.Level;
							switch (selectDrop) {
								case 1:
									dropId = Util.RandomBetween(0, 5);
									break;
								case 2:
									dropId = Util.RandomBetween(0, 18);
									break;
								case 3:
									dropId = Util.RandomBetween(0, 25);
									break;
								case 4:
									dropId = Util.RandomBetween(0, 29);
									break;
							}
							Item itemDrop = Game.itemList.get(dropId);

							//player gets item drop
							player.addItem(itemDrop);
							System.out.printf("%n[Player %d](%dHP) got %s from drop%n",
								player.getId(), player.Health, itemDrop.Name);

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

						int res = tryUseItem(player);

						if (res == 0)
							continue;
						else if (res == 1)
							break;
						else
							return;
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

				for (Monster attackingMonster : monsters) {
					//each monster attack in turn
					//player dodge attack probability 0~45%
					if (Math.random() * 200 > player.Agility) {
						//dodge failed
						int damageToPlayer = attackingMonster.Strength * 100 / (100 + player.Defense);
						player.Health -= Math.min(player.Health, damageToPlayer);
						System.out.printf("%nLv%d %s(%dHP) inflicted %dHP damage on [Player %d](%dHP)%n",
							attackingMonster.Level, attackingMonster.Type, attackingMonster.Health, damageToPlayer, player.getId(), player.Health);
						attackingMonster.useAbility(player);//use monster ability
						if (player.Health <= 0) {
							//player defeated
							System.out.printf("%n[Player %d](%dHP) was defeated by Lv %d %s(%dHP)%n",
								player.getId(), player.Health, attackingMonster.Level, attackingMonster.Type, attackingMonster.Health);
						}
					}
					else {
						//dodged
						System.out.printf("%n[Player %d](%dHP) dodged Lv%d %s(%dHP)'s attack%n",
							player.getId(), player.Health, attackingMonster.Level, attackingMonster.Type, attackingMonster.Health);
					}
				}

			}

			// Toggle turn
			isPlayerTurn = !isPlayerTurn;
		}

	}

	public void PvPStart(Board board) {
		System.out.printf("%n[Player %d](%dHP) will fight [Player %d](%dHP)%n",
			player1.getId(), player1.Health, player2.getId(), player2.Health);

		while ((player1.Health > 0 && player2.Health > 0)) {
			if (isPlayerTurn) {
				System.out.printf("%n[Player %d](%dHP) is in battle%n", player1.getId(), player1.Health);
				System.out.printf("1. Attack [Player %d](%dHP)%n", player2.getId(), player2.Health);
				System.out.println("2. Use Item");
				System.out.printf("3. Flee(%d%c)%n", player1.Agility, '%');

				int choice = CommandParser.readInt(new int[]{1, 2, 3});

				switch (choice) {
					case 1:
						board.draw();
						attackPlayer(player1, player2);
						if (player2.Health <= 0) {
							return;//end battle already
						}
						break;
					case 2:
						board.draw();

						int res = tryUseItem(player1);

						if (res == 0)
							continue;
						else if (res == 1)
							break;
						else
							return;
					case 3:
						board.draw();

						if (Math.random() * 100 < player1.Agility) {
							System.out.printf("%n[Player %d] fled the battle%n",
								player1.getId());
							return;
						}
						else {
							System.out.printf("%n[Player %d] flee unsuccessful%n",
								player1.getId());
						}
						break;
				}
			}
			else {
				System.out.printf("%n[Player %d](%dHP) is in battle%n", player2.getId(), player2.Health);
				System.out.printf("1. Attack [Player %d](%dHP)%n", player1.getId(), player1.Health);
				System.out.println("2. Use Item");
				System.out.printf("3. Flee(%d%c)%n", player2.Agility, '%');

				int choice = CommandParser.readInt(new int[]{1, 2, 3});

				switch (choice) {
					case 1:
						board.draw();
						attackPlayer(player2, player1);
						break;
					case 2:
						board.draw();

						int res = tryUseItem(player2);

						if (res == 0)
							continue;
						else if (res == 1)
							break;
						else
							return;
					case 3:
						board.draw();

						if (Math.random() * 100 < player2.Agility) {
							System.out.printf("%n[Player %d] fled the battle%n",
								player2.getId());
							return;
						}
						else {
							System.out.printf("%n[Player %d] flee unsuccessful%n",
								player2.getId());
						}
						break;
				}
			}
			isPlayerTurn = !isPlayerTurn;
		}
	}

	public void attackPlayer(Player player, Player playerToAttack) {
		if (Math.random() * player.Agility > Math.random() * playerToAttack.Agility) {

			int damage = player.Strength * 100 / (100 + playerToAttack.Defense);
			playerToAttack.Health -= Math.min(playerToAttack.Health, damage);
			System.out.printf("%n[Player %d](%dHP) inflicted %dHP damage on [Player %d](%dHP)%n",
				player.getId(), player.Health, damage, playerToAttack.getId(), playerToAttack.Health);
		}
		else {
			//dodged
			System.out.printf("%n[Player %d](%dHP) dodged [Player %d](%dHP)'s attack%n",
				playerToAttack.getId(), playerToAttack.Health, player.getId(), player.Health);
		}

		//player defeated
		if (playerToAttack.Health <= 0) {
			System.out.printf("%n[Player %d](%dHP) defeated [Player %d](%dHP)%n",
				player.getId(), player.Health, playerToAttack.getId(), playerToAttack.Health);

			//player gets playerToAttack items
			ArrayList<Item> defeatedInventory = playerToAttack.getItems();
			if (defeatedInventory.size() != 0) {
				Item defeatedDrop = defeatedInventory.get(Util.RandomBetween(0, defeatedInventory.size()));
				player.addItem(defeatedDrop);
				System.out.printf("%n[Player %d](%dHP) get %s from [Player %d](%dHP)%n",
					player.getId(), player.Health, defeatedDrop.Name, playerToAttack.getId(), playerToAttack.Health);
			}
			else {
				System.out.printf("%n[Player %d](%dHP) does not have item to give [Player %d](%dHP)%n",
					playerToAttack.getId(), playerToAttack.Health, player.getId(), player.Health);
			}

			//player gets gold = 50% playerToAttack gold 
			player.Gold += (playerToAttack.Gold / 2);
			System.out.printf("%n[Player %d](%dHP) gained %d Gold%n",
				player.getId(), player.Health, (playerToAttack.Gold / 2));

			//player gains exp = 50% playerToAttack exp
			player.Exp += playerToAttack.Exp / 2;
			System.out.printf("%n[Player %d](%dHP) gained %d EXP%n",
				player.getId(), player.Health, playerToAttack.Exp / 2);

			//check for level up
			player.levelUp();
		}
	}

	// 0 if didn't use item, 1 if used item, 2 if used smoke bomb
	public int tryUseItem(Player player) {
		System.out.printf("%n[Player %d] chose item to use%n", player.getId());
		System.out.println("1. Back");

		int choiceSize = 1;

		for (int i = 0; i < player.getItems().size(); i++) {
			Item item = player.getItems().get(i);

			if (item.IsUsable) {
				System.out.printf("%d. Use %s%n", choiceSize + 1, item.Name);
				choiceSize++;
			}
		}

		int[] itemChoices = new int[choiceSize];
		for (int i = 0; i < itemChoices.length; i++) {
			itemChoices[i] = i + 1;
		}

		int itemChoice = CommandParser.readInt(itemChoices);
		if (itemChoice == 1)
			return 0;

		int itemIndex = itemChoice - 2;

		Item itemToUse = null;

		for (int i = 0, c = 0; i < player.getItems().size(); i++) {
			Item item = player.getItems().get(i);

			if (item.IsUsable) {
				if (itemIndex == c) {
					itemToUse = item;
					player.removeItem(i);
				}

				c++;
			}
		}

		itemToUse.use(player);

		if (itemToUse.Name.equals("Smoke Bomb")) {
			System.out.printf("%n[Player %d] escaped the battle%n", player.getId());
			return 2;
		}
		else {
			System.out.printf("%n[Player %d] used %s%n", player.getId(), itemToUse.Name);
			return 1;
		}
	}
}

