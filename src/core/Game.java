package core;

import util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import core.TileType;

public class Game {
	TileType[] boardTiles;
	List<Player> players;
	int currentPlayerIndex;
	HashMap<Integer, String> itemList;
	ArrayList<Integer> Inventory;

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
    //Done: Lawrence 6/12/2020
	private void generateTiles() {
        Random rand = new Random();
		
        boardTiles = new TileType[32];
        for (TileType tile: boardTiles){
            //initialize all tiles as empty
            tile = TileType.EMPTY;
        }
        //hard code non-monster tiles
        boardTiles[0] = TileType.START;
        boardTiles[16] = TileType.CHEST;
        boardTiles[8] = boardTiles[24] = TileType.SHOP;

        //generate monster_type[i] tiles for monster_count[i] times
        TileType[] monster_types = {TileType.SIN_M, TileType.DUO_M, TileType.TRI_M};
        int[] monster_count = {12, 8, 4};
        for (int i = 0; i < 3; i++){
            int count = 0;
            do{
                //generate number from 1 to 31 (since 0 is START TILE) 
                int pos = rand.nextInt(31) + 1; 
                //leave set tiles untouuched
                if (pos % 4 != 0 && boardTiles[pos] == TileType.EMPTY){
                    count++;
                    boardTiles[pos] = monster_types[i];
                }
            }
            while (count < monster_count[i]);
        }	
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
		player.addItem(itemId, 1);
		System.out.printf("%n[Player %d] gets random item from chest%n", player.getId());

		// TODO : Give random item (Player store list of items?)
		// DONE by HGC.
	}

	private void setupShop(Player player) {
		System.out.printf("%n[Player %d] is in a shop%n", player.getId());
		System.out.println("Chose option");
		System.out.println("1. Exit shop");
		System.out.println("2. Buy item");
		System.out.println("3. Sell item");

		int choice = commandParser.readInt(new int[] {1, 2, 3});

		// TODO : Buying items (need place to store item data, maybe hash map)
		// DONE by HGC
		
		HashMap<Integer, Integer> shopItems = new HashMap<Integer, Integer>();
		String[] itemsDisplay = new String[] {"Exit shop", "Sword", "Potion", "Armour"}; // <After we set which items to sell at shop.
		// Example: itemID for Sword is 2, Potion is 28, Armout is 5
		shopItems.put(1, 0);
		shopItems.put(2, 2);
		shopItems.put(3, 28);
		shopItems.put(4, 5);

		int itemSelect;
		int leaveShop = 0;
		
		switch(choice) {
			case 1:
				System.out.printf("%n[Player %d] left shop%n", player.getId());
				break;
			case 2:
				while(leaveShop == 0) {
					System.out.println("Choose item to buy");
					for(int i=0; i<itemsDisplay.length; i++){
						System.out.printf("%d. %s\n", i+1, itemsDisplay[i]);
					}
					itemSelect = commandParser.readInt(new int[] {1, 2, 3, 4});
					if(itemSelect != 1) {
						System.out.println("Quantity: ");
						int q = commandParser.readInt(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9});
						player.addItem(shopItems.get(itemSelect), q);
					}
					else {
						leaveShop = 1;
					}
				}
				break;
			case 3:
				while(leaveShop == 0) {
					int iSize = player.Inventory.size();
					System.out.println("Choose item to sell");
					int[] itemChoice = new int[iSize+1];
					for(int i=0; i<iSize; i++) {
						System.out.printf("%d. %s\n", i+1, gameItems(player.Inventory.get(i)));
						itemChoice[i] = i+1;
					}
					itemChoice[iSize] = iSize+1;
					System.out.printf("%d. Exit shop\n", itemChoice.length);
					itemSelect = commandParser.readInt(itemChoice);
					if(itemSelect != itemChoice.length) {
						player.removeItem(itemSelect-1);
					}
					else {
						leaveShop = 1;
					}
				}
				break;
		}
	
	}	

	private TileType getTileOn(int index) {
		index = Util.loopClampInRange(index, 0, boardTiles.length - 1);
		return boardTiles[index];
	}

	private String gameItems(int itemID) {
		itemList = new HashMap<Integer, String>();
		{	
			itemList.put(0, "Empty");
			itemList.put(2, "Sword");
			itemList.put(28, "Potion");
			itemList.put(5, "Armour");
			// more

		}
		return itemList.get(itemID);

	}
}


