package foodsimulationmodel.environment;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.io.File;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import com.vividsolutions.jts.geom.Coordinate;

import de.lessvoid.nifty.Nifty;
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
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.engine.schedule.AbstractAction;
import repast.simphony.engine.schedule.Frequency;
import repast.simphony.engine.schedule.ScheduleParameters;
import foodsimulationmodel.agents.Consumer;
import foodsimulationmodel.agents.Distributor;
import foodsimulationmodel.agents.IAgent;
import foodsimulationmodel.agents.Producer;
import foodsimulationmodel.agents.Retailer;
import foodsimulationmodel.environment.Work;
import foodsimulationmodel.pathmapping.Junction;
import foodsimulationmodel.pathmapping.NetworkEdge;
import foodsimulationmodel.pathmapping.Scale;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class Main extends SimpleApplication{
	private String baseDir = "";
	public ArrayList<ArrayList<Spatial>> trackList = new ArrayList<ArrayList<Spatial>>();
	private boolean newBuilding = false;
	
	public static Context<Junction> jc;
	public static Network<Junction> rn;
	
	public static void main(String[] args){
		File file = new File(args[0]); // the scenario dir
		ModelRunner runner = new ModelRunner();

		try {
			runner.load(file);     // load the repast scenario
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		runner.runInitialize();  // initialize the run
		
		// get a reference to the UserObserver used for the simulation run
		jc = runner.getContext().getSubContext(GlobalVars.CONTEXT_NAMES.JUNCTION_CONTEXT);
		rn = (Network<Junction>) jc.getProjection(GlobalVars.CONTEXT_NAMES.ROAD_NETWORK);
		
		for (RepastEdge<Junction> r : rn.getEdges()){
			float rx = (float) ((NetworkEdge<Junction>)r).getRoad().getCoords().x;
			float ry = (float) ((NetworkEdge<Junction>)r).getRoad().getCoords().y;
			String rcor = ((NetworkEdge<Junction>)r).getRoad().getCoords().toString();
			System.out.println("x:"+rx+", y:"+ry+rcor);
		}
		
		double rXmax = Scale.findCritPts(rn.getEdges(),true,true);
		double rXmin = Scale.findCritPts(rn.getEdges(),false,true);
		double rYmax = Scale.findCritPts(rn.getEdges(),true,false);
		double rYmin = Scale.findCritPts(rn.getEdges(),false,false);
		System.out.println("X - max:"+rXmax+", min:"+rXmin+"; Y - max:"+rYmax+", min:"+rYmin);
		
		
		Main app = new Main();
		app.start();
		

		while (runner.getActionCount() > 0){  // loop until last action is left
			if (runner.getModelActionCount() == 0) {
				runner.setFinishing(true);}
			runner.step();  // execute all scheduled actions at next tick
		}

		runner.stop();          // execute any actions scheduled at run end
		runner.cleanUpRun();
		runner.cleanUpBatch();    // run after all runs complete
	}

	@Override
    public void simpleInitApp() { 
        setScene();
		initialScene();
		initCrossHairs();
		hudSetup();  
	}
  
    @Override
    public void simpleUpdate(float tpf) {
		
		updateMovers();
		String buildingType;
		
//		if (newBuilding){
//			updateStills(buildingType);
//		}
			
	}

    @Override
    public void simpleRender(RenderManager rm) { 
        //TODO: add render code
    }
	
	public void setScene(){
		DirectionalLight sun = new DirectionalLight();                         
        sun.setDirection (new Vector3f(-1.0f, -0.7f, -1.0f));                   
        rootNode.addLight(sun);       
        Spatial R = assetManager.loadModel(baseDir + "Textures/myTerrain.j3o");            
        rootNode.attachChild(R);       
        flyCam.setEnabled(true);                                                                                                                            
        flyCam.setMoveSpeed(50);                                                                                                           
        flyCam.setRotationSpeed(5);                                              
	}  
	public void initialScene(){
				
		ArrayList<Spatial> workList = new ArrayList<Spatial>();
		ArrayList<Spatial> retailerList = new ArrayList<Spatial>();
		ArrayList<Spatial> producerList = new ArrayList<Spatial>();
		ArrayList<Spatial> distributorList = new ArrayList<Spatial>();
		ArrayList<Spatial> consumerList = new ArrayList<Spatial>();
		float scale = (float) 6.0;
		
		Spatial S;
			   Node workNode = new Node("work");
			   rootNode.attachChild(workNode);
				   for(IAgent w: ContextManager.getAgentContext().getObjects(Work.class)){
				   S = assetManager.loadModel(baseDir + "Textures/building1.mesh.xml");
				   S.scale(5f);
				   S.rotate(0.0f, -3.0f, 0.0f);
				   Coordinate loc = ContextManager.getAgentGeometry(w).getCoordinate();
				   S.setLocalTranslation((float)loc.x*scale,1.0f,(float)loc.y*scale);
				   S.setMaterial((Material)assetManager.loadMaterial(baseDir + "Textures/workMaterial.j3m"));
				   workNode.attachChild(S);
				   workList.add(S);}
	   
			   Node retailerNode = new Node("retailer");
			   rootNode.attachChild(retailerNode);
			   for(IAgent r: ContextManager.getAgentContext().getObjects(Retailer.class)){
				   S = assetManager.loadModel(baseDir + "Textures/retailer1.mesh.xml");
				   S.scale(5f);
				   S.rotate(0.0f, -3.0f, 0.0f);
				   Coordinate loc = ContextManager.getAgentGeometry(r).getCoordinate();
				   S.setLocalTranslation((float)loc.x*scale,1.0f,(float)loc.y*scale);
				   S.setMaterial((Material)assetManager.loadMaterial(baseDir + "Textures/retMaterial.j3m"));
				   retailerNode.attachChild(S);
				   retailerList.add(S);}
	   
			   Node producerNode = new Node("producer");
			   rootNode.attachChild(producerNode);
			   for(IAgent p: ContextManager.getAgentContext().getObjects(Producer.class)){
				   S = assetManager.loadModel(baseDir + "Textures/producer.mesh.xml");
				   S.scale(5f);
				   S.rotate(0.0f, -3.0f, 0.0f);
				   Coordinate loc = ContextManager.getAgentGeometry(p).getCoordinate();
				   S.setLocalTranslation((float)loc.x*scale,1.0f,(float)loc.y*scale);
				   S.setMaterial((Material)assetManager.loadMaterial(baseDir + "Textures/proMaterial.j3m"));
				   producerNode.attachChild(S);
				   producerList.add(S);}
			   
			   Node distributorNode = new Node("distributor");
			   rootNode.attachChild(distributorNode);
			   for(IAgent d: ContextManager.getAgentContext().getObjects(Distributor.class)){
				   S = assetManager.loadModel(baseDir + "Textures/distributor1.mesh.xml");
				   S.scale(5f);
				   S.rotate(0.0f, -3.0f, 0.0f);
				   Coordinate loc = ContextManager.getAgentGeometry(d).getCoordinate();
				   S.setLocalTranslation((float)loc.x*scale,1.0f,(float)loc.y*scale);
				   S.setMaterial((Material)assetManager.loadMaterial(baseDir + "Textures/disMaterial.j3m"));
				   distributorNode.attachChild(S);
				   distributorList.add(S);}
			   
			   Node consumerNode = new Node("consumer");
			   rootNode.attachChild(consumerNode);
			   for(IAgent c: ContextManager.getAgentContext().getObjects(Consumer.class)){
				   S = assetManager.loadModel(baseDir + "Textures/agent007.mesh.xml");
				   S.scale(5f);
				   S.rotate(0.0f, -3.0f, 0.0f);
				   Coordinate loc = ContextManager.getAgentGeometry(c).getCoordinate();
				   S.setLocalTranslation((float)loc.x*scale,1.0f,(float)loc.y*scale);
				   S.setMaterial((Material)assetManager.loadMaterial(baseDir + "Textures/charMaterial.j3m"));
				   consumerNode.attachChild(S);
				   consumerList.add(S);}
			   
			   Node roadNode = new Node("road");
			   rootNode.attachChild(roadNode);
			   Iterable<RepastEdge<Junction>> rnLst = rn.getEdges();
			   double rXmax = Scale.findCritPts(rnLst,true,true);
			   double rXmin = Scale.findCritPts(rnLst,false,true);
			   double rYmax = Scale.findCritPts(rnLst,true,false);
			   double rYmin = Scale.findCritPts(rnLst,false,false);
			   for(RepastEdge<Junction> r: rnLst){
				   Coordinate rCor = ((NetworkEdge<Junction>)r).getRoad().getCoords();
				   // [-16,16] range of all Repast agent coordinates
				   double rx = Scale.corScale(rCor.x,rXmax,rXmin,(float)-16.0,(float)16.0);
				   double ry = Scale.corScale(rCor.y,rYmax,rYmin,(float)-16.0,(float)16.0);
				   S = assetManager.loadModel(baseDir + "Textures/roadtiles.mesh.xml");
				   S.scale(5f);
				   S.rotate(0.0f, -3.0f, 0.0f);
				   S.setLocalTranslation((float)rx*scale,1.0f,(float)ry*scale);
				   S.setMaterial((Material)assetManager.loadMaterial(baseDir + "Textures/roadtile.j3m"));
				   consumerNode.attachChild(S);
				   consumerList.add(S);}
			   
			   trackList.add(workList);
			   trackList.add(retailerList);
			   trackList.add(producerList);
			   trackList.add(distributorList);
			   trackList.add(consumerList);}
	public void updateMovers(){
		int i = 0;
		for(IAgent d: ContextManager.getAgentContext().getObjects(Distributor.class)){

			Coordinate loc = ContextManager.getAgentGeometry(d).getCoordinate();
			trackList.get(3).get(i).setLocalTranslation((float)loc.x*6,1.0f,(float)loc.y*6);
			i++;
			}
		int j = 0;
		for(IAgent c: ContextManager.getAgentContext().getObjects(Consumer.class)){
			Coordinate loc = ContextManager.getAgentGeometry(c).getCoordinate();
			trackList.get(4).get(j).setLocalTranslation((float)loc.x*6,1.0f,(float)loc.y*6);
			j++;
			}
	}
	protected void initCrossHairs() {
		setDisplayStatView(false);
		guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
		BitmapText ch = new BitmapText(guiFont, false);
		ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
		ch.setText("+"); // crosshairs
		ch.setLocalTranslation((Float)(settings.getWidth() / 2 - ch.getLineWidth()/2), (Float)(settings.getHeight() / 2 + ch.getLineHeight()/2), (float)0);
		guiNode.attachChild(ch);		
	 }
	
	protected void hudSetup(){
		
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
		assetManager, inputManager, audioRenderer, guiViewPort);
		Nifty nifty = niftyDisplay.getNifty();
		guiViewPort.addProcessor(niftyDisplay);
		flyCam.setDragToRotate(true);
		nifty.loadStyleFile("nifty-default-styles.xml");
		nifty.loadControlFile("nifty-default-controls.xml");
	 
		// <screen>
		nifty.addScreen("start", new ScreenBuilder("start"){{
			controller(new DefaultScreenController());
			layer(new LayerBuilder("background") {{
				childLayoutCenter();
				backgroundColor("#000f");
				
				
			}});
	 
			layer(new LayerBuilder("foreground") {{
					childLayoutVertical();
					backgroundColor("#0000");
	 
				// panel added
				panel(new PanelBuilder("panel_top") {{
					childLayoutCenter();
					alignCenter();
					backgroundColor("#f008");
					height("25%");
					width("75%");
				}});
	 
				panel(new PanelBuilder("panel_mid") {{
					childLayoutCenter();
					alignCenter();
					backgroundColor("#0f08");
					height("50%");
					width("75%");
				}});
	 
				panel(new PanelBuilder("panel_bottom") {{
					childLayoutHorizontal();
					alignCenter();
					backgroundColor("#00f8");
					height("25%");
					width("75%");
	 
					panel(new PanelBuilder("panel_bottom_left") {{
						childLayoutCenter();
						valignCenter();
						backgroundColor("#44f8");
						height("50%");
						width("50%");
					}});
	 
					panel(new PanelBuilder("panel_bottom_right") {{
						childLayoutCenter();
						valignCenter();
						backgroundColor("#88f8");
						height("50%");
						width("50%");
					}});
				}}); // panel added
			}});
	 
		}}.build(nifty));
		 
		nifty.addScreen("hud", new ScreenBuilder("hud"){{
			controller(new DefaultScreenController());
	 
			layer(new LayerBuilder("background") {{
				childLayoutCenter();
				backgroundColor("#00000000");
				// <!-- ... -->
			}});
	 
			layer(new LayerBuilder("foreground") {{
				childLayoutHorizontal();
				backgroundColor("#00000000");
	 
				// panel added
				panel(new PanelBuilder("panel_left") {{
					childLayoutVertical();
					backgroundColor("#00000000");
					height("100%");
					width("80%");
					// <!-- spacer -->
				}});
	 
				panel(new PanelBuilder("panel_right") {{
					childLayoutVertical();
					backgroundColor("#00000000");
					height("100%");
					width("20%");
	 
					panel(new PanelBuilder("panel_top_right1") {{
						childLayoutCenter();
						backgroundColor("#00000000");
						height("15%");
						width("100%");
					
						control(new ButtonBuilder("QuitButton", "QUIT") {{
							alignCenter();
							valignCenter();
							height("50%");
							width("100%");
						  }});
					}});
	 
					panel(new PanelBuilder("panel_top_right2") {{
						childLayoutCenter();
						backgroundColor("#00000000");
						height("15%");
						width("100%");
					
						control(new ButtonBuilder("QuitButton", "START") {{
							alignCenter();
							valignCenter();
							height("50%");
							width("100%");
						  }});
					}});
	 
					panel(new PanelBuilder("panel_bot_right") {{
						childLayoutCenter();
						valignCenter();
						backgroundColor("#00000000");
						height("70%");
						width("100%");
					}});
				}}); // panel added
			}});
		}}.build(nifty));
		nifty.gotoScreen("hud");
	}
	
}
