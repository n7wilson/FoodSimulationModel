package twoagentmodel.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class Producer extends Person {
	
	public Producer(){
		for(int i = 0; i < 500; i++){
			food.add(new Food())
		}
	}
	
	def work(){
		food.add(new Food())
	}
}
