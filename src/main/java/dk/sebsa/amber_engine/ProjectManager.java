package dk.sebsa.amber_engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import dk.sebsa.amber_engine.windows.BootLoader;

public class ProjectManager {
	public static final String workspaceDir = System.getProperty("user.dir") + "/Amber-Workspace/";
	public static final String editorVersion = ProjectManager.class.getPackage().getImplementationVersion();
	public static byte configInit = 0;
	private static String projectDir = "";
	
	public static void init() {
		openProject(BootLoader.init());
		initConfig();
	}
	
	public static String getProjectDir() { return projectDir; }
	
	public static void openProject(String name) {
		projectDir = workspaceDir + name + "/";
		
		File dir = new File(projectDir);
		Boolean newPro = dir.mkdir();
		
		new File(projectDir + "materials/").mkdir();
		new File(projectDir + "shaders/").mkdir();
		new File(projectDir + "sprites/").mkdir();
		new File(projectDir + "textures/").mkdir();
		new File(projectDir + "scenes/").mkdir();
		new File(projectDir + "sounds/").mkdir();
		new File(projectDir + "sheets/").mkdir();
		
		if(newPro) {
			
		} else {
			
		}
	}
	
	public static void initConfig() {
		if(configInit == 1) return;
		configInit = 1;
		
		File configFile = new File(workspaceDir + "config.properties");
		
		try {
			FileReader fr = new FileReader(configFile);
			Properties p = new Properties();
			p.load(fr);
			
			String recordVersion = p.getProperty("version");
			
			if(!editorVersion.equalsIgnoreCase(recordVersion)) {
				System.out.println("Version changed!");
			} else System.out.println("Same version");
			
			fr.close();
			
			saveConfig(configFile);
		} catch (FileNotFoundException e) {
			System.out.println("New User");
			saveConfig(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void saveConfig(File configFile) {
		try {
			File dir = new File(workspaceDir);
			dir.mkdir();
			
			Properties p = new Properties();
			p.setProperty("version", editorVersion);
			
			FileWriter writer = new FileWriter(configFile);
			p.store(writer, "Amber Engine Configuration");
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
