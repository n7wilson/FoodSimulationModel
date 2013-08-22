package foodsimulationmodel.relogo.food;

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;
import org.apache.commons.math3.distribution.*;

import foodsimulationmodel.relogo.agents.IAgent;

public class Food {
	//Average cost and energy for all Food items
	//Used as a basic way of getting a distribution of food prices
	public static int avgCost = 30;
	public static int avgEng = 25;
	
	//Energy value of the Food
	public int energy;
	//Cost of the Food
	public double money;
	//Time to germinate after being planted
	public int gTime;
	//Boolean tracking whether a plant is fertilized or not
	public boolean fertilized;
	//Health value of the Food
	public int health;
	//Expiry time of the Food, how long before it has to be thrown out
	public int expiry;
	//Boolean tracking whether the plant has been watered or not
	public boolean watered;
	//Agent who owns this Food
	public IAgent owner;
	
	
	/*Default constructor
	 * 
	 * Outside Parameters Referenced:
	 * avgCost - average cost of Food agents
	 * avgEng - average energy value of Food agents
	 * 
	 * Both of these parameters are set in the UserGlobalsAndPanelFactory.groovy class
	 */
	
	public Food(IAgent owner){
		NormalDistribution moneyrandom = new NormalDistribution(avgCost, 3);
		NormalDistribution energyrandom = new NormalDistribution(avgEng, 30);
		energy = (int) energyrandom.sample();
		money = ceiling(100 * moneyrandom.sample())/100;
		health = 10;
		expiry = 2000;
		gTime = 200;
		watered = false;
		this.owner = owner;
	}
	
	public Food(Food planted){
		energy = planted.energy;
		money = planted.money;
		gTime = planted.gTime;
		health = planted.health;
		expiry = planted.expiry;
		watered = false;
	}
	
}
