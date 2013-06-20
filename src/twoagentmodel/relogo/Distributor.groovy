package twoagentmodel.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class Distributor extends Person {
	//where the driver is going
	public Person destination = null
	//how much time it takes to load the truck
	def loadingTime = 10
	def loadingTimeLeft = loadingTime
	
	public Distributor(){
		status = "driving"
		workHours = 450;
		workHoursLeft = workHours;
	}
	
	def step(){
		if(destination != null){
			label = status + " " + destination.getShape() + " :" + destination.getPxcor() + ", " + destination.getPycor() 
		}
		//if the driver is done work for the day then stop moving
		if(workHoursLeft <= 0){
			status = "sleeping"
		}
		switch(status){
			case "driving":
				face(destination)
				forward(0.5)
				if(distance(destination) < 0.5){
					status = "loading"
				}
				workHoursLeft--
				break
			case "loading":
				//transfer food 
				//either to a Retailer or from a Producer
				moveFood(destination)
				loadingTimeLeft--
				workHoursLeft--
				if(loadingTimeLeft <= 0){
					status = "driving"
					loadingTimeLeft = loadingTime
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
	
	//find the next place the Distributor needs to go
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
	
	//moves food between the Distributor and its destination
	//sell to a Retailer or buy from a Distributor
	def moveFood(Person destination){
		if(destination.getClass().equals(Retailer.class)){
			if(!destination.food.empty){
				destination.buy(this)
			}
		}
		else{
			buy(destination)
		}
	}
}
