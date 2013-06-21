package twoagentmodel.relogo;

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;
import org.apache.commons.math3.distribution.*;

class Food extends BaseTurtle {
	//energy value of the food
	public int energy;
	//cost of the food
	public double money;
	//time to germinate after being planted
	public int gTime;
	//whether a plant is fertilized or not
	public boolean fertilized
	
	public int health;
	
	public int expiry;
	
	
	//default constructor
	public Food(){
		NormalDistribution moneyrandom = new NormalDistribution(avgCost, 3);
		NormalDistribution energyrandom = new NormalDistribution(avgEng, 30);
		energy = (int) energyrandom.sample();
		money = ceiling(100 * moneyrandom.sample())/100;
		health = 10
		expiry = 200
		gTime = 20;
	}
	
	public Food(Food planted){
		energy = planted.energy
		money = planted.money
		gTime = planted.gTime
	}
}
