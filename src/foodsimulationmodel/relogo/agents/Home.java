package foodsimulationmodel.relogo.agents;

import com.vividsolutions.jts.geom.Coordinate;


public class Home implements IAgent{
	private Consumer resident;
	private Coordinate home;
	@Override
	public void step() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHome(double x, double y) {
		home = new Coordinate(x,y);
	}

	@Override
	public Coordinate getHome() {
		// TODO Auto-generated method stub
		return home;
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
	
	public void setResident(Consumer res){
		resident = res;
	}
	
	public Consumer getResident(){
		return resident;
	}
	
}
