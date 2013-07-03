package foodsimulationmodel.relogo.agents

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import foodsimulationmodel.relogo.food.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class Producer extends Person {
	//Plants that are planted and growing
	public List<Food> planted
	//Plants that have grown but haven't been harvested yet
	public List<Food> ready
	//Job that the Producer was last doing. Used to revert to previous
	//Job after loading a Distributor
	def preStatus = "planting"
	
	//Amount of time that the Producer agent will spend on each job (minimum)
	private int jobTime
	private int jobTimeLeft
	
	//Boolean used while Producer is maintaining plants.
	//True if the Producer has already watered and fertilized all the plants
	//False otherwise
	def noJob
	
	public Producer(){
		planted = new ArrayList<Food>()
		ready = new ArrayList<Food>()
		status = "planting"
		noJob = false
		jobTime = 30
		jobTimeLeft = jobTime
		//initialize the farms with harvested and planted food
		for(int i = 0; i < 500; i++){
			food.add(new Food())
		}
		for(int i = 0; i < 20; i++){
			planted.add(new Food())
		}
	}
	
	//Perform task for this tick
	def step(){
		//list of distributors that are at the farm
		//TODO: Fix possible issues when farms are close together since this
		//list depends on distance. Tried to add in error checking for this
		//but ran into problems
		def dest = distributors().findAll { this.distance(it) < 0.5 }
		switch(status){
			case "harvesting":
				//move one Food item from to the Producer's inventory 
				food.add(new Food(ready.get(0)))
				ready.remove(0)
				jobTimeLeft--
				break
			case "planting": 
				//plant one new Food item
				planted.add(new Food())
				jobTimeLeft--
				break
			case "maintaining":
				noJob = true
				for(Food plant: planted){
					if(!plant.watered){
						plant.watered = true
						noJob = false
						label = "watering"
						break
					}
				}
				if(noJob){
					for(Food plant: planted){
						if(!plant.fertilized){
							plant.fertilized = true
							noJob = false
							label = "fertilizing"
							break
						}
					}
				}
				if(noJob){
					jobTimeLeft = 0
				}
		}
		//if there is a Distributor at the farm then load Food
		if(!dest.empty){
			status = "loading"
			label = "loading"
		}
		else if(jobTimeLeft == 0){
			//otherwise if there are enough plants planted and there are still plants to
			//water and fertilize then maintain the plants
			if(planted.size > 10 && !noJob){
				jobTimeLeft = jobTime
				status = "maintaining"
			}
			//if there are no more plants to water or fertilize then plant more
			else if(ready.empty || noJob){
				jobTimeLeft = jobTime
				setJob("planting")
				noJob = false
			}
			//if there are more enough plants that need to be harvested then harvest them
			else if(ready.size > 10){
				jobTimeLeft = jobTime
				setJob("harvesting")
			}	
		}
		//if the Producer just finished loading go back to what the Producer was doing before
		else if(status == "loading"){
			status = preStatus
			label = preStatus
		}
	}
	
	//Sets the Producer's current task
	def setJob(String job){
		label = job
		status = job
		preStatus = job
	}
	
	//Function called each tick, makes plants grow
	def growPlants(){
		//list of plants that are ready to be picked
		def toRemove = new ArrayList<Food>()
		for(Food plant: planted){
			//grow plants, if fertilized and watered then grow faster
			if(plant.fertilized){
				plant.gTime--
				plant.fertilized = false
			}
			if(plant.watered){
				plant.gTime--
			}
			plant.gTime--
			//add the plant to the list of plants that are ready if it has finished growing
			if(plant.gTime <= 0){
				toRemove.add(plant)
			}
		}
		//for each plant that is ready to be picked move it to the "ready" list
		for(Food plant: toRemove){
			ready.add(plant)
			planted.remove(plant)
		}
	}
	
	// Chooses a random class of food to plant
	def addSeed() {
		Random random = new Random();
		int i = random.nextInt(3);
		
		if (i == 0) {
			planted.add(new Junk());
		}
		else if (i == 1) {
			planted.add(new Meat());
		}
		else {
			planted.add(new Produce());
		}
	}
}
