package foodsimulationmodel.relogo.environment;

import java.util.HashMap;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;


public class Environment {
	
	//Enumerator for seasons. Could possibly change to months
	public enum Season{
		FALL(0), WINTER(1), SPRING(2), SUMMER(3);
		
		private int code;
		private static HashMap<Integer, Season> codeToSeasonMap;
		private Season(int c){
			code = c;
		}
		public int getCode(){
			return code;
		}
		
		//Get the next season
		public static Season nextSeason(Season current){
			return getSeason((current.getCode() + 1) % 4);
		}
		
		//Get the season specified by the index
		public static Season getSeason(int i){
			if(codeToSeasonMap == null){
				initMap();
			}
			return codeToSeasonMap.get(i);
		}
		
		//Initialize a map between the Season type and it's integer index
		private static void initMap(){
			codeToSeasonMap = new HashMap<Integer, Season>();
			for(Season s: values()){
				codeToSeasonMap.put(s.code, s);
			}
		}
	}
	
	//Enumerator for weather conditions
	public enum Weather{
		PERCIP("Raining"), SUNNY("Sunny"), CLOUDY("Cloudy");
		
		private String label;
		
		private Weather(String l){
			label = l;
		}
		
		//Get the next weather condition
		//Currently uses a Markov chain to determine next state
		//TODO: change so function uses CLimateODE
		public static Weather nextWeather(Weather current){
			double rn = Math.random();
			switch(current){
				case PERCIP:
					if(rn > 0.75){
						return Weather.PERCIP;
					}
					if(rn < 0.2){
						return Weather.SUNNY;
					}
					else{
						return Weather.CLOUDY;
					}
				case CLOUDY:
					if(rn > 0.65){
						return Weather.PERCIP;
					}
					if(rn < 0.35){
						return Weather.SUNNY;
					}
					else{
						return Weather.CLOUDY;
					}
				case SUNNY:
					if(rn > 0.5){
						return Weather.SUNNY;
					}
					if(rn < 0.2){
						return Weather.PERCIP;
					}
					else{
						return Weather.CLOUDY;
					}
				default:
					return Weather.SUNNY;
			}
		}
		
		@Override
		public String toString(){
			return label;
		}
	}
	
	//Current Season
	public Season season;
	//Current Weather
	public Weather weather;
	//Current temperature in degrees Celsius
	public double temp;
	//Current wind speed in km/hour
	public double wind;
	
	//Tracks time
	public int tick;
	//Ordinary Differential Equation to represent the climate
	private FirstOrderDifferentialEquations climate;
	private double weatherIndex;
	
	//Average temperature for the current season
	private int avgTemp;
	
	private void updateAvgTemp(){
		switch(season){
			case FALL:
				avgTemp = 12;
				break;
			case WINTER:
				avgTemp = -5;
				break;
			case SPRING:
				avgTemp = 15;
				break;
			case SUMMER:
				avgTemp = 23;
				break;
		}
	}
	
	//Default constructor
	public Environment(){
		season = Season.FALL;
		weather = Weather.SUNNY;
		temp = 15;
		wind = 15;
		tick = 1;
		climate = new ClimateODE();
		updateAvgTemp();
	}
	
	//Updates the climate. Called at end of each day
	public void update(){
		tick++;
		weather = Weather.nextWeather(weather);
		//if 10 days have passed update Season
		if(tick % 10 == 0){
			season = Season.nextSeason(season);
			updateAvgTemp();
		}
		//sets the temperature and wind based on season
		updateEnvironment();
	}
	
	//Update the Environment variables based on the ODE used to model climate
	public void updateEnvironment(){
		//integrator for our differencial equation
		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
		//array to hold the current climate values
		double[] climateArr = new double[] { weatherIndex, temp - avgTemp, wind};
		double time = tick / 5;
		//get the next values for the climate variables
		dp853.integrate(climate, time, climateArr, time + 0.2, climateArr);
		weatherIndex = climateArr[0];
		temp = climateArr[1] + avgTemp;
		wind = climateArr[2];
	}
}
