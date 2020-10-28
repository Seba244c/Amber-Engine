package dk.sebsa.amber_engine;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class ProjectManager {
	public static final String workspaceDir = System.getProperty("user.dir") + "/Amber-Workspace/";
	private static String projectDir = "";
	private static String projectName = "";

	public static String getProjectDir() { return projectDir; }
	public static String getProjectName() { return projectName; }
	
	public static String configDefaultWorld = "DefaultWorld";
	
	public static void openProject(String name) throws IOException {
		projectDir = workspaceDir + name + "/";
		projectName = name;
		
		File dir = new File(projectDir);
		Boolean newPro = dir.mkdir();
		
		new File(projectDir + "materials/").mkdir();
		new File(projectDir + "shaders/").mkdir();
		new File(projectDir + "sprites/").mkdir();
		new File(projectDir + "textures/").mkdir();
		new File(projectDir + "worlds/").mkdir();
		new File(projectDir + "sounds/").mkdir();
		new File(projectDir + "sheets/").mkdir();
		new File(projectDir + "scripts/").mkdir();
		new File(projectDir + "tags.txt").createNewFile();
		
		File configFile = new File(ProjectManager.projectDir + "config.properties");
		if(newPro) {
			// Creative projectconfig
			try { configFile.createNewFile(); } catch (IOException e1) { e1.printStackTrace(); }
			saveConfig();
			
			// Create new world
			String path = ProjectManager.getProjectDir() + "worlds/DefaultWorld.amw";
			File f = new File(path);
			f.createNewFile();
		} else {
			// Load project config !
			// File
			FileReader fr = new FileReader(configFile);
			Properties p = new Properties();
			p.load(fr);
			
			// Configs
			configDefaultWorld = p.getProperty("default_world");
			
			// close
			fr.close();
			
			// Reset
			saveConfig();
		}
	}
	
	public static void saveConfig() {
		File configFile = new File(ProjectManager.projectDir + "config.properties");
		saveConfig(configFile);
	}
	
	private static void saveConfig(File configFile) {
		try {
			Properties p = new Properties();
			p.setProperty("default_world", configDefaultWorld);
			
			FileWriter writer = new FileWriter(configFile);
			p.store(writer, "Amber Project Configuration");
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
