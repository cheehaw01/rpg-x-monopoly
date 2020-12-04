package core;

// Items only upgrade player stats for now
public class Item {
	public final int HealBonus;
	public final int StrengthBonus;
	public final int DefenseBonus;
	public final int AgilityBonus;

	public Item(int healBonus, int strengthBonus, int defenseBonus, int agilityBonus) {
		HealBonus = healBonus;
		StrengthBonus = strengthBonus;
		DefenseBonus = defenseBonus;
		AgilityBonus = agilityBonus;
	}
}
