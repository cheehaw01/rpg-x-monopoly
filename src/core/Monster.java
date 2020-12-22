package core;

import util.Util;

public class Monster {
	public int Health;
	public int Strength;
	public int Defense;
	public int Agility;
	public int Exp;

	public Monster() {
		// Maybe generate values based on player level

		Health = Util.RandomBetween(5, 15);
		Strength = Util.RandomBetween(1, 3);
		Defense = Util.RandomBetween(1, 3);
		Agility = Util.RandomBetween(1, 3);

		Exp = Util.RandomBetween(25, 50);
	}
}
