package core;

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

	public Battle(Player player1, Player player2){
		this.player1 = player1;
		this.player2 = player2;
	}

	public void start(Board board) {
		ArrayList<Item> dropped = new ArrayList<>();
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
							
							//monster drop item
							int dropId = 0;
							int selectDrop = monsterToAttack.Level;
							switch(selectDrop) {
								case 1:
									dropId = Util.RandomBetween(0, 5);
									break;
								case 2:
									dropId = Util.RandomBetween(0, 18);
									break;
								case 3:
									dropId = Util.RandomBetween(0, 25);
									break;
								default:
									dropId = Util.RandomBetween(0, 29);
									break;
							}
							Item itemDrop = Game.itemList.get(dropId);
							dropped.add(itemDrop);
							System.out.printf("%nLv%d %s(%dHP) drop item [%s]%n", monsterToAttack.Level, 
								monsterToAttack.Type, monsterToAttack.Health, itemDrop.Name);

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

						if(tryUseItem(player))
							break;
						else
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
		
		if (player.Health > 0) {
			// player pick items dropped
			System.out.printf("%n[Player %d](%dHP) battle end%n", player.getId(), player.Health);
			while (dropped.size() != 0) {
				System.out.printf("%n[Player %d](%dHP) Select item pick%n", player.getId(), player.Health);
				System.out.println("1. Leave");

				// List all dropped items
				for (int i = 0; i < dropped.size(); i++) {
					Item itemDropped = dropped.get(i);
					System.out.printf("%d. %s%n", i + 2, itemDropped.Name);
				}

				// Dynamically populate choices based on dropped items
				int[] pickChoices = new int[dropped.size() + 1];
				for (int i = 0; i < pickChoices.length; i++) {
					pickChoices[i] = i + 1;
				}

				int pickChoice = CommandParser.readInt(pickChoices);

				// Leave
				if (pickChoice == 1) {
					break;
				}

				// Pick item
				int itemIndex = pickChoice - 2;
				Item itemPicked = dropped.get(itemIndex);
				if (player.addItem(itemPicked)) {
					dropped.remove(itemPicked);
					System.out.printf("%n[Player %s] pick %s%n", player.getId(), itemPicked.Name);
				}
				else {
					System.out.printf("%n[Player %d] inventory full%n", player.getId());
					System.out.printf("%n[Player %d] Choose item to discard%n", player.getId());
					System.out.println("1. Back");

					ArrayList<Item> playerItems = player.getItems();

					// List all player items
					for (int i = 0; i < playerItems.size(); i++) {
						Item itemToDiscard = playerItems.get(i);
						System.out.printf("%d. %s%n", i + 2, itemToDiscard.Name);
					}

					int[] discardChoices = new int[playerItems.size() + 1];
					for (int i = 0; i < discardChoices.length; i++) {
						discardChoices[i] = i + 1;
					}

					int discardChoice = CommandParser.readInt(discardChoices);
					// Back
					if (discardChoice == 1) {
						continue;
					}

					// Pick item
					int playeritemIndex = discardChoice - 2;
					Item discardedItem = playerItems.get(playeritemIndex);
					player.removeItem(playeritemIndex);
					System.out.printf("%n[Player %d] discard [%s]%n", player.getId(), discardedItem.Name);
				}	
				continue;
			}
			System.out.printf("%n[Player %d](%dHP) leave%n", player.getId(), player.Health);

	}

	public void PKStart(Board board) {
		System.out.printf("%n[Player %d](%dHP) will fight [Player %d](%dHP)%n",
		player1.getId(), player1.Health, player2.getId(), player2.Health);

		while ((player1.Health > 0 && player2.Health > 0)) {
			if (isPlayerTurn){
				System.out.printf("%n[Player %d](%dHP) is in battle%n", player1.getId(), player1.Health);
				System.out.printf("1. Attack [Player %d](%dHP)%n", player2.getId(), player2.Health);
				System.out.println("2. Use Item");
				System.out.printf("3. Flee(%d%c)%n", player1.Agility, '%');
	
				int choice = CommandParser.readInt(new int[]{1, 2, 3});
	
				switch (choice) {
					case 1:
						board.draw();
						attackPlayer(player1, player2);
						if (player2.Health <= 0){
							return;//end battle already
						}
						break;
					case 2:
						board.draw();

						if(tryUseItem(player))
							break;
						else
							continue;
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
			else{
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

						if(tryUseItem(player2))
							break;
						else
							continue;
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

	public void attackPlayer(Player player, Player playerToAttack){				
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

	// True if used item, false if didn't use item
	boolean tryUseItem(Player player) {
		System.out.printf("%n[Player %d] chose item to use%n", player.getId());
		System.out.println("1. Back");

		int choiceSize = 1;

		for (int i = 0; i < player.getItems().size(); i++) {
			Item item = player.getItems().get(i);

			if(item.IsUsable) {
				System.out.printf("%d. Use %s%n", i + 2, item.Name);
				choiceSize++;
			}
		}

		int[] itemChoices = new int[choiceSize];
		for (int i = 0; i < itemChoices.length; i++) {
			itemChoices[i] = i + 1;
		}

		int itemChoice = CommandParser.readInt(itemChoices);
		if(itemChoice == 1)
			return false;

		Item itemToUse = player.getItems().get(itemChoice - 2);
		itemToUse.use(player);

		System.out.printf("%n[Player %d] used %s%n", player.getId(), itemToUse.Name);
		return true;
	}
}

