/*package dk.sebsa.amber.entity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.sebsa.amber.Entity;
import dk.sebsa.amber.sound.SoundManager;
import dk.sebsa.amber.util.Logger;

public class SceneManager {
	private Scene currentScene;
	public final Scene currentScene() { return currentScene; }
	private SoundManager sm;
	
	public void init(SoundManager sm) {
		this.sm = sm;
	}
	
	public void loadScene(Scene sc)
	{
		currentScene = sc;
		
		BufferedReader br = null;
		if(!sc.name.startsWith("/"))
		{
			FileReader fr;
			try {fr = new FileReader(sc.name + ".sc");}
			catch (FileNotFoundException e) { Logger.errorLog("SceneManager", "Scene " + sc.name + " wasnt loaded because it could not be found"); return;}
			br = new BufferedReader(fr);
		}
		else
		{
			InputStreamReader isr = new InputStreamReader(SceneManager.class.getResourceAsStream("/scenes" + sc.name + ".sc"));
			br = new BufferedReader(isr);
		}
		
		Entity.clear();
		sm.clear();
		
		String line;
		
		try
		{
			Entity g = null;
			Component b = null;
			
			Map<Component, List<String>> batch = new HashMap<Component, List<String>>();
			List<String> currentInfo = null;
			while((line = br.readLine()) != null)
			{
				line = line.replace("\t", "");
				String[] split = line.split("\"");
				
				//New Way to create gameobjects after parenting
				if(line.startsWith("<E")) g = CreateEntity(split);
				else if(line.startsWith("<B"))
				{
					b = g.addComponent(ComponentImporter.getComponent(split[1]));
					currentInfo = new ArrayList<String>();
				}
				else if(line.startsWith("</B>"))
				{
					batch.put(b, currentInfo);
					currentInfo = null;
				}
				else if(currentInfo != null) currentInfo.add(line);
				else if(line.startsWith("<P")) g.parent(Entity.getEntity(split[1]));
			}
			
			for(Component c : batch.keySet()) SetClass(c, batch.get(c));
						
			List<Entity> es = Entity.getInstances();
			for(Entity e : es) { e.resetDirty(); }
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	private static Entity CreateEntity(String[] split)
	{
		Entity g = new Entity(split[1]);
		
		String[] tSplit = split[3].split(" ");
		g.setPosition(Float.parseFloat(tSplit[0]), Float.parseFloat(tSplit[1]));
		
		tSplit = split[5].split(" ");
		g.setScale(Float.parseFloat(tSplit[0]), Float.parseFloat(tSplit[1]));
		
		g.setRotation(Float.parseFloat(split[7]));
		
		g.setId(split[9]);
		
		return g;
	}
	
	
}*/
