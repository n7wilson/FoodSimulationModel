package foodsimulationmodel.relogo.agents

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class SmallFarm extends Producer {

	public SmallFarm() {
		type = "SmallFarm"
		acres = 50;
		for (int i = 1; i < seedTypes.size(); i++) {
			addType(null);
		}
	}
}
