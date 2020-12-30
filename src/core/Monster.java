package core;

import util.Util;

public class Monster {
	public int Health;
	public int Strength;
	public int Defense;
	public int Agility;
	public int Exp;
	public int Gold;
	public int Level = 1;
	public String Type;
	public String[] Types = {"Tanker", "Lunatic", "Trickster"};//we'll leave fallen mage out for now
	public int Stats[][][] = {/*Tanker*/{{100, 250, 400, 550}, {10, 20, 30, 40},{50, 60, 70, 80}, {5, 10, 15, 20}},
							/*Lunatic*/{{50, 100, 150, 200}, {30, 50, 70, 90}, {10, 20, 30, 50}, {5, 20, 35, 50}},
							/*Trickster*/{{50, 150, 250, 350}, {20, 30, 40, 50}, {30, 40, 50, 60}, {20, 40, 60, 80}},};
							/* 				Health				Strength			Defense				Agility
							By level*/
	public Monster() {
		// Generate values and types based on player level.
		int choiceType = Util.RandomBetween(0, 2);
		switch (choiceType) {
			case 0:
				Type = "Tanker";
				break;
			
			case 1:
				Type = "Lunatic";
				break;

			case 2:
				Type = "Trickster";
				break;
		}
		//TODO: generate level based on player level.

		Gold = Util.RandomBetween(10, 50) * Level;
		Exp = Util.RandomBetween(10, 30) * Level;

		Health = Stats[choiceType][0][Level - 1];
		Strength = Stats[choiceType][1][Level - 1];
		Defense = Stats[choiceType][2][Level - 1];
		Agility = Stats[choiceType][3][Level - 1];
	}
}
