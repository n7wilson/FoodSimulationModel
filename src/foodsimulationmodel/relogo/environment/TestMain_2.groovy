package foodsimulationmodel.relogo.environment;

import java.io.File;
import com.jme3.app.SimpleApplication;

import com.jme3.light.DirectionalLight;

import com.jme3.material.Material;

import com.jme3.math.ColorRGBA;

import com.jme3.math.Vector3f;

import com.jme3.renderer.RenderManager;

import com.jme3.scene.Geometry;

import com.jme3.scene.Spatial;

import com.jme3.scene.shape.Box;

import java.io.BufferedReader;

import java.io.File;

import java.io.FileNotFoundException;

import java.io.FileReader;

import java.io.IOException;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.List;

import java.util.Scanner;

import java.util.logging.Level;

import java.util.logging.Logger;
import repast.simphony.engine.schedule.AbstractAction;
import repast.simphony.engine.schedule.Frequency;
import repast.simphony.engine.schedule.ScheduleParameters;
import foodsimulationmodel.relogo.agents.Consumer;
import foodsimulationmodel.relogo.agents.Distributor
import foodsimulationmodel.relogo.agents.Producer
import foodsimulationmodel.relogo.agents.Retailer
import foodsimulationmodel.relogo.UserObserver
import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import foodsimulationmodel.relogo.environment.Work;
import repast.simphony.relogo.AgentSet
import repast.simphony.relogo.BaseObserver;
import repast.simphony.relogo.Observer;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;

public class TestMain_2 extends SimpleApplication{
	private String baseDir = "";
	private static UserObserver ob;

	public static void main(String[] args){

		File file = new File(args[0]); // the scenario dir

		TestRunner_2 runner = new TestRunner_2();

		try {
			runner.load(file);     // load the repast scenario
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		runner.runInitialize();  // initialize the run
		
		Iterator itr = runner.context.iterator();
		while(itr.hasNext()){
			Object next = itr.next();
			if(next.getClass() == UserObserver.class){
				ob = (UserObserver) next;
			}
		}
		
		TestMain_2 app = new TestMain_2();
		app.start();
		

//		double endTime = 1000.0;  // some arbitrary end time

		// Run the sim a few times to check for cleanup and init issues.
		for(int i=0; i<1; i++){


//			RunEnvironment.getInstance().endAt(endTime);
			
			AbstractAction action = new SimulationAction(ScheduleParameters.createRepeating(0, 1), runner.getContext());
			
			runner.scheduleAction(ScheduleParameters.createRepeating(0, 1), action);

			while (runner.getActionCount() > 0){  // loop until last action is left
				if (runner.getModelActionCount() == 0) {
					runner.setFinishing(true);
				}
				runner.step();  // execute all scheduled actions at next tick

			}

			runner.stop();          // execute any actions scheduled at run end
			runner.cleanUpRun();
		}
		runner.cleanUpBatch();    // run after all runs complete
	}

	@Override

    public void simpleInitApp() {   //this is where your initialization code goes. basically whatever the first frame of the game looks like is in here

        

             

        DirectionalLight sun = new DirectionalLight();                          //creates a light source called sun as a directional light. look up the                 

                                                                                //different kinds of lights to understand the differences. 

        sun.setDirection (new Vector3f(-1.0f, -0.7f, -1.0f));                   //sets the direction  of the directional light.

        rootNode.addLight(sun);

        

        

        Spatial R = assetManager.loadModel(baseDir + "assets/myTerrain.j3o");             //adds a terrain to the scene. You can make your own terrains in the terrain 

                                                                                //builder

        rootNode.attachChild(R);

        

        flyCam.setEnabled(true);                                                //turns the camera into a fly camera. Not entirely sure what that means, 

                                                                                //but its easier to move around than the regular camera

        flyCam.setMoveSpeed(50);                                                //sets the speed of the camera. Ideally you want to multiply it by fps, so that

                                                                                //camera moves slower or faster depending on the computer speed. 

        flyCam.setRotationSpeed(5);                                             //sets the rotation speed (aka mouse sensitivity)

        flyCam.setDragToRotate(true);                                           //you have to click to rotate

        

        

     
    

    Spatial S;                                                                  //create a spatial (object that takes up space) called S
 

        for(Work w: ob.works()){

             

        S = assetManager.loadModel(baseDir + "assets/building1.mesh.xml");                //give the spatial S a model. In this case a building model.

        S.scale(5f);

        S.rotate(0.0f, -3.0f, 0.0f);

        S.setLocalTranslation((Float)w.getXcor()*6,1.0f,(Float)w.getYcor()*6);//set its initial position

            S.setMaterial((Material)assetManager.loadMaterial(baseDir + "assets/workMaterial.j3m"));
			
			rootNode.attachChild(S);
			}//set its material. material is basically how the surface of the

                                                                                            //model acts. It defines things like its colour in light, colour in

                                                                                            //darkness. the colour of glare it gives off. etc. 

        

        for(Retailer r: ob.retailers()){

            

        S = assetManager.loadModel(baseDir + "assets/retailer1.mesh.xml");

        S.scale(5f);                                                            //scale it in size. you can also say S.scale(1f, 2f, 3f) to be more specific 

                                                                                //about the amount of scaling you do in each direction. 

        S.rotate(0.0f, -3.0f, 0.0f);                                            //rotate that spatial!

        S.setLocalTranslation((Float)r.getXcor()*6,1.0f,(Float)r.getYcor()*6);

            S.setMaterial((Material)assetManager.loadMaterial(baseDir + "assets/retMaterial.j3m"));
			
			rootNode.attachChild(S);
			}

        

        for(Distributor d: ob.distributors()){

             

        S = assetManager.loadModel(baseDir + "assets/distributor1.mesh.xml");

        S.scale(5f);

        S.rotate(0.0f, -3.0f, 0.0f);

        S.setLocalTranslation((Float)d.getXcor()*6,1.0f,(Float)d.getYcor()*6);

            S.setMaterial((Material)assetManager.loadMaterial(baseDir + "assets/disMaterial.j3m"));
			
			rootNode.attachChild(S);
			}

        

        for(Producer p: ob.producers()){

             

        S = assetManager.loadModel(baseDir + "assets/producer.mesh.xml");

        S.scale(5f);

        S.rotate(0.0f, -3.0f, 0.0f);

        S.setLocalTranslation((Float)p.getXcor()*6,1.0f,(Float)p.getYcor()*6);

            S.setMaterial((Material)assetManager.loadMaterial(baseDir + "assets/proMaterial.j3m"));
			
			rootNode.attachChild(S);
			}

        

        for(Consumer c: ob.consumers()){

           

        S = assetManager.loadModel(baseDir + "assets/agent007.mesh.xml");

        S.scale(5f);

        S.rotate(0.0f, -3.0f, 0.0f);

        S.setLocalTranslation((Float)c.getXcor()*6,1.0f,(Float)c.getYcor()*6);

            S.setMaterial((Material)assetManager.loadMaterial(baseDir + "assets/charMaterial.j3m"));
			
			rootNode.attachChild(S);
			}
    }

    

    @Override

    public void simpleUpdate(float tpf) { //this is where all the code for actually running the game goes. the function gets called once a frame. 

     /*  b +=1;

       

       for(int i = 0; i < 36; i++){

           

           float xmove = (float) (timestep[(b-1)%frames][i][1]-timestep[(b)%frames][i][1]);

           float zmove =  (float)(timestep[(b-1)%frames][i][2]-timestep[(b)%frames][i][2]);

            agents[i].move(xmove, 0f, zmove);

        

    }*/

    }



    @Override

    public void simpleRender(RenderManager rm) { //not entirely sure what this is for

        //TODO: add render code

    }
	
	
}