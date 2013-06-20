package twoagentmodel.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class Producer extends Person {
	//plants that are planted and growing
	public List<Food> planted
	//plants that have grown but haven't been harvested yet
	public List<Food> ready
	//job that the Producer was last doing. Used to revert to previous
	//job after loading a Distributor
	def preStatus = "harvesting"
	
	public Producer(){
		planted = new ArrayList<Food>()
		ready = new ArrayList<Food>()
		status = "planting"
		//initialize the farms with harvested and planted food
		for(int i = 0; i < 500; i++){
			food.add(new Food())
		}
		for(int i = 0; i < 20; i++){
			planted.add(new Food())
		}
	}
	
	def work(){
		//list of distributors that are at the farm
		//TODO: Fix possible issues when farms are close together since this
		//list depends on distance. Tried to add in error checking for this
		//but ran into problems
		def dest = distributors().findAll { this.distance(it) < 0.5 }
		switch(status){
			case "harvesting": 
				food.add(new Food(ready.get(0)))
				ready.remove(0)
				break
			case "planting": 
				planted.add(new Food())
				break
		}
		if(!dest.empty){
			status = "loading"
			label = "loading"
		}
		else if(ready.empty){
			setJob("planting")
			
		}
		else if(ready.size > 10){
			setJob("harvesting")
		}
		else{
			status = preStatus
			label = preStatus
		}
	}
	
	//sets the current job that the Producer is doing
	def setJob(String job){
		label = job
		status = job
		preStatus = job
	}
	
	//function called each tick, makes plants grow
	def growPlants(){
		def toRemove = new ArrayList<Food>()
		for(Food plant: planted){
			plant.gTime--
			if(plant.gTime <= 0){
				ready.add(plant)
				toRemove.add(plant)
			}
		}
		for(Food plant: toRemove){
			planted.remove(plant)
		}
	}
}
