/*
©Copyright 2012 Nick Malleson
This file is part of RepastCity.

RepastCity is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

RepastCity is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with RepastCity.  If not, see <http://www.gnu.org/licenses/>.
*/

package foodsimulationmodel.agents;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;


/**
 * All agents must implement this interface so that it the simulation knows how
 * to step them.
 * 
 * @author Nick Malleson
 * 
 */
public interface IAgent {
	

	/**
	 * Controls the agent. This method will be called by the scheduler once per
	 * iteration.
	 */
	 public void step() throws Exception;
	
	/**
	 * Set where the agent lives.
	 */
	public void setHome(double x, double y);
	
	/**
	 * Get the agent's home.
	 */
	public Coordinate getHome();

	public Coordinate getCoords();

	public String getIdentifier();
	
	
}
