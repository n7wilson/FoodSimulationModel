package foodsimulationmodel.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import foodsimulationmodel.relogo.agents.*;
import foodsimulationmodel.relogo.environment.Environment;
import foodsimulationmodel.relogo.environment.Work;
import foodsimulationmodel.relogo.environment.Environment.Season;
import repast.simphony.relogo.BaseObserver;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class UserObserver extends BaseObserver{
	def tick = 0
	def environment = new Environment()
	
	/*Outside Parameters Referenced:
	 * numConsumers - number of Consumers to create
	 * numRetailers - number of Retailers to create
	 * numDistributors - number of Distributors to create
	 * numProducers - number of Producers to create
	 * 
	 * All parameters are set in the UserGlobalsAndPanelFactory.groovy class
	 */
	def setup(){
		clearAll()
		//set the shape and coordinates for each type of agent
		//the number of agents is dependent on 
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
			//set the Consumer's starting store, the Consumer's work place 
			//and the location of the Consumer's home
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
			//set the first destination for the Distributor
			getDestination()
		}
	}
	
	//Add 1 Consumer to the simulation
	def addConsumer(){
		setDefaultShape(Consumer, "person")
		createConsumers(1){
			setxy(randomXcor(), randomYcor())
			setStore()
			setWork()
			setOrigin()
		}
	}
	
	//Update the simulation
	def Update(){
		updateRetailers()
		updateConsumers()
	}
	
	//Update the number of retailers
	def updateRetailers(){
		def additional = numRetailers - retailers().size()
		//if there are too many Retailers then delete excess from 
		//the front of the list of Retailers
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
		//if there are too many Retailers then delete excess from
		//the front of the list of Retailers
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
	
	//Move the model forward one tick in time
	/*Outside Parameters Referenced
	 * timeInterval - sets sleep time (in milliseconds) for model between calls to go()
	 * 
	 * Parameter is set in the UserGlobalsAndPanelFactory.groovy class
	 */
	def go(){
		tick++
		//step each agent type
		ask(consumers()){
			step()
		}
		ask(distributors()){
			step()
		}
		ask(producers()){
			//if it isn't Winter then the plants grow
			if(environment.season != Environment.Season.WINTER){
				growPlants()
			}
			step()
		}
		//used to set speed of model
		sleep(timeInterval);
		
		//sets the agents properties for each new day
		if(tick > 720){
			tick = 0
			environment.update()
			ask(persons()){
				nextDay(environment.wind > 55)
			}
			//changes preferences everyday
			ask(persons()){
				setPref()
			}
		}
	}
	
	//Functions for simulation monitors. Called from UserGlobalsAndPanelFactory.groovy class
	def getSeason(){
		environment.season.toString()
	}
	
	def getTemp(){
		environment.temp
	}
	
	def getWind(){
		environment.wind
	}
	
	def getWeather(){
		environment.weather.toString()
	}
	

}