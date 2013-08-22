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

package foodsimulationmodel.relogo.environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;
import repast.simphony.space.gis.SimpleAdder;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import foodsimulationmodel.pathmapping.GISFunctions;
import foodsimulationmodel.pathmapping.Junction;
import foodsimulationmodel.pathmapping.NetworkEdge;
import foodsimulationmodel.pathmapping.NetworkEdgeCreator;
import foodsimulationmodel.pathmapping.Road;
import foodsimulationmodel.pathmapping.SpatialIndexManager;
import foodsimulationmodel.environment.contexts.JunctionContext;
import foodsimulationmodel.environment.contexts.RoadContext;
import foodsimulationmodel.exceptions.AgentCreationException;
import foodsimulationmodel.exceptions.EnvironmentError;
import foodsimulationmodel.exceptions.NoIdentifierException;
import foodsimulationmodel.exceptions.ParameterNotFoundException;

public class ContextManager implements ContextBuilder<Object> {

	/*
	 * A logger for this class. Note that there is a static block that is used to configure all logging for the model
	 * (at the bottom of this file).
	 */
//	private static Logger LOGGER = Logger.getLogger(ContextManager.class.getName());

	// Optionally force agent threading off (good for debugging)
	private static final boolean TURN_OFF_THREADING = false;;

	private static Properties properties;

	/*
	 * Pointers to contexts and projections (for convenience). Most of these can be made public, but the agent ones
	 * can't be because multi-threaded agents will simultaneously try to call 'move()' and interfere with each other. So
	 * methods like 'moveAgent()' are provided by ContextManager.
	 */

	private static Context<Object> mainContext;

	// building context and projection cab be public (thread safe) because buildings only queried

	public static Context<Road> roadContext;
	public static Geography<Road> roadProjection;

	public static Context<Junction> junctionContext;
	public static Geography<Junction> junctionGeography;
	public static Network<Junction> roadNetwork;


	@Override
	public Context<Object> build(Context<Object> con) {
		System.out.println("ContextManager initializing...");

		// Keep a useful static link to the main context
		mainContext = con;

		// This is the name of the 'root'context
		mainContext.setId(GlobalVars.CONTEXT_NAMES.MAIN_CONTEXT);

		// Read in the model properties
		try {
			readProperties();
		} catch (IOException ex) {
			throw new RuntimeException("Could not read model properties,  reason: " + ex.toString(), ex);
		}

		// Configure the environment
		String gisDataDir = ContextManager.getProperty(GlobalVars.GISDataDirectory);
//		LOGGER.log(Level.FINE, "Configuring the environment with data from " + gisDataDir);

		try {

			// Create the Roads - context and geography
			roadContext = new RoadContext();
			roadProjection = GeographyFactoryFinder.createGeographyFactory(null).createGeography(
					GlobalVars.CONTEXT_NAMES.ROAD_GEOGRAPHY, roadContext,
					new GeographyParameters<Road>(new SimpleAdder<Road>()));
			String roadFile = gisDataDir + getProperty(GlobalVars.RoadShapefile);
			GISFunctions.readShapefile(Road.class, roadFile, roadProjection, roadContext);
			mainContext.addSubContext(roadContext);
			SpatialIndexManager.createIndex(roadProjection, Road.class);
//			LOGGER.log(Level.FINER, "Read " + roadContext.getObjects(Road.class).size() + " roads from " + roadFile);

			// Create road network

			// 1.junctionContext and junctionGeography
			junctionContext = new JunctionContext();
			mainContext.addSubContext(junctionContext);
			junctionGeography = GeographyFactoryFinder.createGeographyFactory(null).createGeography(
					GlobalVars.CONTEXT_NAMES.JUNCTION_GEOGRAPHY, junctionContext,
					new GeographyParameters<Junction>(new SimpleAdder<Junction>()));

			// 2. roadNetwork
			NetworkBuilder<Junction> builder = new NetworkBuilder<Junction>(GlobalVars.CONTEXT_NAMES.ROAD_NETWORK,
					junctionContext, false);
			builder.setEdgeCreator(new NetworkEdgeCreator<Junction>());
			roadNetwork = builder.buildNetwork();
			GISFunctions.buildGISRoadNetwork(roadProjection, junctionContext, junctionGeography, roadNetwork);

			// Add the junctions to a spatial index (couldn't do this until the
			// road network had been created).
			SpatialIndexManager.createIndex(junctionGeography, Junction.class);

			testEnvironment();

		} catch (MalformedURLException e) {
//			LOGGER.log(Level.SEVERE, "", e);
			return null;
		} catch (EnvironmentError e) {
//			LOGGER.log(Level.SEVERE, "There is an eror with the environment, cannot start simulation", e);
			return null;
		} catch (NoIdentifierException e) {
//			LOGGER.log(Level.SEVERE, "One of the input buildings had no identifier (this should be read"
//					+ "from the 'identifier' column in an input GIS file)", e);
			return null;
		} catch (FileNotFoundException e) {
//			LOGGER.log(Level.SEVERE, "Could not find an input shapefile to read objects from.", e);
			return null;
		}


		return mainContext;
	}

	private static <T> List<T> toList(Iterable i) {
		List<T> l = new ArrayList<T>();
		Iterator<T> it = i.iterator();
		while (it.hasNext()) {
			l.add(it.next());
		}
		return l;
	}

	

	public void printTicks() {
//		LOGGER.info("Iterations: " + RunEnvironment.getInstance().getCurrentSchedule().getTickCount());
	}

	/**
	 * Convenience function to get a Simphony parameter
	 * 
	 * @param <T>
	 *            The type of the parameter
	 * @param paramName
	 *            The name of the parameter
	 * @return The parameter.
	 * @throws ParameterNotFoundException
	 *             If the parameter could not be found.
	 */
	public static <V> V getParameter(String paramName) throws ParameterNotFoundException {
		Parameters p = RunEnvironment.getInstance().getParameters();
		Object val = p.getValue(paramName);

		if (val == null) {
			throw new ParameterNotFoundException(paramName);
		}

		// Try to cast the value and return it
		@SuppressWarnings("unchecked")
		V value = (V) val;
		return value;
	}

	/**
	 * Get the value of a property in the properties file. If the input is empty or null or if there is no property with
	 * a matching name, throw a RuntimeException.
	 * 
	 * @param property
	 *            The property to look for.
	 * @return A value for the property with the given name.
	 */
	public static String getProperty(String property) {
		if (property == null || property.equals("")) {
			throw new RuntimeException("getProperty() error, input parameter (" + property + ") is "
					+ (property == null ? "null" : "empty"));
		} else {
			String val = ContextManager.properties.getProperty(property);
			if (val == null || val.equals("")) { // No value exists in the
													// properties file
				throw new RuntimeException("checkProperty() error, the required property (" + property + ") is "
						+ (property == null ? "null" : "empty"));
			}
			return val;
		}
	}

	/**
	 * Read the properties file and add properties. Will check if any properties have been included on the command line
	 * as well as in the properties file, in these cases the entries in the properties file are ignored in preference
	 * for those specified on the command line.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void readProperties() throws FileNotFoundException, IOException {

		File propFile = new File("./pathmapping.properties");
		if (!propFile.exists()) {
			throw new FileNotFoundException("Could not find properties file in the default location: "
					+ propFile.getAbsolutePath());
		}

//		LOGGER.log(Level.FINE, "Initialising properties from file " + propFile.toString());

		ContextManager.properties = new Properties();

		FileInputStream in = new FileInputStream(propFile.getAbsolutePath());
		ContextManager.properties.load(in);
		in.close();

		// See if any properties are being overridden by command-line arguments
		for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
			String k = (String) e.nextElement();
			String newVal = System.getProperty(k);
			if (newVal != null) {
				// The system property has the same name as the one from the
				// properties file, replace the one in the properties file.
//				LOGGER.log(Level.INFO, "Found a system property '" + k + "->" + newVal
//						+ "' which matches a NeissModel property '" + k + "->" + properties.getProperty(k)
//						+ "', replacing the non-system one.");
//				properties.setProperty(k, newVal);
			}
		} // for
		return;
	} // readProperties

	/**
	 * Check that the environment looks ok
	 * 
	 * @throws NoIdentifierException
	 */
	private void testEnvironment() throws EnvironmentError, NoIdentifierException {

//		LOGGER.log(Level.FINE, "Testing the environment");
		// Get copies of the contexts/projections from main context
		Context<Road> rc = (Context<Road>) mainContext.getSubContext(GlobalVars.CONTEXT_NAMES.ROAD_CONTEXT);
		Context<Junction> jc = (Context<Junction>) mainContext.getSubContext(GlobalVars.CONTEXT_NAMES.JUNCTION_CONTEXT);

		// Geography<Building> bg = (Geography<Building>)
		// bc.getProjection(GlobalVars.CONTEXT_NAMES.BUILDING_GEOGRAPHY);
		// Geography<Road> rg = (Geography<Road>)
		// rc.getProjection(GlobalVars.CONTEXT_NAMES.ROAD_GEOGRAPHY);
		// Geography<Junction> jg = (Geography<Junction>)
		// rc.getProjection(GlobalVars.CONTEXT_NAMES.JUNCTION_GEOGRAPHY);
		Network<Junction> rn = (Network<Junction>) jc.getProjection(GlobalVars.CONTEXT_NAMES.ROAD_NETWORK);

		// 1. Check that there are some objects in each of the contexts
		checkSize(rc, jc);

		// 2. Check that the number of roads matches the number of edges
		if (sizeOfIterable(rc.getObjects(Road.class)) != sizeOfIterable(rn.getEdges())) {
			throw new EnvironmentError("There should be equal numbers of roads in the road "
					+ "context and edges in the road network. But there are "
					+ sizeOfIterable(rc.getObjects(Road.class)) + " and " + sizeOfIterable(rn.getEdges()));
		}

		// 3. Check that the number of junctions matches the number of nodes
		if (sizeOfIterable(jc.getObjects(Junction.class)) != sizeOfIterable(rn.getNodes())) {
			throw new EnvironmentError("There should be equal numbers of junctions in the junction "
					+ "context and nodes in the road network. But there are "
					+ sizeOfIterable(jc.getObjects(Junction.class)) + " and " + sizeOfIterable(rn.getNodes()));
		}

		System.out.println("The road network has " + sizeOfIterable(rn.getNodes()) + " nodes and "
				+ sizeOfIterable(rn.getEdges()) + " edges.");

		// 4. Check that Roads and Buildings have unique identifiers
		HashMap<String, ?> idList = new HashMap<String, Object>();
		for (Road r : rc.getObjects(Road.class)) {
			if (idList.containsKey(r.getIdentifier()))
				throw new EnvironmentError("More than one building found with id " + r.getIdentifier());
			idList.put(r.getIdentifier(), null);
		}

	}

	public static int sizeOfIterable(Iterable i) {
		int size = 0;
		Iterator<Object> it = i.iterator();
		while (it.hasNext()) {
			size++;
			it.next();
		}
		return size;
	}

	/**
	 * Checks that the given <code>Context</code>s have more than zero objects in them
	 * 
	 * @param contexts
	 * @throws EnvironmentError
	 */
	public void checkSize(Context<?>... contexts) throws EnvironmentError {
		for (Context<?> c : contexts) {
			int numObjs = sizeOfIterable(c.getObjects(Object.class));
			if (numObjs == 0) {
				throw new EnvironmentError("There are no objects in the context: " + c.getId().toString());
			}
		}
	}

	public static void stopSim(Exception ex, Class<?> clazz) {
		ISchedule sched = RunEnvironment.getInstance().getCurrentSchedule();
		sched.setFinishing(true);
		sched.executeEndActions();
		//LOGGER.log(Level.SEVERE, "ContextManager has been told to stop by " + clazz.getName(), ex);
	}

}
			