package core;

import java.util.ArrayList;

// Container for stats and state
public class Player {
	// STATS
	public int Level = 1;
	public int Health = 25;
	public int Strength = 5;
	public int Defense = 5;
	public int Agility = 5;
	public int Gold = 200;
	public int Exp = 0;

	private final ArrayList<Item> inventory = new ArrayList<>(5);

	// STATE
	private int index = 0; // Counts from 0 - 31
	private int id = 0;

	public Player(int id) {
		this.id = id;
	}

	public void move(int steps) {
		index += steps;
	}

	public int getIndex() {
		return index;
	}

	public int getId() {
		return id;
	}

	public ArrayList<Item> getItems() {
		return inventory;
	}

	public void addItem(Item item, int quantity) {
		for (int i = 0; i < quantity; i++) {
			inventory.add(item);
		}
	}

	public void removeItem(int inventoryIndex) {
		inventory.remove(inventoryIndex);
	}
}
