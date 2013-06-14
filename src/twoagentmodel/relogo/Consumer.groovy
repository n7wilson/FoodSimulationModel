package twoagentmodel.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class Consumer extends Person {
	
	def status = "hungry"
	def generator = new Random()
	def store = null
	def origin = null
	def work = null
	def hungerMin = 500 - hungerTime
	def salary = 30

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
		if(status == "hungry"){
			//label = "Hungry!";
			if(notAtLocation(store)){
				moveTowards(store) 
			}
			else{
				status = "normal"
				this.buy(store, store.inventory)
			}
		}
		else if(status == "working"){
			if(notAtLocation(work)){
				moveTowards(work)
			}
			else{
				workHoursLeft--
				if(workHoursLeft <= 0){
					status = "normal"
				}
				label = "working, energy: " + this.getEnergy()
				this.addMoney(salary)
				if(this.getEnergy() < hungerMin){
					status = "hungry"
					store = minOneOf(retailers()){this.distance(it)}
				}
			}
		}
		else{
			label = "...";
			if(distance(origin) > 0.25){
				face(origin)
				forward(0.25)
			}
			else{
				if(this.getEnergy() < hungerMin){
					status = "hungry"
					store = minOneOf(retailers()){this.distance(it)}
				}
				else if(workHoursLeft > 0){
					status = "working"
				}
			}
		}
	}
	
	def notAtLocation(BaseTurtle agent){
		return distance(agent) > 0.25
	}
	
	def moveTowards(BaseTurtle agent){
		face(agent)
		forward(0.25)
	}
}
