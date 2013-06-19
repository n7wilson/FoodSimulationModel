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
	protected int energy;
	//modifier for the agents energy, don't want to be able to set it outright
	public void addEnergy(int energy){
		this.energy += energy;
	}
	//returns an agents energy
	public int getEnergy(){
		return this.energy;
	}
	
	//current available funds of the agent
	protected double money;
	//modifier for the agents energy, similar to energy
	public void addMoney(double money){
		this.money += money;
	}
	//returns an agents energy
	public double getMoney(){
		return this.money;
	}
	
	//time that the agent spends each day working
	protected int workHours;
	public int getWorkHours(){
		return this.workHours;
	}
	//time that the agent has left to work for this day
	protected int workHoursLeft;
	public int getWorkHoursLeft(){
		return this.workHoursLeft;
	}
	public void setWorkHoursLeft(int hours){
		this.workHoursLeft = hours
	}
	
	//food that the agent is carrying
	public List<Food> food;
	
	//default constructor
	public Person(){
		food = new ArrayList<Food>()
		energy = 1000;
		money = 9999999;
		workHours = 240;
		workHoursLeft = workHours;
	}
	
	
	public void buy(Person seller){
		Food item = chooseItem(seller.food)
		
		double cost = item.money;
		int energy = item.energy;
		
		if(cost > this.money){
			this.label = "Not enough money..."
			return
		}
		
		this.addMoney(-cost)
		seller.addMoney(cost);
		this.food.add(item);
		seller.food.remove(item);
		if(seller.getClass().equals(Retailer.class) || seller.getClass().equals(Producer.class))
		seller.label = "Last item sold: Food -> cost = " + cost + " energy = " + energy
		
	}
	
	public void eat(){
		Food item = food.get(0)
		this.addEnergy(item.energy)
		food.remove(item)
	}
	
	public Food chooseItem(List<Food> inventory){
		long seed = System.nanoTime();
		Collections.shuffle(inventory, new Random(seed));
		Food item = inventory.get(0)
		int numItems = Math.min(10, inventory.size())
		for(int i = 0; i < numItems; i++){
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
