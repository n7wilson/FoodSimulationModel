package foodsimulationmodel.agents;

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import foodsimulationmodel.environment.ContextManager;
import foodsimulationmodel.environment.Work;
import foodsimulationmodel.food.*;
import foodsimulationmodel.pathmapping.Route;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import repast.simphony.relogo.AgentSet;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Patch;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

public class Consumer extends Person {
	Random generator = new Random();
	//Reference for next store the Consumer will buy food from
	Retailer store = null;
	//Consumer's house
	Home origin = null;
	//Consumer's work
	Work work = null;
	//Threshold at which Consumer gets hungry and must eat food
	int hungerMin = 500;
	//Money made from working (per tick)
	int salary = 30;
	//Route the Consumer is following
	Route route = null;
	
	public Consumer(){
		type = "Consumer";
		status = Status.WORKING;
	}

	public void setStore(){
		store = (Retailer)ContextManager.getClosestAgent(this, Retailer.class);
	}
	
	
	//Default way of setting the Consumer's work location.
	//If more than one work location is added then should be modified
	public void setWork(){
		work = (Work)ContextManager.getClosestAgent(this, Work.class);
		route = new Route(this, work.getHome(), work);
	}
	
	@Override
	public void setHome(double x, double y) {
		home = new Coordinate(x,y);
		origin = new Home();
		origin.setHome(x, y);
		origin.setResident(this);
		ContextManager.addAgentToContext(origin);
		GeometryFactory geoFac = new GeometryFactory();
		ContextManager.moveAgent(origin, geoFac.createPoint(home));
	}
	
	
	@Override
	public void step() throws Exception {
		this.addEnergy(-1);
		
		//if the Consumer needs food this takes priority
		if(this.getEnergy() < hungerMin){
			if(status != Status.HUNGRY){
				status = Status.HUNGRY;
				store = (Retailer)ContextManager.getClosestAgent(this, Retailer.class);
				route = new Route(this, ContextManager.getAgentGeometry(store).getCoordinate(),store);
			}
		}
		//if the Consumer still needs to work this is next priority
		else if(workHoursLeft > 0 && status != Status.WORKING){
			status = Status.WORKING;
			route = new Route(this, ContextManager.getAgentGeometry(work).getCoordinate(), work);
		}
		
		//if the Consumer is unhealthy, needs to change lifestyle
		//may modify how lifestyle is changed
		//speed is reduce when very unhealthy
		if(this.health <= 10) {
			setPref();
			speed = 0.25;
		}
		else{
			speed = 0.5;
		}		
		
		//decide what to do based on the Consumer's status
		switch(status){
			case HUNGRY:
				//if the Consumer doesn't have any food with them then go to the store
				if(food.isEmpty()){
					if(notAtLocation(store)){
						move();
					}
					//if the Consumer is at the store then buy an item of food based on preferences
					else{
						this.buy(store);
						store = null;
					}
				}
				//otherwise eat some of the food the agent has
				else{
					eat();
					status = Status.NORMAL;
					route = new Route(this, origin.getHome(),origin);
				}
				break;
			case WORKING:
				//go to work if not there already
				if(notAtLocation(work)){
					move();
				}
				//work and get paid
				//TODO: possibly change pay to mirror pay in real life (given in bulk after a time period)
				else{
					workHoursLeft--;
					addHealth(-2);
					if(workHoursLeft <= 0){
						status = Status.NORMAL;
					route = new Route(this, origin.getHome(),origin);
					}
					//label = "working, energy: " + this.getEnergy()
					this.addMoney(salary);
				}
				break;
			default:
				//if the Consumer doesn't have anything else to do go home
				if(distance(origin) > speed){
					move();
				}
				else{
					addHealth(1);
				}
				break;
		}
	}
	
	@Override
	// currently consumers are starving if they can't afford
	// the cheapest food of their preference.
	public Food chooseItem(List<Food> inventory) {		
		long seed = System.nanoTime();
		//shuffle the inventory of the seller so the Consumers aren't looking
		//at the same items each time
		Collections.shuffle(inventory, new Random(seed));
		Food item = inventory.get(0);
		//number of items to look at
		int numItems = Math.min(10, inventory.size());
		
		for(int i = 0; i < numItems; i++) {
			Food nextitem = inventory.get(i);
			if (nextitem.getClass().getSimpleName() == this.pref) {
				if (item.getClass().getSimpleName() != this.pref) {
					item = nextitem;
				}
				else if (nextitem.money < item.money) {
					item = nextitem;
				}
			}
			else if(item.getClass().getSimpleName() != this.pref && nextitem.money < item.money){
				item = nextitem;
			}
		}
		return item;
	}
	
	private boolean notAtLocation(IAgent agent){
		return distance(agent) > speed;
	}
	
	private void move() throws Exception{
		route.travel();
	}
}
