package twoagentmodel.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class Retailer extends Person {
	public List<Food> inventory = new ArrayList<Food>()
	
	public Retailer(){
		for(int i = 0; i < 500; i++){
			inventory.add(new Food())
		}
	}
}
