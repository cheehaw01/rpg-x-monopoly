package core;

import java.util.ArrayList;


// Container for stats and state
public class Player {
	// STATS
	public int Level = 1;
	public int Health = 100;
	public int Strength = 50;
	public int Defense = 50;
	public int Agility = 5;
	public int Gold = 200;
	public int Exp = 0;
	public String Type = "Normal";
	public String[] Types = {"Normal", "Melee", "Mage", "Archer"};
	public int[] expGauges = {50, 150, 300}; //limits of upgrading
	public int[][][] Stats = {/*melee*/{{100, 200, 400, 700},{50, 70, 90, 110},{50, 60, 70, 80},{5, 20, 35, 50}},
								/*mage*/{{100, 200, 400, 700},{50, 60, 70, 80},{50, 80, 110, 140},{5, 15, 25, 35}},
								/*archer*/{{100, 200, 400, 700},{50, 80, 110, 140},{50, 70, 90, 110},{5, 10, 15, 20}}};
										/*Health, 				Strength, 			Defense, 			Agility
										all stats by level 1-4*/

	private final int maxCapacity = 4;
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

	public void levelUp(){
		if (Exp < 50 || Exp > 500){
			//dont waste runtime here
			return;
		}

		boolean upgraded = false;
		for (int i = 0; i < expGauges.length; i++){
			if (i == Level - 1 && Exp >= expGauges[Level - 1]){
				//just passel level gauge for the first time this level
				Level++;
				System.out.printf("%n[Player %d] upgraded to Level %d%n", getId(), Level);
				upgraded = true;
				break;//no need to continue looping
			}
		}
		
		if (upgraded == true){
			if (Level == 2){
				//for now allow choose type at level 2 only
				chooseType();
			}

			//upgrade stats
			int typeIndex = 0;
			for (int i = 1; i < Types.length; i++){
				if (Type.equals(Types[i])){
					typeIndex = i - 1;
					break;
				}
			}

			Health = Stats[typeIndex][0][Level - 1];
			//health will recover to full health of next level but it will only happens 3 times throughout the game so whatever
			Strength = Stats[typeIndex][1][Level - 1];
			Defense = Stats[typeIndex][2][Level - 1];
			Agility =  Stats[typeIndex][3][Level - 1];
		}
		
	}

	public void chooseType() {
		System.out.println("Choose Type:");
		for (int i = 1; i < Types.length; i++){
			System.out.printf("%d. %s\n", i, Types[i]);
		}
		
		int choiceType = CommandParser.readInt(new int[]{1, 2, 3});
		Type = Types[choiceType];
		System.out.printf("[Player %d] is now type %s\n", getId(), Type);
	}
	


}
