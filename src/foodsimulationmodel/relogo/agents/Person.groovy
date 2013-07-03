package foodsimulationmodel.relogo.agents


import static repast.simphony.relogo.Utility.*
import static repast.simphony.relogo.UtilityG.*
import foodsimulationmodel.relogo.food.*
import repast.simphony.relogo.BasePatch
import repast.simphony.relogo.BaseTurtle
import repast.simphony.relogo.Plural
import repast.simphony.relogo.Stop
import repast.simphony.relogo.Utility
import repast.simphony.relogo.UtilityG

//Base class for all People agents
class Person extends BaseTurtle {
	//Person's status, used to determine what the Person should be doing, read only
	protected String status;
	public String getStatus(){
		return status;
	}
	
	//Current energy of the Person
	protected int energy;
	//Modifier for the agents energy, don't want to be able to set it outright
	public void addEnergy(int energy){
		this.energy += energy;
	}
	public int getEnergy(){
		return this.energy;
	}
	
	// Health of Person, from 0 - 100
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
	
	//Food preferences
	public String pref;
	//List of Food preferences
	private String[] preferences = [ "None", "Meat", "Produce", "Junk" ];
	
	/*Set a Person's food preferences
	 * Right now can select one preference and adjust the percentage of people who have that preference
	 * The people not assigned the selected preference are randomly assigned one of the other preferences
	 * Outside Parameters Referenced:
	 * percent - percent of people who are assigned the selected preference
	 * choice - the selected choice
	 * 
	 * Both of these parameters are set in the UserGlobalsAndPanelFactory.groovy class
	 */
	public void setPref() {
		//random number from 0-1 used to assign the Person's preference
		double rn = Math.random()
		//the percentage of people who have each preference besides the selected preference
		int interval = (1 - percent)/(preferences.size() - 1)
		List<String> prefsLeft = new ArrayList<String>()
		//create a list of the preferences that aren't the selected preference
		for(String p:preferences){
			if(p != choice){
				prefsLeft.add(p)
			}
		}
		if(rn < percent){
			pref = choice
		}
		else{
			for(int i = 0; i < preferences.size(); i++){
				if(rn < percent + (i + 1)*interval){
					pref = prefsLeft.get(i)
					break
				}
			}
		}
	}
	
	public String getPref() {
		return this.pref;
	}
	
	//Current available funds of the Person
	protected double money;
	//Modifier for the agents energy, similar to energy
	public void addMoney(double money){
		this.money += money;
	}
	//Returns the Person's energy
	public double getMoney(){
		return this.money;
	}
	
	//Time that the Person spends each day working
	protected int workHours;
	public int getWorkHours(){
		return this.workHours;
	}
	//Time that the Person has left to work for this day
	protected int workHoursLeft;
	public int getWorkHoursLeft(){
		return this.workHoursLeft;
	}
	public void setWorkHoursLeft(int hours){
		this.workHoursLeft = hours
	}
	
	//Food that the Person is carrying
	public List<Food> food;
	
	//Default constructor
	//Note: The value for workHours has been adjusted to match the number of hours in a day.
	//		If it is modified the model may not work properly
	public Person(){
		food = new ArrayList<Food>()
		energy = 1000;
		//TODO: implement money accurately
		money = 9999999;
		workHours = 240;
		workHoursLeft = workHours;
	}
	
	//Function to have food move from Person to Person
	public void buy(Person seller){
		//find food that the agent wants to purchase
		Food item = chooseItem(seller.food)
		
		if(item == null){
			return
		}
		
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
		
		//label the seller with the last item sold if the seller is a Retailer
		if(seller.getClass().equals(Retailer.class)){
		seller.label = "Last item sold: Food -> cost = " + cost + " energy = " + energy}
		
	}
	
	//Consume an item of Food
	public void eat(){
		Food item = food.get(0)
		this.addEnergy(item.energy)
		this.addHealth(item.health)
		food.remove(item)
	}
	
	//Removes expired Food from person's food stash
	public void updateFood() {
		List<Food> toRemove = new ArrayList<Food>();
		//get list of Food that need to be removed
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
	
	//Default way of choosing item
	//Look at up to 10 random foods from the seller and choose the cheapest one
	public Food chooseItem(List<Food> inventory){
		if(inventory.size == 0){
			return null
		}
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
	
	//Sets all the agent properties for a new day
	public void nextDay(boolean windy){
		//add the Person's daily work hours to the hours they have left to work
		this.workHoursLeft += this.workHours;
		if(this.class == Producer.class){
			for(Food f:planted){
				f.watered = false
			}
			if(windy && !planted.isEmpty()){
				long seed = System.nanoTime();
				Collections.shuffle(planted, new Random(seed));
				planted.remove(0)
			}
		}
	}
}
