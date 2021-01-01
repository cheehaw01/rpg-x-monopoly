package core;

import util.Util;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Game {
	Board board;

	List<Player> players;
	int currentPlayerIndex;

	List<Item> itemList;

	public Game() {
		board = new Board();
		itemList = new ArrayList<>();

		initPlayers();
		setupItems();
	}

	public void run() {
		board.draw();

		while (players.size() > 1) {
			Player currPlayer = players.get(currentPlayerIndex);

			System.out.printf("%n[Player %d] turn%n", currPlayer.getId());
			System.out.println("1. Roll dice");
			System.out.println("2. Check stats");
			System.out.println("3. Quit");

			int choice = CommandParser.readInt(new int[]{1, 2, 3});

			switch (choice) {
				case 1:
					movePlayer(currPlayer);
					break;
				case 2:
					checkStats(currPlayer);
					currentPlayerIndex--; // So that player doesn't lose turn
					break;
				case 3:
					removePlayer(currPlayer);
					break;
				default:
					break;
			}

			if (currPlayer.Health <= 0){
				removePlayer(currPlayer);
			}

			currentPlayerIndex++;
			currentPlayerIndex = Util.wrapAroundClamp(currentPlayerIndex, 0, players.size());
		}

		System.out.printf("%n[Player %s] WON! %n", players.get(0).getId());
	}

	// ================ SETUP ================

	private void initPlayers() {
		System.out.print("Enter player amount:");
		int initialPlayerCount = CommandParser.readInt(new int[]{2, 3, 4});

		players = new ArrayList<>();

		for (int i = 0; i < initialPlayerCount; i++) {
			// Player id starts from 1 not 0
			players.add(new Player(i + 1));
		}
	}

	private void setupItems() {
		itemList.add(new Item("Junk", 0, 0, 0, 0, 0));
		itemList.add(new Item("Smokebomb", 400, 0, 0, 0, 0));
		itemList.add(new Item("Basic Sword", 200, 0, 10, 0, 0));
		itemList.add(new Item("Basic Wand", 200, 0, 10, 0, 0));
		itemList.add(new Item("Basic Bow", 200, 0, 10, 0, 0));
		itemList.add(new Item("Leather shirt", 400, 0, 0, 20, 10));
		itemList.add(new Item("Shield", 700, 0, 0, 60, 0));
		itemList.add(new Item("Standard Sword", 600, 0, 35, 0, 0));
		itemList.add(new Item("Magic Wand", 600, 0, 30, 10, -10));
		itemList.add(new Item("Standard Bow", 600, 0, 30, 5, 0));
		itemList.add(new Item("Steel Armour", 800, 0, 0, 45, -10));
		itemList.add(new Item("Strength Potion", 700, 0, 0, 0, 0)); 
		itemList.add(new Item("Agility Potion", 700, 0, 0, 0, 0));
		itemList.add(new Item("Defence Potion", 700, 0, 0, 0, 0)); 
		itemList.add(new Item("HP Potion", 600, 0, 0, 0, 0));
		itemList.add(new Item("Antidote", 600, 0, 0, 0, 0));
		itemList.add(new Item("Lucky Potion", 600, 0, 0, 0, 0));
		itemList.add(new Item("Barricade", 700, 0, 0, 0, 0));
		itemList.add(new Item("Double Dice", 700, 0, 0, 0, 0));
		itemList.add(new Item("Great Sword", 1100, 0, 75, 0, -20));
		itemList.add(new Item("Magic Book", 1100, 100, 60, 0, 0));
		itemList.add(new Item("Crossbow", 1100, 0, 65, 0, -10));
		itemList.add(new Item("Titanium Armour", 1500, 0, 75, 0, -20));
		itemList.add(new Item("Ability Potion", 1400, 0, 0, 0, 0));
		itemList.add(new Item("Poison", 800, 0, 0, 0, 0));
		itemList.add(new Item("Experience Potion", 2000, 0, 0, 0, 0));
		itemList.add(new Item("Excalibur", 2000, 0, 130, 10, 0));
		itemList.add(new Item("Infinity Ring", 2000, 200, 115, 0, 0));
		itemList.add(new Item("Sniper rifle", 2000, 0, 150, 0, -10));
		itemList.add(new Item("Energy Shield", 2000, 0, 0, 110, 0));
		// Item(String name, int cost, int healBonus, int strengthBonus, int defenseBonus, int agilityBonus)
	}

	// ================ PLAYER ================

	private void movePlayer(Player player) {
		player.move(DiceRoller.Roll());
		TileType currTile = board.getTileOn(player.getIndex());

		board.draw();
		System.out.printf(
			"%n[Player %d] moved and landed on %s%n",
			player.getId(), currTile);

		switch (currTile) {
			case EMPTY:
			case START:
				break;
			case SIN_M:
				new Battle(player, 1).start(board);
				break;
			case DUO_M:
				new Battle(player, 2).start(board);
				break;
			case TRI_M:
				new Battle(player, 3).start(board);
				break;
			case CHEST:
				giveRandomItem(player);
				break;
			case SHOP:
				setupShop(player);
				break;
		}
	}

	private void removePlayer(Player player) {
		players.remove(player);

		System.out.printf("%n[Player %d] quit the game%n", player.getId());
		currentPlayerIndex--;
	}

	private void checkStats(Player player) {
		board.draw();
		System.out.printf("%n[Player %d] stats%n", player.getId());

		System.out.printf("Type: %s%n", player.Type);
		System.out.printf("Level: %d%n", player.Level);
		System.out.printf("Exp: %d%n", player.Exp);
		System.out.printf("Health: %d%n", player.Health);
		System.out.printf("Strength: %d%n", player.Strength);
		System.out.printf("Defense: %d%n", player.Defense);
		System.out.printf("Agility: %d%n", player.Agility);
		System.out.printf("Gold: %d%n", player.Gold);
	}

	
	// ================ GAMEPLAY ================

	private void giveRandomItem(Player player) {
		Random r = new Random();

		// 10% - very rare, 20% - rare, 30% moderate, 40% common
		int itemAppear = r.nextInt(100) + 1;
		int itemIndex = 0;
		if(itemAppear <= 5 || itemAppear >= 95) {
			itemIndex = r.nextInt(6);
		}
		else if(itemAppear <= 15 || itemAppear >= 75) {
			itemIndex = r.nextInt(19);
		}
		else if(itemAppear <= 40 || itemAppear >= 60) {
			itemIndex = r.nextInt(26);
		}
		else {
			itemIndex = r.nextInt(itemList.size());
		}
		Item item = itemList.get(itemIndex);
		player.addItem(item, 1);

		if(player.addItem(item)){
			System.out.printf("%n[Player %d] gets %s from chest%n", player.getId(), item.Name);
		}
		else {
			// Not enough space in inventory
			System.out.printf("%n[Player %d] Not enough space in inventory%n",
				player.getId());
		}
	}

	private void setupShop(Player player) {
		System.out.printf("%n[Player %d](%dG) is in a shop%n", player.getId(), player.Gold);
		System.out.println("1. Exit shop");
		System.out.println("2. Buy item");
		System.out.println("3. Sell item");

		int choice = CommandParser.readInt(new int[]{1, 2, 3});

		Random r = new Random();

		// Shops only sells 3 random items at once
		List<Item> shopItems = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			shopItems.add(itemList.get(r.nextInt(25)));
		}

		// Loop until leave shop
		while (choice != 1) {
			switch (choice) {
				case 2:
					board.draw();
					System.out.printf("%n[Player %d](%dG) Choose item to buy%n", player.getId(), player.Gold);
					System.out.println("1. Back");
					System.out.printf("2. %s (%dG)%n", shopItems.get(0).Name, shopItems.get(0).Cost);
					System.out.printf("3. %s (%dG)%n", shopItems.get(1).Name, shopItems.get(1).Cost);
					System.out.printf("4. %s (%dG)%n", shopItems.get(2).Name, shopItems.get(2).Cost);

					int buyChoice = CommandParser.readInt(new int[]{1, 2, 3, 4});

					board.draw();

					// Return to shop menu
					if (buyChoice == 1) {
						break;
					}

					Item itemToBuy = shopItems.get(buyChoice - 2);

					if (player.Gold >= itemToBuy.Cost) {
						if(player.addItem(itemToBuy)) {
							player.Gold -= itemToBuy.Cost;

							System.out.printf("%n[Player %d] bought %s for %dG. Remaining gold is %dG.%n",
								player.getId(), itemToBuy.Name, itemToBuy.Cost, player.Gold);
						}
						else {
							// Not enough space in inventory
							System.out.printf("%n[Player %d] Not enough space in inventory%n",
								player.getId());
						}
					}
					else {
						System.out.printf("[Player %d] gold is not enough%n", player.getId());
					}

					break;
				case 3:
					board.draw();
					System.out.printf("%n[Player %d] Choose item to sell%n", player.getId());
					System.out.println("1. Back");

					List<Item> playerItems = player.getItems();

					// List all player items
					for (int i = 0; i < playerItems.size(); i++) {
						Item itemToSell = playerItems.get(i);
						System.out.printf("%d. Sell %s (%dG)%n", i + 2, itemToSell.Name, itemToSell.Cost);
					}

					// Dynamically populate choices based on player items
					int[] sellChoices = new int[playerItems.size() + 1];
					for (int i = 0; i < sellChoices.length; i++) {
						sellChoices[i] = i + 1;
					}

					int sellChoice = CommandParser.readInt(sellChoices);

					board.draw();

					// Return to shop menu
					if (sellChoice == 1) {
						break;
					}

					// Sell item
					int itemIndex = sellChoice - 2;
					Item itemSold = playerItems.get(itemIndex);

					player.Gold += itemSold.Cost;
					System.out.printf("%n[Player %s] sold %s for %dG%n",
						player.getId(), itemSold.Name, itemSold.Cost);

					player.removeItem(itemIndex);

					break;
			}

			System.out.printf("%n[Player %d](%dG) is in a shop%n", player.getId(), player.Gold);
			System.out.println("1. Exit shop");
			System.out.println("2. Buy item");
			System.out.println("3. Sell item");

			choice = CommandParser.readInt(new int[]{1, 2, 3});
		}

		board.draw();
		System.out.printf("%n[Player %d] left shop%n", player.getId());
	}
}
