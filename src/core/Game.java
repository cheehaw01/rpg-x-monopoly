package core;

import util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

public class Game {
	TileType[] boardTiles;
	List<Player> players;
	int currentPlayerIndex;

	CommandParser commandParser;

	public Game() {
		commandParser = new CommandParser();

		generateTiles();
		initPlayers();
	}

	public void run() {
		while(players.size() > 0) {
			Player currPlayer = players.get(currentPlayerIndex);

			System.out.printf("%n[Player %d] turn%n", currPlayer.getId());
			System.out.println("Chose option");
			System.out.println("1. Roll dice");
			System.out.println("2. Quit");

			int choice = commandParser.readInt(new int[] {1, 2});

			switch (choice){
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
			currentPlayerIndex++;
			currentPlayerIndex = Util.loopClampInRange(currentPlayerIndex, 0, players.size() - 1);
		}

		System.out.println("Game end");
	}

	// TODO : Random tile generation using predetermined values (see design doc)
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

	private void initPlayers(){
		System.out.println("Enter player amount");
		int initialPlayerCount = commandParser.readInt(new int[]{ 2, 3, 4 });

		players = new ArrayList<>();

		for (int i = 0; i < initialPlayerCount; i++) {
			// Player id starts from 1 not 0
			players.add(new Player(i + 1));
		}
	}

	private void movePlayer(Player player){
		player.move(DiceRoller.Roll());
		TileType currTile = getTileOn(player.getIndex());

		System.out.printf(
			"%n[Player %d] moved and landed on %s%n",
			player.getId(), currTile);

		switch (currTile){
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
		int itemId = r.nextInt(30);
		int option = 1;
		player.bag(option, itemId);

		System.out.printf("%n[Player %d] gets random item from chest%n", player.getId());

		// TODO : Give random item (Player store list of items?)
		// Ans : Player store id of items.
	}

	private void setupShop(Player player) {
		System.out.printf("%n[Player %d] is in a shop%n", player.getId());
		System.out.println("Chose option");
		System.out.println("1. Exit shop");
		System.out.println("2. Buy item");
		System.out.println("3. Sell item");

		int choice = commandParser.readInt(new int[] {1, 2, 3});

		// TODO : Buying items (need place to store item data, maybe hash map)
		HashMap<Integer, Integer> shopItems = new HashMap<Integer, Integer>();
		// I not sure but there's Example below.
		// Example: item id for "Sword" is 2 and potion is 28
		shopItems.put(1, 2);
		shopItems.put(2, 28);

		int option = 0;
		int itemSelect = 0;
		switch (choice) {
			case 1:
				System.out.printf("%n[Player %d] left shop%n", player.getId());
				break;
			case 2:
				System.out.println("Choose item to buy");
				option = 1;
				System.out.println("1. Sword");
				System.out.println("2. Potion");
				itemSelect = commandParser.readInt(new int[] {1, 2}); // << According to number of items sell in shop.
				player.bag(option, shopItems.get(itemSelect));
				break;
			case 3:
				System.out.println("Choose item to sell");
				option = 3;
				player.bag(option, 0);
				itemSelect = commandParser.readInt(new int[] {1, 2, 3, 4}); // << depend on bag size.
				option = 2;
				player.bag(option, itemSelect);
				break;
		}
		
	}

	private TileType getTileOn(int index) {
		index = Util.loopClampInRange(index, 0, boardTiles.length - 1);
		return boardTiles[index];
	}
}
