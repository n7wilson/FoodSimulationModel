package twoagentmodel.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BaseObserver;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class UserObserver extends BaseObserver{
	def tick = 0
	
	def setup(){
		clearAll()
		setDefaultShape(Work, "x")
		createWorks(1){
			setxy(randomXcor(), randomYcor())
			size = 3
		}
		setDefaultShape(Retailer, "house")
		createRetailers(numRetailers){
			setxy(randomXcor(), randomYcor())
		}
		setDefaultShape(Consumer, "person")
		createConsumers(numConsumers){
			setxy(randomXcor(), randomYcor())
		}
		ask(consumers()){
			setStore()
			setWork()
			setOrigin()
		}
	}
	
	def go(){
		tick++
		ask(consumers()){
			step()
		}
		sleep(50);
		if(tick > 60){
			tick = 0
			ask(persons()){
				nextDay()
			}
		}
	}

}