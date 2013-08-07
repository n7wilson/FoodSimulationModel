package foodsimulation.game;



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

import java.util.List;

import java.util.Scanner;

import java.util.logging.Level;

import java.util.logging.Logger;



/**

 * test

 * @author normenhansen

 */

public class Main extends SimpleApplication {                                   //this is the main class you want running

    



    public static int b = 0;                                                    //bunch of public variables

    public static Spatial[] agents = new Spatial[36];

    public static String filepath = "/Users/nathanwilson/Documents/workspace/FoodSimulationModel/FoodSimulationData";

    public double timestep[][][] = gimmeData(filepath);

    public static int frames = 0;

    public static void main(String[] args) {

        Main app = new Main();

        app.start();

     

    }



    @Override

    public void simpleInitApp() {   //this is where your initialization code goes. basically whatever the first frame of the game looks like is in here

        

             

        DirectionalLight sun = new DirectionalLight();                          //creates a light source called sun as a directional light. look up the                 

                                                                                //different kinds of lights to understand the differences. 

        sun.setDirection (new Vector3f(-1.0f, -0.7f, -1.0f));                   //sets the direction  of the directional light.

        rootNode.addLight(sun);

        

        

        Spatial R = assetManager.loadModel("Textures/myTerrain.j3o");             //adds a terrain to the scene. You can make your own terrains in the terrain 

                                                                                //builder

        rootNode.attachChild(R);

        

        flyCam.setEnabled(true);                                                //turns the camera into a fly camera. Not entirely sure what that means, 

                                                                                //but its easier to move around than the regular camera

        flyCam.setMoveSpeed(50);                                                //sets the speed of the camera. Ideally you want to multiply it by fps, so that

                                                                                //camera moves slower or faster depending on the computer speed. 

        flyCam.setRotationSpeed(5);                                             //sets the rotation speed (aka mouse sensitivity)

        flyCam.setDragToRotate(true);                                           //you have to click to rotate

        

        

     

    

    Spatial S;                                                                  //create a spatial (object that takes up space) called S

    



    

            

    for(int a = 0; a < 36; a++) 

    {    

       

        if(timestep[0][a][0] == 0){

             

        S = assetManager.loadModel("Textures/building1.mesh.xml");                //give the spatial S a model. In this case a building model.

        S.scale(5f);

        S.rotate(0.0f, -3.0f, 0.0f);

        S.setLocalTranslation((float)timestep[0][a][1]*6,1.0f,(float)timestep[0][a][2]*6);//set its initial position

            S.setMaterial((Material)assetManager.loadMaterial("Materials/workMaterial.j3m"));}//set its material. material is basically how the surface of the

                                                                                            //model acts. It defines things like its colour in light, colour in

                                                                                            //darkness. the colour of glare it gives off. etc. 

        

        else if(timestep[0][a][0] == 1){

            

        S = assetManager.loadModel("Models/retailer1.mesh.xml");

        S.scale(5f);                                                            //scale it in size. you can also say S.scale(1f, 2f, 3f) to be more specific 

                                                                                //about the amount of scaling you do in each direction. 

        S.rotate(0.0f, -3.0f, 0.0f);                                            //rotate that spatial!

        S.setLocalTranslation((float)timestep[0][a][1]*6,1.0f,(float)timestep[0][a][2]*6);

            S.setMaterial((Material)assetManager.loadMaterial("Materials/retMaterial.j3m"));}

        

        else if(timestep[0][a][0] == 2){

             

        S = assetManager.loadModel("Models/distributor1.mesh.xml");

        S.scale(5f);

        S.rotate(0.0f, -3.0f, 0.0f);

        S.setLocalTranslation((float)timestep[0][a][1]*6,1.0f,(float)timestep[0][a][2]*6);

            S.setMaterial((Material)assetManager.loadMaterial("Materials/disMaterial.j3m"));}

        

          else if(timestep[0][a][0] == 3){

             

        S = assetManager.loadModel("Models/producer.mesh.xml");

        S.scale(5f);

        S.rotate(0.0f, -3.0f, 0.0f);

        S.setLocalTranslation((float)timestep[0][a][1]*6,1.0f,(float)timestep[0][a][2]*6);

            S.setMaterial((Material)assetManager.loadMaterial("Materials/proMaterial.j3m"));}

        

        else{

           

        S = assetManager.loadModel("Models/agent007.mesh.xml");

        S.scale(5f);

        S.rotate(0.0f, -3.0f, 0.0f);

        S.setLocalTranslation((float)timestep[0][a][1]*6,1.0f,(float)timestep[0][a][2]*6);

            S.setMaterial((Material)assetManager.loadMaterial("Materials/charMaterial.j3m"));}

        

        agents[a] = S;                                                          //add the spatial to an array. (this should become an array list)

                                                                                //with a seperate array list for agents that dont move. 

        rootNode.attachChild(S);                                                //add the spatial to the scene (called rootNode)

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

    

    public double[][][] gimmeData(String filepath){ //worlds most ridiculously coded notepad reader

                                                        

                                                        

                                                    //seriously, this is not for the faint of heart

                                                    

                                                    

                                                    //TURN BACK NOW. YOU CANT HANDLE THIS

        

        File select = new File(filepath);

        int numtimesteps = select.listFiles().length - 1;

        frames = numtimesteps;                      //has it burned a hole in your coder's retina yet?

        

        

       

        double info[][][] = new double[numtimesteps][36][4];



    

    for (int i = 0; i < numtimesteps; i++){

        

        String csvpath = filepath + "/testdata" + i + ".csv"; 

       

        Scanner scanning = null;

            try {

                scanning = new Scanner(new File(csvpath)).useDelimiter(",|\n");

            } catch (FileNotFoundException ex) {

                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);

            }

            

        for(int j = 0; j < 36; j++){

            for(int k = 0; k < 4; k++){

                                

                String agent_type = scanning.next();

                

                if("Work".equals(agent_type)){

                    info[i][j][k] = 0;

                }

                

                else if("Retailer".equals(agent_type)){

                    info[i][j][k] = 1;

                }

                

                else if("Distributor".equals(agent_type)){

                    info[i][j][k] = 2;

                }

                

                else if("Producer".equals(agent_type) || "SmallFarm".equals(agent_type) || "LargeFarm".equals(agent_type)){

                    info[i][j][k] = 3;

                }

                

                else if("Consumer".equals(agent_type)){

                    info[i][j][k] = 4;

                }

                

                else{

                    info[i][j][k] = Float.parseFloat(agent_type);

                }

            }

        }

        

    }

        return info;

}

}



    



