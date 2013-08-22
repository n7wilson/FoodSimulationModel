package foodsimulationmodel.relogo.agents;

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import foodsimulationmodel.relogo.environment.ContextManager;
import foodsimulationmodel.relogo.food.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

//TODO: figure out systematic way to restrict number of planted seeds based on acres
//TODO: pesticide use to change gTime (set initial gTime higher, let pesticide use reduce gTime)

public class Producer extends Person {
	//Plants that are planted and growing
	public List<Food> planted;
	//Plants that have grown but haven't been harvested yet
	public List<Food> ready;
	//Job that the Producer was last doing. Used to revert to previous
	//Job after loading a Distributor
	Status preStatus = Status.PLANTING;
	
	//Size of farm
	public int acres;
	
	//Array of seed types
	public String[] seedTypes = { "Meat", "Produce", "Junk" };
	//List of remaining seed types producer does not have
	public List<String> seedCatalogue;
	//List of seeds producer can plant
	public List<String> seedList;
	
	//Amount of time that the Producer agent will spend on each job (minimum)
	private int jobTime;
	private int jobTimeLeft;
	
	//Boolean used while Producer is maintaining plants.
	//True if the Producer has already watered and fertilized all the plants
	//False otherwise
	boolean noJob;
	
	public Producer(){
		type = "Producer";
		planted = new ArrayList<Food>();
		ready = new ArrayList<Food>();
		status = Status.PLANTING;
		noJob = false;
		jobTime = 30;
		jobTimeLeft = jobTime;
		acres = 200;
		seedCatalogue = new ArrayList<String>();
		seedList = new ArrayList<String>();
		//initialize the farms with harvested and planted food
		for(int i = 0; i < 500; i++){
			food.add(new Food(this));
		}
		for(int i = 0; i < 20; i++){
			planted.add(new Food(this));
		}
		for(int i = 0; i < seedTypes.length; i++) {
			seedCatalogue.add(seedTypes[i]);
		}
		//start off with one random type of Seed
		addType(null);
	}
	
	@Override
	public void step() throws Exception {
		//list of distributors that are at the farm
		//TODO: Fix possible issues when farms are close together since this
		//list depends on distance. Tried to add in error checking for this
		//but ran into problems
		boolean distAreHere = ContextManager.getAgentGeometry(this).distance(ContextManager.getAgentGeometry(ContextManager.getClosestAgent(this, Distributor.class))) < 0.5;
		switch(status){
			case HARVESTING:
				//move one Food item from to the Producer's inventory 
				food.add(ready.get(0));
				ready.remove(0);
				jobTimeLeft--;
				break;
			case PLANTING: 
				//plant one new Food item
				addSeed();
				jobTimeLeft--;
				break;
			case MAINTAINING:
				noJob = true;
				for(Food plant: planted){
					if(!plant.watered){
						plant.watered = true;
						noJob = false;
						break;
					}
				}
				if(noJob){
					for(Food plant: planted){
						if(!plant.fertilized){
							plant.fertilized = true;
							noJob = false;
							break;
						}
					}
				}
				if(noJob){
					jobTimeLeft = 0;
				}
		}
		//if there is a Distributor at the farm then load Food
		if(distAreHere){
			status = Status.LOADING;
		}
		else if(jobTimeLeft == 0){
			//otherwise if there are enough plants planted and there are still plants to
			//water and fertilize then maintain the plants
			if(planted.size() > 10 && !noJob){
				jobTimeLeft = jobTime;
				status = Status.MAINTAINING;
			}
			//if there are no more plants to water or fertilize then plant more
			else if(ready.isEmpty() || noJob){
				jobTimeLeft = jobTime;
				setJob(Status.PLANTING);
				noJob = false;
			}
			//if there are more enough plants that need to be harvested then harvest them
			else if(ready.size() > 10){
				jobTimeLeft = jobTime;
				setJob(Status.HARVESTING);
			}	
		}
		//if the Producer just finished loading go back to what the Producer was doing before
		else if(status == Status.LOADING){
			status = preStatus;
		}
	}
	
	//Sets the Producer's current task
	private void setJob(Status job){
		status = job;
		preStatus = job;
	}
	
	//Function called each tick, makes plants grow
	public void growPlants(){
		//list of plants that are ready to be picked
		ArrayList<Food> toRemove = new ArrayList<Food>();
		for(Food plant: planted){
			//grow plants, if fertilized and watered then grow faster
			if(plant.fertilized){
				plant.gTime--;
				plant.fertilized = false;
			}
			if(plant.watered){
				plant.gTime--;
			}
			plant.gTime--;
			//add the plant to the list of plants that are ready if it has finished growing
			if(plant.gTime <= 0){
				toRemove.add(plant);
			}
		}
		//for each plant that is ready to be picked move it to the "ready" list
		for(Food plant: toRemove){
			ready.add(plant);
			planted.remove(plant);
		}
	}
	
	// Chooses a random class of food to plant based on seedList
	public void addSeed() {
		Random random = new Random();
		int i = random.nextInt(seedList.size());
		String newCrop = seedList.get(i);
		
		if (newCrop.equals("Junk")) {
			planted.add(new Junk(this));
		}
		else if (newCrop.equals("Meat")) {
			planted.add(new Meat(this));
		}
		else {
			planted.add(new Produce(this));
		}
	}
	
	// Chooses a new type of seed (random or otherwise) to add to seedList
	public void addType(String newSeed) {
		// must have new type of seed to add
		if (seedCatalogue.size() < 1) {
			return;
		}
		// no preference indicates random assignment of new type of seed
		else if (newSeed == null) {
			Random random = new Random();
			int i = random.nextInt(seedCatalogue.size());
			newSeed = seedCatalogue.get(i);
			addType(newSeed);
		}
		else if (newSeed.equals("Junk")) {
			seedList.add(newSeed);
			seedCatalogue.remove(newSeed);
		}
		else if (newSeed.equals("Meat")) {
			seedList.add(newSeed);
			seedCatalogue.remove(newSeed);
		}
		else if (newSeed.equals("Produce")) {
			seedList.add(newSeed);
			seedCatalogue.remove(newSeed);
		}
	}
	
	@Override
	public void nextDay(boolean windy){
		//add the Person's daily work hours to the hours they have left to work
		this.workHoursLeft += this.workHours;
		for(Food f: planted){
			f.watered = false;
		}
		if(windy && !planted.isEmpty()){
			long seed = System.nanoTime();
			Collections.shuffle(planted, new Random(seed));
			planted.remove(0);
		}
	}
}
