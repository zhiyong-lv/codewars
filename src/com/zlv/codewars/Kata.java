package com.zlv.codewars;

public class Kata {
	public static String declareWinner(Fighter fighter1, Fighter fighter2, String firstAttacker) {
		// Your code goes here. Have fun!
		if(fighter2.name.equals(firstAttacker)) {
			Fighter f = fighter1;
			fighter1 = fighter2;
			fighter2 = f;
		} else if(!fighter1.name.equals(firstAttacker)) {
			return "No one match!";
		}
		
		return isWinner(fighter1, fighter2);
	}
	
	private static String isWinner(Fighter fighter1, Fighter fighter2) {
		fighter2.health -= fighter1.damagePerAttack;
		System.out.println(String.format("%s attacks %s, %s now has %d health.", fighter1.name, fighter2.name, fighter2.name, fighter2.health));
		if(fighter2.health<=0) {
			return fighter1.name;
		} else {
			return isWinner(fighter2, fighter1);
		}
	}
}

class Fighter {
	public String name;
	public int health, damagePerAttack;

	public Fighter(String name, int health, int damagePerAttack) {
		this.name = name;
		this.health = health;
		this.damagePerAttack = damagePerAttack;
	}
}
