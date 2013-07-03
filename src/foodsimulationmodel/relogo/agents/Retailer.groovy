package foodsimulationmodel.relogo.agents

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import foodsimulationmodel.relogo.food.*
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class Retailer extends Person {
	
	public Retailer(){
		status = "selling"
		for(int i = 0; i < 100; i++){
			food.add(new Meat())
		}
		for(int i = 0; i < 100; i++){
			food.add(new Food())
		}
		for(int i = 0; i < 100; i++){
			food.add(new Produce())
		}
		for(int i = 0; i < 100; i++){
			food.add(new Junk())
		}
	}
}
