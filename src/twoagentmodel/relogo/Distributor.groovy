package twoagentmodel.relogo

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
	//where the driver is going
	public Person destination = null
	//how much time it takes to load the truck
	def loadingTime = 40
	def loadingTimeLeft = loadingTime
	def tick = 4
	
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
				tick--
				if(tick == 0){
					tick = 3
					def noFoodLeft = moveFood(destination)
					if(noFoodLeft){
						status = "driving"
						loadingTimeLeft = loadingTime
						getDestination()
					}
					loadingTimeLeft--
					workHoursLeft--
					if(loadingTimeLeft <= 0){
						status = "driving"
						loadingTimeLeft = loadingTime
						getDestination()
					}
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
	public boolean moveFood(Person destination){
		if(destination.getClass().equals(Retailer.class)){
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
