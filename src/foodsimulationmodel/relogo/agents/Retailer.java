package foodsimulationmodel.relogo.agents;

import foodsimulationmodel.relogo.food.*;

public class Retailer extends Person {
	
	public Retailer(){
		type = "Retailer";
		status = Status.NORMAL;
		for(int i = 0; i < 100; i++){
			food.add(new Meat(this));
		}
		for(int i = 0; i < 100; i++){
			food.add(new Food(this));
		}
		for(int i = 0; i < 100; i++){
			food.add(new Produce(this));
		}
		for(int i = 0; i < 100; i++){
			food.add(new Junk(this));
		}
	}
}
