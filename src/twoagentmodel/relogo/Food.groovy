package twoagentmodel.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class Food extends BaseTurtle {
	//energy value of the food
	public int energy
	//cost of the food
	public int money
	
	//default constructor
	def Food(){
		energy = 75
		money = 3
	}
}
