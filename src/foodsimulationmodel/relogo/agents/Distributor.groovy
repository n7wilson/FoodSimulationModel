package foodsimulationmodel.relogo.agents

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;

import com.sun.org.apache.bcel.internal.generic.RETURN;


import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class Distributor extends Person {
	//Where the Distributor is going
	public Person destination = null
	//How much time it takes to load the truck
	def loadingTime = 40
	def loadingTimeLeft = loadingTime
	//How long it takes to load 1 item
	def itemLoadTime = 4
	def itemLoadTimeLeft = itemLoadTime
	
	public Distributor(){
		type = "Distributor"
		status = "driving"
		workHours = 450;
		workHoursLeft = workHours;
	}
	
	//Perform task for this tick
	def step(){
		//set the label of the Distributor to show it's current destination
		if(destination != null){
			label = status + " " + destination.getShape() + " :" + destination.getPxcor() + ", " + destination.getPycor() 
		}
		//if the Distributor is done work for the day then stop moving
		if(workHoursLeft <= 0){
			status = "sleeping"
		}
		switch(status){
			case "driving":
				face(destination)
				forward(speed)
				if(distance(destination) < speed){
					status = "loading"
					loadingTimeLeft = loadingTime
				}
				workHoursLeft--
				break
			case "loading":
				//transfer food, either to a Retailer or from a Producer
				loadingTimeLeft--
				itemLoadTimeLeft--
				workHoursLeft--
				if(itemLoadTimeLeft == 0){
					itemLoadTimeLeft = itemLoadTime
					def noFoodLeft = moveFood(destination)
					//if there is no food left to move then get next destination and start driving
					if(noFoodLeft){
						status = "driving"
						getDestination()
					}
				}
				//if the Distributor is done loading the food
				// then get next destination and start driving
				if(loadingTimeLeft <= 0){
					status = "driving"
					getDestination()
				}
				break
			case "sleeping":
				if(workHoursLeft > 0){
					status = "driving"
				}
				break
		}
	}
	
	//Find the next place the Distributor needs to go
	def getDestination(){
		//if they were just at a Retailer go to a Producer
		if(destination == null || destination.getClass().equals(Retailer.class)){
			destination = maxOneOf(producers()){it.food.size}
		}
		//and vice versa
		else{
			destination = minOneOf(retailers()){it.food.size}
		}
	}
	
	//Move Food between the Distributor and its destination
	//Sell to a Retailer or buy from a Producer
	public boolean moveFood(Person destination){
		if(destination.getClass().equals(Retailer.class)){
			//check if there is any food left
			if(!destination.food.empty){
				if(this.food.size == 0){
					return true
				}
				else{
					destination.buy(this)
					return false
				}

			}
		}
		else{
			//check if there is any food left
			if(destination.food.size == 0){
					return true
			}
			else{
				buy(destination)
				return false
			}
		}
	}
}
