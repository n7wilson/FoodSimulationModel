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
		ask(distributors()){
			step()
		}
		ask(producers()){
			work()
		}
		sleep(timeInterval);
		if(tick > 720){
			tick = 0
			ask(persons()){
				nextDay()
			}
		}
	}

}