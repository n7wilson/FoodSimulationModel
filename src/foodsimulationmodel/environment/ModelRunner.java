package foodsimulationmodel.environment;

import java.io.File;

import repast.simphony.batch.BatchScenarioLoader;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.engine.controller.Controller;
import repast.simphony.engine.controller.DefaultController;
import repast.simphony.engine.environment.AbstractRunner;
import repast.simphony.engine.environment.ControllerRegistry;
import repast.simphony.engine.environment.DefaultRunEnvironmentBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunEnvironmentBuilder;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.BoundParameters;
import repast.simphony.parameter.DefaultParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.parameter.SweeperProducer;
import simphony.util.messages.MessageCenter;

public class ModelRunner extends AbstractRunner {

	private static MessageCenter msgCenter = MessageCenter.getMessageCenter(ModelRunner.class);

	private RunState currentRunState = null;
	private RunEnvironmentBuilder runEnvironmentBuilder;
	private RunEnvironment runEnvironment;
	protected Controller controller;
	protected boolean pause = false;
	protected Object monitor = new Object();
	protected SweeperProducer producer;
	private ISchedule schedule;
	protected Parameters parameters;

	public ModelRunner() {
		runEnvironmentBuilder = new DefaultRunEnvironmentBuilder(this, true);
		controller = new DefaultController(runEnvironmentBuilder);
		controller.setScheduleRunner(this);
		parameters = new BoundParameters(new DefaultParameters());
	}

	public void load(File scenarioDir) throws Exception{
		if (scenarioDir.exists()) {
			BatchScenarioLoader loader = new BatchScenarioLoader(scenarioDir);
			ControllerRegistry registry = loader.load(runEnvironmentBuilder);
			controller.setControllerRegistry(registry);
		} else {
			System.out.println("Directory not found");
			msgCenter.error("Scenario not found", new IllegalArgumentException(
					"Invalid scenario " + scenarioDir.getAbsolutePath()));
			return;
		}

		controller.batchInitialize();
		controller.runParameterSetters(null);
		parameters = new BoundParameters(new DefaultParameters());
	}


	
	public void runInitialize(){
		((DefaultParameters) parameters).addParameter("randomSeed", "Default Random Seed", int.class, 914509816, false);
		((DefaultParameters) parameters).addParameter("default_observer_maxPxcor", "Max X Coordinate", int.class, 16, false);
		((DefaultParameters) parameters).addParameter("default_observer_maxPycor", "Max Y Coordinate", int.class, 16, false);
		((DefaultParameters) parameters).addParameter("default_observer_minPxcor", "Min X Coordinate", int.class, -16, false);
		((DefaultParameters) parameters).addParameter("default_observer_minPycor", "Min Y Coordinate", int.class, -16, false);
		((DefaultParameters) parameters).addParameter("AGENT_DEFINITION", "Agent Definition", String.class, "random:40,10,10,7,7" , false);
		runEnvironment = runEnvironmentBuilder.createRunEnvironment(); 
		runEnvironment.setParameters(parameters);
		
		Context con = new DefaultContext<Object>();
		ContextManager manager = new ContextManager();
		manager.build(con);
		
		setContext(con);
		
		
		if(currentRunState == null){
			System.out.println("Run State is null");
		}
		else if(currentRunState.getMasterContext() == null){
			System.out.println("Master Context is null");
		}
		if(runEnvironmentBuilder == null){
			System.out.println("Environment Builder is null");
		}
		if(controller == null){
			System.out.println("Controller is null");
		}
		else if(controller.getCurrentRunState() == null){
			System.out.println("Run State2 is null");
		}
		else if(controller.getCurrentRunState().getMasterContext() == null){
			System.out.println("Master Context2 is null");
		}
		if(monitor == null){
			System.out.println("Monitor is null");
		}
		if(producer == null){
			System.out.println("Producer is null");
		}
		if(schedule == null){
			System.out.println("Scedule is null");
		}
		if(parameters == null){
			System.out.println("Parameters is null");
		}
		if(runEnvironment == null){
			System.out.println("Run Environment is null");
		}
	    currentRunState = controller.runInitialize(parameters);
		schedule = RunEnvironment.getInstance().getCurrentSchedule();
	}

	public void cleanUpRun(){
		controller.runCleanup();
	}
	public void cleanUpBatch(){
		controller.batchCleanup();
	}

	// returns the tick count of the next scheduled item
	public double getNextScheduledTime(){
		return ((Schedule)runEnvironment.getCurrentSchedule()).peekNextAction().getNextTime();
	}

	// returns the number of model actions on the schedule
	public int getModelActionCount(){
		return schedule.getModelActionCount();
	}

	// returns the number of non-model actions on the schedule
	public int getActionCount(){
		return schedule.getActionCount();
	}

	// Step the schedule
	public void step(){
    schedule.execute();
	}

	// stop the schedule
	public void stop(){
		if ( schedule != null )
			schedule.executeEndActions();
	}

	public void setFinishing(boolean fin){
		schedule.setFinishing(fin);
	}
	
	public Context getContext(){
		return currentRunState.getMasterContext();
	}
	
	public void setContext(Context context){
		if(currentRunState == null){
			controller.getCurrentRunState().setMasterContext(context);
		}
		else{
			currentRunState.setMasterContext(context);
		}
	}
	
	public void scheduleAction(ScheduleParameters params, IAction action){
		if(schedule == null){
			System.out.println("Schedule is null!"); 
		}
		else{
		schedule.schedule(params, action);
		}
	}
	

	public void execute(RunState toExecuteOn) {
		// required AbstractRunner stub.  We will control the
		//  schedule directly.
	}
}
