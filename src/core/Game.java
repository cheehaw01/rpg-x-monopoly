package core;

import util.Util;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Game {
	TileType[] boardTiles;

	List<Player> players;
	int currentPlayerIndex;

	List<Item> itemList;

	CommandParser commandParser;

	public Game() {
		commandParser = new CommandParser();
		itemList = new ArrayList<>();

		generateTiles();
		initPlayers();
		setupItems();
	}

	public void run() {
		while (players.size() > 0) {
			Player currPlayer = players.get(currentPlayerIndex);

			// TODO : Check stats

			System.out.printf("%n[Player %d] turn%n", currPlayer.getId());
			System.out.println("1. Roll dice");
			System.out.println("2. Quit");

			int choice = commandParser.readInt(new int[]{1, 2});

			switch (choice) {
				case 1:
					movePlayer(currPlayer);
					break;
				case 2:
					removePlayer(currPlayer);
					break;
				default:
					break;
			}

			// Prevent out of bound exceptions
			if(players.size() > 0) {
				currentPlayerIndex++;
				currentPlayerIndex = Util.loopClampInRange(currentPlayerIndex, 0, players.size());
			}
		}

		System.out.println("Game end");
	}

	private void generateTiles() {
		boardTiles = new TileType[] {
			TileType.START,
			TileType.DUO_M,
			TileType.TRI_M,
			TileType.SIN_M,
			TileType.EMPTY,
			TileType.DUO_M,
			TileType.SIN_M,
			TileType.SIN_M,
			TileType.SHOP,
			TileType.DUO_M,
			TileType.SIN_M,
			TileType.DUO_M,
			TileType.EMPTY,
			TileType.SIN_M,
			TileType.SIN_M,
			TileType.TRI_M,
			TileType.CHEST,
			TileType.SIN_M,
			TileType.DUO_M,
			TileType.TRI_M,
			TileType.EMPTY,
			TileType.SIN_M,
			TileType.DUO_M,
			TileType.SIN_M,
			TileType.SHOP,
			TileType.SIN_M,
			TileType.DUO_M,
			TileType.DUO_M,
			TileType.EMPTY,
			TileType.TRI_M,
			TileType.SIN_M,
			TileType.SIN_M
		};
	}

	private void initPlayers() {
		System.out.println("Enter player amount:");
		int initialPlayerCount = commandParser.readInt(new int[]{2, 3, 4});

		players = new ArrayList<>();

		for (int i = 0; i < initialPlayerCount; i++) {
			// Player id starts from 1 not 0
			players.add(new Player(i + 1));
		}
	}

	private void setupItems() {
		itemList.add(new Item("Sword", 50, 0, 3, 0, 0));
		itemList.add(new Item("Armor", 50, 0, 0, 3, -1));
		itemList.add(new Item("Potion", 50, 3, 1, 0, 1));
	}

	private void movePlayer(Player player) {
		player.move(DiceRoller.Roll());
		TileType currTile = getTileOn(player.getIndex());

		System.out.printf(
			"%n[Player %d] moved and landed on %s%n",
			player.getId(), currTile);

		switch (currTile) {
			case EMPTY:
			case START:
			case SIN_M: // Ignore for now
			case DUO_M: // Ignore for now
			case TRI_M: // Ignore for now
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
		System.out.printf("[Player %d] quit the game%n%n", player.getId());
		currentPlayerIndex--;
	}

	private void giveRandomItem(Player player) {
		Random r = new Random();

		int itemIndex = r.nextInt(itemList.size());
		Item item = itemList.get(itemIndex);
		player.addItem(item, 1);

		System.out.printf("%n[Player %d] gets %s from chest%n", player.getId(), item.Name);
	}

	private void setupShop(Player player) {
		System.out.printf("%n[Player %d] is in a shop%n", player.getId());
		System.out.println("1. Exit shop");
		System.out.println("2. Buy item");
		System.out.println("3. Sell item");

		int choice = commandParser.readInt(new int[]{1, 2, 3});

		Random r = new Random();

		// Shops only sells 3 random items at once
		List<Item> shopItems = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			shopItems.add(itemList.get(r.nextInt(3)));
		}

		// Loop until leave shop
		while (choice != 1) {
			switch (choice) {
				case 2:
					System.out.printf("%n[Player %d] Choose item to buy%n", player.getId());
					System.out.println("1. Back");
					System.out.printf("2. %s (%dG)%n", shopItems.get(0).Name, shopItems.get(0).Cost);
					System.out.printf("3. %s (%dG)%n", shopItems.get(1).Name, shopItems.get(1).Cost);
					System.out.printf("4. %s (%dG)%n", shopItems.get(2).Name, shopItems.get(2).Cost);

					int buyChoice = commandParser.readInt(new int[]{1, 2, 3, 4});

					// TODO : Buying items (check if player has enough gold)
					if (player.Gold >= shopItems.get(buyChoice-2).Cost) {
						player.Gold -= shopItems.get(buyChoice-2).Cost;
						player.addItem(shopItems.get(buyChoice-2), 1);
						System.out.printf("[Player %d] bought %s for %dG. Your remaining gold is %dG.%n", player.getId(), shopItems.get(buyChoice-2).Name, shopItems.get(buyChoice-2).Cost, player.Gold);
					} else {
						System.out.printf("[Player %d] gold is not enough%n", player.getId());
					}
					// Return to shop menu
					if(buyChoice == 1)
						break;

					break;
				case 3:
					System.out.printf("%n[Player %d] Choose item to sell%n", player.getId());
					System.out.println("1. Back");

					// TODO : List player items for sale

					int sellChoice = commandParser.readInt(new int[]{1});

					// Return to shop menu
					if(sellChoice == 1)
						break;

					break;
			}

			System.out.printf("%n[Player %d] is1 in a shop%n", player.getId());
			System.out.println("1. Exit shop");
			System.out.println("2. Buy item");
			System.out.println("3. Sell item");

			choice = commandParser.readInt(new int[]{1, 2, 3});
		}

		System.out.printf("%n[Player %d] left shop%n", player.getId());
	}

	private TileType getTileOn(int index) {
		index = Util.loopClampInRange(index, 0, boardTiles.length - 1);
		return boardTiles[index];
	}
}


