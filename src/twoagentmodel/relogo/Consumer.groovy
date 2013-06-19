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
	
	def status = "hungry"
	def generator = new Random()
	def store = null
	def origin = null
	def work = null
	def hungerMin = 500
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
		if(this.getEnergy() < hungerMin){
			status = "hungry"
			store = minOneOf(retailers()){this.distance(it)}
		}
		else if(workHoursLeft > 0){
			status = "working"
		}
		
		
		if(status == "hungry"){
			if(food.empty){
				if(notAtLocation(store)){
					moveTowards(store) 
				}
				else{
					this.buy(store)
				}
			}
			else{
				eat()
				status = "normal"
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
			}
		}
		else{
			if(distance(origin) > 0.5){
				face(origin)
				forward(0.5)
			}
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
