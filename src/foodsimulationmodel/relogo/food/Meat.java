package foodsimulationmodel.relogo.food;

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import foodsimulationmodel.relogo.agents.IAgent;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

public class Meat extends Food {

	public Meat(IAgent owner) {
		super(owner);
		health = 5;
		expiry = 1000;
		gTime = 300;
	}
}
