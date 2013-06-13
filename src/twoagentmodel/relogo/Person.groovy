package twoagentmodel.relogo


import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

//base class for all People agents
class Person extends BaseTurtle {
	//current energy of the agent, scale from 1-100
	private int energy;
	//current available funds of the agent
	private int money;
	
	//modifier for the agents energy, don't want to be able to set it outright
	public void addEnergy(int energy){
		this.energy += energy;
	}
	//returns an agents energy
	public int getEnergy(){
		return this.energy;
	}
	
	//modifier for the agents energy, similar to energy
	public void addMoney(int moeny){
		this.money += money;
	}
	//returns an agents energy
	public int getMoney(){
		return this.money;
	}
	
	//default constructor
	public Person(){
		energy = 100;
		money = 9999999;
	}
	
	
	public void buy(Person seller, List<Food> inventory){
		Food item = inventory.get(0)
		
		int cost = item.money;
		int energy = item.energy;
		
		if(cost > this.money){
			this.label = "Not enough money..."
			return
		}
		
		this.money -= cost;
		this.energy += energy;
		seller.addMoney(cost);
		inventory.remove(item);
	}
}
