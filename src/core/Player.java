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

	private final int maxCapacity = 1;
	private final ArrayList<Item> inventory = new ArrayList<>();

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

	public boolean addItem(Item item) {
		if(inventory.size() >= maxCapacity)
			return false;

		Strength += item.StrengthBonus;
		Defense += item.DefenseBonus;
		Agility += item.AgilityBonus;
		Agility = Math.max(0, Math.min(Agility, 90));

		return inventory.add(item);
	}

	public void removeItem(int inventoryIndex) {
		Item item = inventory.remove(inventoryIndex);

		Strength -= item.StrengthBonus;
		Defense -= item.DefenseBonus;
		Agility -= item.AgilityBonus;
		Agility = Math.max(0, Math.min(Agility, 90));
	}
}
