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
		createConsumers(1){
			setxy(randomXcor(), randomYcor())
		}
		ask(consumers()){
			setStore()
			setWork()
			setOrigin()
		}
	}
	
	def addConsumer(){
		setDefaultShape(Consumer, "person")
		createConsumers(1){
			setxy(randomXcor(), randomYcor())
			setStore()
			setWork()
			setOrigin()
		}
	}
	
	def Update(){
		updateRetailers()
		updateConsumers()
	}
	
	
	def updateRetailers(){
		def additional = numRetailers - retailers().size()
		if(additional < 0){
			for(int i = 0; i < -additional; i++){
				retailers().get(0).die()
			}
		}
		else if(additional > 0){
			createRetailers(additional){
				setxy(randomXcor(), randomYcor())
			}
		}
	}
	
	def updateConsumers(){
		def additional = numConsumers - consumers().size()
		if(additional < 0){
			for(int i = 0; i < -additional; i++){
				consumers().get(0).die()
			}
		}
		else if(additional > 0){
			createConsumers(additional){
				setxy(randomXcor(), randomYcor())
				setStore()
				setWork()
				setOrigin()
			}
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