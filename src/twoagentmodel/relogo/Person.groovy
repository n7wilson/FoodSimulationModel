package twoagentmodel.relogo


import static repast.simphony.relogo.Utility.*
import static repast.simphony.relogo.UtilityG.*
import repast.simphony.relogo.BasePatch
import repast.simphony.relogo.BaseTurtle
import repast.simphony.relogo.Plural
import repast.simphony.relogo.Stop
import repast.simphony.relogo.Utility
import repast.simphony.relogo.UtilityG

//base class for all People agents
class Person extends BaseTurtle {
	//agents status, used to determine what the agent should be doing, read only
	protected String status;
	public String getStatus(){
		return status;
	}
	
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
	
	// health of agent, from 0 - 100
	public int health;
	
	public void addHealth(int health) {
		health += health;
		if (health > 100) {
			health = 100;
		}
		else if (health < 0) {
			health = 0;
		}
	}
	
	public int getHealth() {
		return this.health;
	}
	
	public String pref;
	
	private String[] preferences = [ "none", "meat", "produce", "junk" ];
	
	public void setPref() {
		Random random = new Random();
		int i = random.nextInt(preferences.size());
		if (i == 0) {
			pref = "meat";
		}
		else if (i == 1) {
			pref = "produce";
		}
		else if (i == 2) {
			pref = "junk";
		}
		else {
			pref = "none";
		}
	}
	
	public String getPref() {
		return this.pref;
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
	
	//function to have food move from agent to agent
	public void buy(Person seller){
		//find food that the agent wants to purchase
		Food item = chooseItem(seller.food)
		
		double cost = item.money;
		int energy = item.energy;
		
		//error check in case agent doesn't have enough money
		if(cost > this.money){
			this.label = "Not enough money..."
			return
		}
		
		//have money and food change hands
		this.addMoney(-cost)
		seller.addMoney(cost);
		this.food.add(item);
		seller.food.remove(item);
//		if(seller.getClass().equals(Retailer.class) || seller.getClass().equals(Producer.class)){
//		seller.label = "Last item sold: Food -> cost = " + cost + " energy = " + energy}
		
	}
	
	//consume an item of food
	public void eat(){
		Food item = food.get(0)
		this.addEnergy(item.energy)
		this.addHealth(item.health)
		food.remove(item)
	}
	
	// removes expired food from person's food stash
	public void updateFood() {
		List<Food> toRemove = new ArrayList<Food>();
		for(Food item: food) {
			item.expiry--
			if (item.expiry <= 0) {
				toRemove.add(item);
			}
		}
		for(Food item: toRemove) {
			food.remove(item);
		}
	}
	
	//default way of choosing item
	//look at up to 10 random foods from the seller and choose the cheapest one
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
	
	//sets all the agent properties for a new day
	public void nextDay(){
		this.workHoursLeft += this.workHours;
	}
}
