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
	Person destination = null
	def status = "driving"
	def loadingTime = 10
	def loadingTimeLeft = loadingTime
	
	public Distributor(){
		workHours = 450;
		workHoursLeft = workHours;
	}
	
	def step(){
		if(workHoursLeft <= 0){
			status = "sleeping"
		}
		if(status == "driving"){
			face(destination)
			forward(0.5)
			if(distance(destination) < 0.5){
				status = "loading"
			}
			workHoursLeft--
		}
		else if(status == "loading"){
			moveFood(destination)
			loadingTimeLeft--
			workHoursLeft--
			if(loadingTimeLeft <= 0){
				status = "driving"
				loadingTimeLeft = loadingTime
				getDestination()
			}
		}
	}
	
	def getDestination(){
		if(destination == null || destination.getClass().equals(Retailer.class)){
			destination = minOneOf(producers()){this.distance(it)}
		}
		else{
			destination = minOneOf(retailers()){this.distance(it)}
		}
	}
	
	@Override
	public void nextDay(){
		workHoursLeft = workHours
		status = "driving"
	}
	
	def moveFood(Person destination){
		if(destination.getClass().equals(Retailer.class) && !food.empty){
			destination.buy(this)
		}
		else{
			buy(destination)
		}
	}
}
