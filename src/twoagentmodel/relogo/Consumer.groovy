package twoagentmodel.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;

import java.util.List;

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
		//label = this.getEnergy()
		
		//if the agent needs food this takes priority
		if(this.getEnergy() < hungerMin){
			status = "hungry"
			store = minOneOf(retailers()){this.distance(it)}
		}
		//if agent still needs to work this is next priority
		else if(workHoursLeft > 0){
			status = "working"
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
	
	def notAtLocation(BaseTurtle agent){
		return distance(agent) > 0.5
	}
	
	def moveTowards(BaseTurtle agent){
		face(agent)
		forward(0.5)
	}
}
