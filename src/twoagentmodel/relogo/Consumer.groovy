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
	
	def hungry = true
	def generator = new Random()
	def store = null
	def origin = null
	def hungerMin = 50 - hungerTime

	
	def step(){
		this.addEnergy(-1)
		label = this.getEnergy()
		if(store == null){
			store = retailers().get(generator.nextInt(retailers().size()))
		}
		if(origin == null){
			origin = patchHere()
		}
		if(hungry){
			//label = "Hungry!";
			if(distance(store) > 0.25){
				face(store)
				forward(0.25) 
			}
			else{
				hungry = false
				this.buy(store, store.inventory)
			}
		}
		else{
			//label = "...";
			if(distance(origin) > 0.25){
				face(origin)
				forward(0.25)
			}
			else{
				if(this.getEnergy() < hungerMin){
					hungry = true
					store = minOneOf(retailers()){distance(it)}
				}
			}
		}
	}
}
