package twoagentmodel.relogo;

import java.util.HashMap;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;

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
		
		public static Season nextSeason(Season current){
			return getSeason((current.getCode() + 1) % 4);
		}
		
		public static Season getSeason(int i){
			if(codeToSeasonMap == null){
				initMap();
			}
			return codeToSeasonMap.get(i);
		}
		
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
	
	public Season season;
	public Weather weather;
	//current temperature in degrees Celsius
	public int temp;
	//current wind speed in km/hour
	public int wind;
	public int tick;
	
	//average temperature for the current season
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
	
	public Environment(){
		season = Season.FALL;
		weather = Weather.SUNNY;
		temp = 15;
		wind = 15;
		tick = 0;
		updateAvgTemp();
	}
	
	public void update(){
		tick++;
		weather = Weather.nextWeather(weather);
		if(tick >= 10){
			season = Season.nextSeason(season);
			updateAvgTemp();
			tick = 0;
		}
		//sets the temperature based on season
		//TODO: change logic of how temperature is determined
		NormalDistribution tempRandom = new NormalDistribution(avgTemp, 6); 
		temp = (int) tempRandom.sample();
		Random rn = new Random();
		wind = rn.nextInt(60);
	}
}
