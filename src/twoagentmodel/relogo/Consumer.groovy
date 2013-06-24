package twoagentmodel.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;

import java.util.List;

import repast.simphony.relogo.AgentSet
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class Consumer extends Person {
	def generator = new Random()
	def store = null
	def origin = null
	def work = null
	def hungerMin = 500
	def salary = 30
	
	public Consumer(){
		status = "working"
	}

	def setStore(){
		store = minOneOf(retailers()){this.distance(it)}
	}
	
	def setOrigin(){
		origin = patchHere()
	}
	
	def setWork(){
		work = work(0)
	}
	
	
	
	def step(){
		this.addEnergy(-1)
		label = this.getEnergy()
		
		//if the agent needs food this takes priority
		if(this.getEnergy() < hungerMin){
			if(status != "hungry"){
				status = "hungry"
				def filteredRet = retailers().with({it.food.size != 0})
				store = minOneOf(filteredRet){this.distance(it)}
			}
		}
		//if agent still needs to work this is next priority
		else if(workHoursLeft > 0){
			status = "working"
		}
		
		// if agent is unhealthy, needs to change lifestyle
		if(this.health <= 10) {
			setPref()
		}		
		
		switch(status){
			case "hungry":
				//if the agent doesn't have any food with them then go to the store
				if(food.empty){
					if(notAtLocation(store)){
						moveTowards(store) 
					}
					else{
						this.buy(store)
						store = null
					}
				}
				//otherwise eat some of the food the agent has
				else{
					eat()
					status = "normal"
				}
				break
			case "working":
				//go to work if not there already
				if(notAtLocation(work)){
					moveTowards(work)
				}
				//work and get paid
				//TODO: possibly change pay to mirror pay in real life (given in bulk after a time period)
				else{
					workHoursLeft--
					if(workHoursLeft <= 0){
						status = "normal"
					}
					//label = "working, energy: " + this.getEnergy()
					this.addMoney(salary)
				}
				break
			default:
				if(distance(origin) > 0.5){
					face(origin)
					forward(0.5)
				}
				break
		}
	}
	
	@Override
	// currently consumers are starving if they can't afford
	// the cheapest food of their preference.
	public Food chooseItem(List<Food> inventory) {		
		long seed = System.nanoTime();
		Collections.shuffle(inventory, new Random(seed));
		Food item = inventory.get(0)
		int numItems = Math.min(10, inventory.size())
		
		if (this.pref == "Meat") {
			for(int i = 0; i < numItems; i++) {
				def nextitem = inventory.get(i)
				if (nextitem.getClass().equals(Meat)) {
					if (!item.getClass().equals(Meat)) {
						item = nextitem;
					}
					else if (nextitem.money < item.money) {
						item = nextitem;
					}
				}
			}
		}
		
		else if (this.pref == "Produce") {
			for(int i = 0; i < numItems; i++) {
				def nextitem = inventory.get(i)
				if (nextitem.getClass().equals(Produce)) {
					if (!item.getClass().equals(Produce)) {
						item = nextitem;
					}
					else if (nextitem.money < item.money) {
						item = nextitem;
					}
				}
			}
		}
		
		else if (this.pref == "junk") {
			for(int i = 0; i < numItems; i++) {
				def nextitem = inventory.get(i)
				if (nextitem.getClass().equals(Junk)) {
					if (!item.getClass().equals(Junk)) {
						item = nextitem;
					}
					else if (nextitem.money < item.money) {
						item = nextitem;
					}
				}
			}
		}
		
		else {
			for(int i = 0; i < numItems; i++){
				def nextitem = inventory.get(i)
				if(nextitem.money < item.money){
					item = nextitem;
				}
			}
		}
		return item;
	}
	
	def notAtLocation(BaseTurtle agent){
		return distance(agent) > 0.5
	}
	
	def moveTowards(BaseTurtle agent){
		face(agent)
		forward(0.5)
	}
}
