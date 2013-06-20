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
			setStore()
			setWork()
			setOrigin()
		}
		setDefaultShape(Producer, "farm")
		createProducers(10){
			setxy(randomXcor(), randomYcor())
		}
		setDefaultShape(Distributor, "truck")
		createDistributors(10){
			setxy(randomXcor(), randomYcor())
			getDestination()
		}
	}
	
	//adds 1 Consumer to the simulation
	def addConsumer(){
		setDefaultShape(Consumer, "person")
		createConsumers(1){
			setxy(randomXcor(), randomYcor())
			setStore()
			setWork()
			setOrigin()
		}
	}
	
	//updates the simulation
	def Update(){
		updateRetailers()
		updateConsumers()
	}
	
	//updates the number of retailers
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
	
	//updates the number of consumers
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
	
	//move the model forward one tick
	def go(){
		tick++
		ask(consumers()){
			step()
		}
		ask(distributors()){
			step()
		}
		ask(producers()){
			growPlants()
			work()
		}
		//used to set speed of model
		sleep(timeInterval);
		
		//sets the agents properties for each new day
		if(tick > 720){
			tick = 0
			ask(persons()){
				nextDay()
			}
		}
	}

}