/*package dk.sebsa.amber.entity;

import dk.sebsa.amber.Entity;
import dk.sebsa.amber.sound.SoundManager;

public class SceneManager {
	private String currentScene = "";
	public final String currentScene() { return currentScene; }
	private SoundManager sm;
	
	public void init(SoundManager sm) {
		this.sm = sm;
	}
	
	public void loadScene(String name)
	{
		currentScene = name;
		
		BufferedReader br = null;
		if(ProjectSettings.isEditor)
		{
			FileReader fr;
			try {fr = new FileReader(Editor.WorkingDirectory() + "/Scenes/" + name + ".scene");}
			catch (FileNotFoundException e) {Debug.Log("Scene " + name + " wasnt loaded because it could not be found"); return;}
			br = new BufferedReader(fr);
		}
		else
		{
			InputStreamReader isr = new InputStreamReader(SceneManager.class.getResourceAsStream("/Scenes/" + name + ".scene"));
			br = new BufferedReader(isr);
		}
		
		Entity.clear();
		sm.clear();
		
		String line;
		
		try
		{
			GameObject g = null;
			LogicBehaviour b = null;
			
			Map<LogicBehaviour, List<String>> batch = new HashMap<LogicBehaviour, List<String>>();
			List<String> currentInfo = null;
			while((line = br.readLine()) != null)
			{
				line = line.replace("\t", "");
				String[] split = line.split("\"");
				
				//New Way to create gameobjects after parenting
				if(line.startsWith("<G")) g = CreateObject(split);
				else if(line.startsWith("<B"))
				{
					b = g.AddComponent(split[1]);
					currentInfo = new ArrayList<String>();
				}
				else if(line.startsWith("</B>"))
				{
					batch.put(b, currentInfo);
					currentInfo = null;
				}
				else if(currentInfo != null) currentInfo.add(line);
				else if(line.startsWith("<P")) g.Parent(GameObject.Find(split[1]));
			}
			
			for(LogicBehaviour behaviour : batch.keySet()) SetClass(behaviour, batch.get(behaviour));
			
			GameObject.Recalculate();
			
			List<GameObject> gos = GameObject.Instances();
			for(GameObject go : gos) {go.ResetDirty(); go.Start();}
		}
		catch (IOException e) {e.printStackTrace();}
	}
}*/
