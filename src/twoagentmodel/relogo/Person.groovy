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
	//current energy of the agent
	private int energy;
	//modifier for the agents energy, don't want to be able to set it outright
	public void addEnergy(int energy){
		this.energy += energy;
	}
	//returns an agents energy
	public int getEnergy(){
		return this.energy;
	}
	
	//current available funds of the agent
	private double money;
	//modifier for the agents energy, similar to energy
	public void addMoney(double moeny){
		this.money += money;
	}
	//returns an agents energy
	public double getMoney(){
		return this.money;
	}
	
	//time that the agent spends each day working, read only value
	private int workHours;
	public int getWorkHours(){
		return this.workHours;
	}
	//time that the agent has left to work for this day
	private int workHoursLeft;
	public int getWorkHoursLeft(){
		return this.workHoursLeft;
	}
	public void setWorkHoursLeft(int hours){
		this.workHoursLeft = hours
	}
	
	//default constructor
	public Person(){
		energy = 1000;
		money = 9999999;
		workHours = 20;
		workHoursLeft = workHours;
	}
	
	
	public void buy(Person seller, List<Food> inventory){
		Food item = chooseItem(inventory)
		
		double cost = item.money;
		int energy = item.energy;
		
		if(cost > this.money){
			this.label = "Not enough money..."
			return
		}
		
		this.money -= cost;
		this.energy += energy;
		seller.addMoney(cost);
		inventory.remove(item);
		inventory.add(new Food())
		
		seller.label = "Last item sold: Food -> cost = " + cost + " energy = " + energy
	}
	
	public Food chooseItem(List<Food> inventory){
		long seed = System.nanoTime();
		Collections.shuffle(inventory, new Random(seed));
		Food item = inventory.get(0)
		for(int i = 0; i < 10; i++){
			def nextitem = inventory.get(i)
			if(nextitem.money < item.money){
				item = nextitem;
			}
		}
		return item;
	}
	
	public void nextDay(){
		this.workHoursLeft += this.workHours;
	}
}
