package foodsimulationmodel.agents;

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.vividsolutions.jts.geom.Coordinate;

import foodsimulationmodel.environment.ContextManager;
import foodsimulationmodel.pathmapping.Route;


import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

public class Distributor extends Person {
	//Where the Distributor is going
	public Person destination = null;
	//How much time it takes to load the truck
	int loadingTime = 40;
	int loadingTimeLeft = loadingTime;
	//How long it takes to load 1 item
	int itemLoadTime = 4;
	int itemLoadTimeLeft = itemLoadTime;
	Route route = null;
	
	public Distributor(){
		type = "Distributor";
		status = Person.Status.DRIVING;
		workHours = 450;
		workHoursLeft = workHours;
		getDestination();
	}
	
	@Override
	public void step() throws Exception {
		//if the Distributor is done work for the day then stop moving
		if(workHoursLeft <= 0){
			status = Person.Status.SLEEPING;
		}
		switch(status){
			case DRIVING:
				route.travel();
				if(distance(destination) < speed){
					status = Person.Status.LOADING;
					loadingTimeLeft = loadingTime;
				}
				workHoursLeft--;
				break;
			case LOADING:
				//transfer food, either to a Retailer or from a Producer
				loadingTimeLeft--;
				itemLoadTimeLeft--;
				workHoursLeft--;
				if(itemLoadTimeLeft == 0){
					itemLoadTimeLeft = itemLoadTime;
					boolean noFoodLeft = moveFood(destination);
					//if there is no food left to move then get next destination and start driving
					if(noFoodLeft){
						status = Person.Status.DRIVING;
						getDestination();
					}
				}
				//if the Distributor is done loading the food
				// then get next destination and start driving
				if(loadingTimeLeft <= 0){
					status = Person.Status.DRIVING;
					getDestination();
				}
				break;
			case SLEEPING:
				if(workHoursLeft > 0){
					status = Person.Status.DRIVING;
				}
				break;
		}
	}
	
	//Find the next place the Distributor needs to go
	public void getDestination(){
		//if they were just at a Retailer go to a Producer
		if(destination == null || destination.getClass().equals(Retailer.class)){
			destination = ContextManager.getProducerWithMostFood();
		}
		//and vice versa
		else{
			destination = ContextManager.getRetailerWithLeastFood();
		}
		route = new Route(this, destination.getHome(), destination);
	}
	
	//Move Food between the Distributor and its destination
	//Sell to a Retailer or buy from a Producer
	public boolean moveFood(Person destination){
		if(destination.getClass().equals(Retailer.class)){
			//check if there is any food left
				if(this.food.size() == 0){
					return true;
				}
				else{
					destination.buy(this);
					return false;
				}
		}
		else{
			//check if there is any food left
			if(destination.food.size() == 0){
					return true;
			}
			else{
				buy(destination);
				return false;
			}
		}
	}
}
