package foodsimulationmodel.food;

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import foodsimulationmodel.agents.IAgent;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

public class Junk extends Food {

	public Junk(IAgent owner) {
		super(owner);
		health = -20;
		expiry = 10000;
		gTime = 100;
	}
}
