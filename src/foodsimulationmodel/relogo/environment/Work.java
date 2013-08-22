package foodsimulationmodel.relogo.environment;


import com.vividsolutions.jts.geom.Coordinate;

import foodsimulationmodel.relogo.agents.IAgent;

public class Work implements IAgent {
	Coordinate location;

	@Override
	public void step() throws Exception {
		
	}

	@Override
	public void setHome(double x, double y) {
		location = new Coordinate(x,y);
		
	}

	@Override
	public Coordinate getHome() {
		// TODO Auto-generated method stub
		return location;
	}

	@Override
	public Coordinate getCoords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
