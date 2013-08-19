package foodsimulationmodel.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.BaseTurtle;
import repast.simphony.relogo.BasePatch;
import repast.simphony.relogo.Plural;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

class UserTurtle extends BaseTurtle{
	protected static int uniqueID = 0
	private String id
	public void setIdentifier(String id){
		this.id = id
	}
	public String getIdentifier(){
		return this.id
	}
	
	public UserTurtle(){
		id = uniqueID.toString()
		uniqueID++
	}
}