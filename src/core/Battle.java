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

	public void start(Board board, BoardPanel boardPanel) {
		while ((player.Health > 0 && monsters.length > 0)) {
			if (isPlayerTurn) {
				//check for player effect
				if (player.negativeEffect.equals("Poisoned")) {
					String format = String.format("%n%dHP poison damage inflicted on [Player %d](%dHP)%n", ((int) (player.Health * 0.1)), player.getId(), player.Health);
                    boardPanel.getMessage().setText(format);
                    System.out.printf(format);
					player.Health -= ((int) (player.Health * 0.1));
				}

				String line1 = String.format("%n[Player %d](%dHP) is in battle%n", player.getId(), player.Health);
                System.out.printf(line1);
                String line2 = "1. Attack";
                System.out.println(line2);
                String line3 = "2. Use Item";
                System.out.println(line3);
                String format1 = String.format("3. Flee(%s)%n", player.Agility);
                System.out.printf(format1);
                boardPanel.getCommand().setText(line1+"\n"+line2+"\n"+line3+"\n"+format1);
				int choice = CommandParser.readInt(new int[]{1, 2, 3});

				switch (choice) {
					case 1:
						board.draw();
						String format = String.format("%n[Player %d](%dHP) chose monster to attack%n", player.getId(), player.Health);
						System.out.printf(format);
						
						String format00 = "1. Back";
						System.out.println(format00);

						boardPanel.getMessage().setText(format+"\n"+format00);
						// List all monsters
						for (int i = 0; i < monsters.length; i++) {
							Monster monster = monsters[i];

							String format09 = String.format("%d. Attack Lv%d %s(%dHP)%n",
							i + 2, monster.Level, monster.Type, monster.Health);
							System.out.printf(format09);
							boardPanel.getMessage().setText(format09);
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
							String line4 = String.format("%n[Player %d](%dHP) inflicted %dHP damage on Lv%d %s(%dHP)%n",
                            player.getId(), player.Health, damageToMonster, monsterToAttack.Level, monsterToAttack.Type, monsterToAttack.Health);
                            boardPanel.getMessage().setText(line4);
							System.out.printf(line4);
							
							if (player.positiveEffect.equals("Weapon Enchant")) {
								monsterToAttack.Health -= 30;
								String line08 = String.format("%n[Player %d](%dHP)'s enchanted weapon deal another 30HP damage on Lv%d %s(%dHP)%n",
								player.getId(), player.Health, monsterToAttack.Level, monsterToAttack.Type, monsterToAttack.Health);
                            boardPanel.getMessage().setText(line08);
							System.out.printf(line08);
							
						
							}
						}
						else {
							//dodged
							String line5 = String.format("%nLv %d %s(%dHP) dodged [Player %d](%dHP)'s attack%n",
                            monsterToAttack.Level, monsterToAttack.Type, monsterToAttack.Health, player.getId(), player.Health);
                            boardPanel.getMessage().setText(line5);
                            System.out.printf(line5);
						}

						//monster defeated
						if (monsterToAttack.Health <= 0) {
							String line6 = String.format("%n[Player %d](%dHP) defeated Lv%d %s(%dHP)%n",
                            player.getId(), player.Health, monsterToAttack.Level, monsterToAttack.Type, monsterToAttack.Health);
                            boardPanel.getMessage().setText(line6);
                            System.out.printf(line6);
							//remove monsterToAttack from monsters
							Monster[] tempArrayMonsters = new Monster[monsters.length - 1];

							//player gets item
							int dropId = 0;
							int selectDrop = monsterToAttack.Level;
							switch (selectDrop) {
								case 1:
									dropId = Util.RandomBetween(0, 6);
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
							String line7 = String.format("%n[Player %d](%dHP) got %s from drop%n",
                            player.getId(), player.Health, itemDrop.Name);
                            boardPanel.getMessage().setText(line7);
                            System.out.printf(line7);

							//player gets gold
							player.Gold += monsterToAttack.Gold;
							String line8 = String.format("%n[Player %d](%dHP) gained %d Gold%n",
                            player.getId(), player.Health, monsterToAttack.Gold);
							boardPanel.getMessage().setText(line8);
							System.out.printf(line8);
							if (player.positiveEffect.equals("Lucky")) {
								player.Gold += monsterToAttack.Gold;

								String line07 = String.format("%n[Player %d](%dHP) is lucky and gained another %d Gold%n",
								player.getId(), player.Health, monsterToAttack.Gold);
								boardPanel.getMessage().setText(line07);
								System.out.printf(line07);
					
							}

							//player gains exp
							player.Exp += monsterToAttack.Exp;
							String line9 = String.format("%n[Player %d](%dHP) gained %d EXP%n",
                            player.getId(), player.Health, monsterToAttack.Exp);
                            boardPanel.getMessage().setText(line9);
                            System.out.printf(line9);

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

						int res = tryUseItem(player, boardPanel);

						if (res == 0)
							continue;
						else if (res == 1)
							break;
						else if (res == 2) {
			
								String line06 = String.format("%n[Player %d](%dHP) choose monster to use poison%n"
								, player.getId(), player.Health);
								boardPanel.getMessage().setText(line06);
								System.out.printf(line06);

							System.out.println("1. Back");
							// List all monsters
							for (int i = 0; i < monsters.length; i++) {
								Monster monster = monsters[i];

								String line05 = String.format("%d. Attack Lv%d %s(%dHP)%n",
								i + 2, monster.Level, monster.Type, monster.Health);
								boardPanel.getMessage().setText(line05);
								System.out.printf(line05);
								
							}

							// Dynamically populate choices based on monsters
							monsterChoices = new int[monsters.length + 1];
							for (int i = 0; i < monsterChoices.length; i++) {
								monsterChoices[i] = i + 1;
							}

							monsterChoice = CommandParser.readInt(monsterChoices);
							if (monsterChoice == 1) {
								continue;
							}

							int monsterSelectIndex = monsterChoice - 2;
							Monster monsterToPoison = monsters[monsterSelectIndex];
							monsterToPoison.negativeEffect = "Poisoned";

							String line04 = String.format("%nLv %d %s(%dHP) is poisoned%n",
							monsterToPoison.Level, monsterToPoison.Type, monsterToPoison.Health);
								boardPanel.getMessage().setText(line04);
								System.out.printf(line04);
						
							break;
						}
						else
							return;
					case 3:
						board.draw();

						if (Math.random() * 100 < player.Agility) {
							String line10 = String.format("%n[Player %d] fled the battle%n",
                            player.getId());
                            boardPanel.getMessage().setText(line10);
                            System.out.printf(line10);
							return;
						}
						else {
							String line11 = String.format("%n[Player %d] flee unsuccessful%n",
                            player.getId());
                            boardPanel.getMessage().setText(line11);
                            System.out.printf(line11);
						}

						break;
				}
			}
			else {
				// Monsters turn
				System.out.printf("%nMonsters' turn to attack%n");

				for (Monster attackingMonster : monsters) {
					//each monster attack in turn

					//check monster's effect
					if (attackingMonster.negativeEffect.equals("Poisoned")) {

						String line04 = String.format("%n%dHP poison damage inflicted on Lv%d %s(%dHP)%n"
						, ((int) (attackingMonster.Health * 0.1)), attackingMonster.Level, attackingMonster.Type, attackingMonster.Health);
								boardPanel.getMessage().setText(line04);
								System.out.printf(line04);
						attackingMonster.Health -= ((int) (attackingMonster.Health * 0.1));
					}

					//player dodge attack probability 0~45%
					if (Math.random() * 200 > player.Agility) {
						//dodge failed
						int damageToPlayer = attackingMonster.Strength * 100 / (100 + player.Defense);
						player.Health -= Math.min(player.Health, damageToPlayer);
						String line12 = String.format("%nLv%d %s(%dHP) inflicted %dHP damage on [Player %d](%dHP)%n",
                        attackingMonster.Level, attackingMonster.Type, attackingMonster.Health, damageToPlayer, player.getId(), player.Health);
                attackingMonster.useAbility(player);//use monster ability
                        boardPanel.getMessage().setText(line12);
                        System.out.printf(line12);
	
						attackingMonster.useAbility(player);//use monster ability
						if (player.Health <= 0) {
							//player defeated
							String line13 = String.format("%n[Player %d](%dHP) was defeated by Lv %d %s(%dHP)%n",
                            player.getId(), player.Health, attackingMonster.Level, attackingMonster.Type, attackingMonster.Health);
                        boardPanel.getMessage().setText(line13);
						System.out.printf(line13);
						}
					}
					else {
						//dodged
						String line14 = String.format("%n[Player %d](%dHP) dodged Lv%d %s(%dHP)'s attack%n",
                        player.getId(), player.Health, attackingMonster.Level, attackingMonster.Type, attackingMonster.Health);
                        boardPanel.getMessage().setText(line14);
						System.out.printf(line14);
					}
				}

			}

			// Toggle turn
			isPlayerTurn = !isPlayerTurn;
		}

		//player effect, bonus end.
		potionBonusEnd(player);
		player.negativeEffect = "";
		player.positiveEffect = "";

	}

	public void PvPStart(Board board, BoardPanel boardPanel) {
		String line15 = String.format("%n[Player %d](%dHP) will fight [Player %d](%dHP)%n",
        player1.getId(), player1.Health, player2.getId(), player2.Health);
                        boardPanel.getMessage().setText(line15);
                        System.out.printf(line15);

		while ((player1.Health > 0 && player2.Health > 0)) {
			if (isPlayerTurn) {
				//check for effect
				if (player1.negativeEffect.equals("Poisoned")) {

					String line16 = String.format("%n%dHP poison damage inflicted on [Player %d](%dHP)%n"
                    , ((int) (player1.Health * 0.1)), player1.getId(), player1.Health);
			player1.Health -= ((int) (player1.Health * 0.1));
			boardPanel.getMessage().setText(line16);
                                    System.out.printf(line16);

					player1.Health -= ((int) (player1.Health * 0.1));
				}

				String line17 = String.format("%n[Player %d](%dHP) is in battle%n", player1.getId(), player1.Health);
                System.out.printf(line17);
                String line18 = String.format("1. Attack [Player %d](%dHP)%n", player2.getId(), player2.Health);
                System.out.println(line18);
                String line19 = "2. Use Item";
                System.out.println(line19);
                String Line20 = String.format("3. Flee(%d%c)%n", player1.Agility, '%');
                System.out.println(20);
                boardPanel.getCommand().setText(line17+"\n"+line18+"\n"+line19+"\n"+Line20);


				int choice = CommandParser.readInt(new int[]{1, 2, 3});

				switch (choice) {
					case 1:
						board.draw();
						attackPlayer(player1, player2, boardPanel);
						if (player2.Health <= 0) {
							return;//end battle already
						}
						break;
					case 2:
						board.draw();

						int res = tryUseItem(player1, boardPanel);

						if (res == 0)
							continue;
						else if (res == 1)
							break;
						else if (res == 2) {
							player2.negativeEffect = "Poisoned";
							String line03 = String.format("%n[Player %d](%dHP) is poisoned%n", player2.getId(), player2.Health);
                            System.out.println(  line03 );
                        boardPanel.getMessage().setText(line03);

							
						}
						else
							return;
					case 3:
						board.draw();

						if (Math.random() * 100 < player1.Agility) {
							String line21 = String.format("%n[Player %d] fled the battle%n",
                            player1.getId());
                            System.out.println(line21);
                        boardPanel.getMessage().setText(line21);

							return;
						}
						else {
							String line22 = String.format("%n[Player %d] flee unsuccessful%n",
                            player1.getId());
                            System.out.println(line22);
                        boardPanel.getMessage().setText(line22);
						}
						break;
				}
			}
			else {
				//check for effect
				if (player2.negativeEffect.equals("Poisoned")) {
					String line23 = String.format("%n%dHP poison damage inflicted on [Player %d](%dHP)%n"
                    , ((int) (player2.Health * 0.1)), player2.getId(), player2.Health);
            player2.Health -= ((int) (player2.Health * 0.1));
                    System.out.println(line23);
                boardPanel.getMessage().setText(line23);
				}

				String line24 = String.format("%n[Player %d](%dHP) is in battle%n", player2.getId(), player2.Health);
                System.out.printf(line24);
                String line25 = String.format("1. Attack [Player %d](%dHP)%n", player1.getId(), player1.Health);
                System.out.println(line25);
                String line26 = "2. Use Item";
                System.out.println(line26);
                String line27 = String.format("3. Flee(%d%c)%n", player2.Agility, '%');
                System.out.println(line27);
                boardPanel.getCommand().setText(line24+"\n"+line25+"\n"+line26+"\n"+line27);
				int choice = CommandParser.readInt(new int[]{1, 2, 3});

				switch (choice) {
					case 1:
						board.draw();
						attackPlayer(player2, player1, boardPanel);
						break;
					case 2:
						board.draw();

						int res = tryUseItem(player2, boardPanel);

						if (res == 0)
							continue;
						else if (res == 1)
							break;
						else if (res == 2) {
							player1.negativeEffect = "Poisoned";
							String line99 = String.format("%n[Player %d](%dHP) is poisoned%n", player1.getId(), player1.Health);
                    System.out.println(line99);
                boardPanel.getMessage().setText(line99);
							
							break;
						}
						else
							return;
					case 3:
						board.draw();

						if (Math.random() * 100 < player2.Agility) {
							String line28 = String.format("%n[Player %d] fled the battle%n",
                            player2.getId());
                    System.out.println(line28);
                boardPanel.getMessage().setText(line28);

							return;
						}
						else {
							String line29 = String.format("%n[Player %d] flee unsuccessful%n",
                            player2.getId());
                    System.out.println(line29);
                boardPanel.getMessage().setText(line29);
						}
						break;
				}
			}
			isPlayerTurn = !isPlayerTurn;
		}

		//both player effect, bonus end.
		potionBonusEnd(player1);
		player1.negativeEffect = "";
		player1.positiveEffect = "";
		potionBonusEnd(player2);
		player2.negativeEffect = "";
		player2.positiveEffect = "";
	}

	public void attackPlayer(Player player, Player playerToAttack,BoardPanel boardPanel ) {
		if (Math.random() * player.Agility > Math.random() * playerToAttack.Agility) {

			int damage = player.Strength * 100 / (100 + playerToAttack.Defense);
			playerToAttack.Health -= Math.min(playerToAttack.Health, damage);

			String line30 = String.format("%n[Player %d](%dHP) inflicted %dHP damage on [Player %d](%dHP)%n",
			player.getId(), player.Health, damage, playerToAttack.getId(), playerToAttack.Health);
                    System.out.println(line30);
				boardPanel.getMessage().setText(line30);
				
			
			if (player.positiveEffect.equals("Weapon Enchant")) {
				playerToAttack.Health -= 30;

				String line31 = String.format("%n[Player %d](%dHP)'s enchanted weapon deal another 30HP damage on [Player %d](%dHP)%n",
				player.getId(), player.Health, playerToAttack.getId(), playerToAttack.Health);
                    System.out.println(line31);
				boardPanel.getMessage().setText(line31);

				
			}
		}
		else {
			//dodged
			String line32 = String.format("%n[Player %d](%dHP) dodged [Player %d](%dHP)'s attack%n",
			playerToAttack.getId(), playerToAttack.Health, player.getId(), player.Health);
                    System.out.println(line32);
				boardPanel.getMessage().setText(line32);

	
		}

		//player defeated
		if (playerToAttack.Health <= 0) {
			String line33 = String.format("%n[Player %d](%dHP) defeated [Player %d](%dHP)%n",
			player.getId(), player.Health, playerToAttack.getId(), playerToAttack.Health);
                    System.out.println(line33);
				boardPanel.getMessage().setText(line33);
			
			//player gets playerToAttack items
			ArrayList<Item> defeatedInventory = playerToAttack.getItems();
			if (defeatedInventory.size() != 0) {
				Item defeatedDrop = defeatedInventory.get(Util.RandomBetween(0, defeatedInventory.size() - 1));
				player.addItem(defeatedDrop);

				String line34 = String.format("%n[Player %d](%dHP) get %s from [Player %d](%dHP)%n",
				player.getId(), player.Health, defeatedDrop.Name, playerToAttack.getId(), playerToAttack.Health);
                    System.out.println(line34);
				boardPanel.getMessage().setText(line34);
			
			}
			else {
				String line35 = String.format("%n[Player %d](%dHP) does not have item to give [Player %d](%dHP)%n",
				playerToAttack.getId(), playerToAttack.Health, player.getId(), player.Health);
                    System.out.println(line35);
				boardPanel.getMessage().setText(line35);
				
			}

			//player gets gold = 50% playerToAttack gold 
			player.Gold += (playerToAttack.Gold / 2);
			String line36 = String.format("%n[Player %d](%dHP) gained %d Gold%n",
			player.getId(), player.Health, (playerToAttack.Gold / 2));
                    System.out.println(line36);
				boardPanel.getMessage().setText(line36);

	
			if (player.positiveEffect.equals("Lucky")) {
				player.Gold += (playerToAttack.Gold / 2);

				String line37 = String.format("%n[Player %d](%dHP) is lucky and gained another %d Gold%n",
				player.getId(), player.Health, (playerToAttack.Gold / 2));
                    System.out.println(line37);
				boardPanel.getMessage().setText(line37);
			}

			//player gains exp = 50% playerToAttack exp
			player.Exp += playerToAttack.Exp / 2;

			String line38 = String.format("%n[Player %d](%dHP) gained %d EXP%n",
			player.getId(), player.Health, playerToAttack.Exp / 2);
                    System.out.println(line38);
				boardPanel.getMessage().setText(line38);
			
			//check for level up
			player.levelUp();
		}
	}

	// 0 if didn't use item, 1 if used item, 3 if used smoke bomb
	private int tryUseItem(Player player, BoardPanel boardPanel)  {

		String line39 = String.format("%n[Player %d] chose item to use%n", player.getId());
                    System.out.println(line39);
				boardPanel.getCommand().setText(line39);
				String line40 = "1. Back";
				System.out.println(line40);
			boardPanel.getCommand().setText(line40);
		

		int choiceSize = 1;

		for (int i = 0; i < player.getItems().size(); i++) {
			Item item = player.getItems().get(i);

			if (item.IsUsable) {
				String line41 = String.format("%d. Use %s%n", choiceSize + 1, item.Name);
                    System.out.println(line41);
				boardPanel.getCommand().setText(line41);
			
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

			String line42 = String.format("%n[Player %d] escaped the battle%n", player.getId());
                    System.out.println(line42);
				boardPanel.getMessage().setText(line42);
		
			return 3;
		}
		else if (itemToUse.Name.equals("Poison"))
			return 2;
		else {
			if (itemToUse.Name.equals("Strength Potion")) {


			String line43 = String.format("%n[Player %d](%dHP) use %s, strength increase by %d%n",
			player.getId(), player.Health, itemToUse.Name, ((int) (player.Strength * 0.2)));
			System.out.println(line43);
		boardPanel.getMessage().setText(line43);
			
				potionBonusEnd(player);
				player.tempStrength = ((int) (player.Strength * 0.2));
				potionBonus(player);
			}
			if (itemToUse.Name.equals("Agility Potion")) {
				String line44 = String.format("%n[Player %d](%dHP) use %s, agility increase by %d%n",
				player.getId(), player.Health, itemToUse.Name, ((int) (player.Agility * 0.2)));
			System.out.println(line44);
		boardPanel.getMessage().setText(line44);

				potionBonusEnd(player);
				player.tempAgility = ((int) (player.Agility * 0.2));
				potionBonus(player);
			}
			if (itemToUse.Name.equals("Defence Potion")) {

				String line45 = String.format("%n[Player %d](%dHP) use %s, defense increase by %d%n",
				player.getId(), player.Health, itemToUse.Name, ((int) (player.Defense * 0.2)));
			System.out.println(line45);
		boardPanel.getMessage().setText(line45);
				
				potionBonusEnd(player);
				player.tempDefense = ((int) (player.Defense * 0.2));
				potionBonus(player);
			}
			if (itemToUse.Name.equals("Ability Potion")) {
				String line46 = String.format("%n[Player %d](%dHP) use %s, strength +%d, agility +%d and defense +%d %n",
				player.getId(), player.Health, itemToUse.Name,
				((int) (player.Strength * 0.2)), ((int) (player.Agility * 0.2)), (int) (player.Defense * 0.2));
			System.out.println(line46);
		boardPanel.getMessage().setText(line46);
				
				potionBonusEnd(player);
				player.tempStrength = ((int) (player.Strength * 0.2));
				player.tempAgility = ((int) (player.Agility * 0.2));
				player.tempDefense = ((int) (player.Defense * 0.2));
				potionBonus(player);
			}
			if (itemToUse.Name.equals("Rage Potion")) {
				String line47 = String.format("%n[Player %d](%dHP) use %s, strength +%d, agility +%d and defense +%d %n",
				player.getId(), player.Health, itemToUse.Name,
				((int) (player.Strength * 0.5)), ((int) (player.Agility * 0.5)), (int) (player.Defense * 0.5));
			System.out.println(line47);
		boardPanel.getMessage().setText(line47);

			
				potionBonusEnd(player);
				player.tempStrength = ((int) (player.Strength * 0.5));
				player.tempAgility = ((int) (player.Agility * 0.5));
				player.tempDefense = ((int) (player.Defense * 0.5));
				potionBonus(player);
			}
			if (itemToUse.Name.equals("HP Potion")) {

				String line48 = String.format("%n[Player %d](%dHP) use %s, recover %d HP%n",
				player.getId(), player.Health, itemToUse.Name, ((int) (player.Health * 0.2)));
			System.out.println(line48);
		boardPanel.getMessage().setText(line48);
				
				player.Health += ((int) (player.Health * 0.2));
			}
			if (itemToUse.Name.equals("Nasi Lemak")) {
				String line49 = String.format("%n[Player %d](%dHP) eat %s, recover 50 HP%n",
				player.getId(), player.Health, itemToUse.Name);
			System.out.println(line49);
		boardPanel.getMessage().setText(line49);

				player.Health += 50;
			}
			if (itemToUse.Name.equals("Antidote")) {
				if (player.negativeEffect.equals("Poisoned")) {
					player.negativeEffect = "";
					String line50 = String.format("%n[Player %d] release effect of poison with %s%n", player.getId(), itemToUse.Name);
			System.out.println(line50);
		boardPanel.getMessage().setText(line50);

					
				}
				else {

					String line51 = String.format("%n[Player %d] is not poisoned%n", player.getId());
			System.out.println(line51);
		boardPanel.getMessage().setText(line51);
					
				}
			}
			if (itemToUse.Name.equals("Weapon Enchanter")) {
				player.positiveEffect = "Weapon Enchant";

				String line51 = String.format("%n[Player %d] weapon is enchanted%n", player.getId());
			System.out.println(line51);
		boardPanel.getMessage().setText(line51);

			}
			if (itemToUse.Name.equals("Lucky Potion")) {
				player.positiveEffect = "Lucky";


				String line52 = String.format("%n[Player %d] is now lucky%n", player.getId());
			System.out.println(line52);
		boardPanel.getMessage().setText(line52);

			}
			return 1;
		}
	}

	public void potionBonus(Player player) {
		player.Strength += player.tempStrength;
		player.Agility += player.tempAgility;
		player.Defense += player.tempDefense;
	}

	public void potionBonusEnd(Player player) {
		player.Strength -= player.tempStrength;
		player.Agility -= player.tempAgility;
		player.Defense -= player.tempDefense;
	}
}

