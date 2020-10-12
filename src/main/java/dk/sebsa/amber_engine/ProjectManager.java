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
	private static String projectName = "";
	
	public static boolean EconfigEditorVsync = true;
	
	public static boolean init() {
		openProject(BootLoader.init());
		return initEConfig();
	}
	
	public static String getProjectDir() { return projectDir; }
	public static String getProjectName() { return projectName; }
	
	public static void openProject(String name) {
		projectDir = workspaceDir + name + "/";
		projectName = name;
		
		File dir = new File(projectDir);
		Boolean newPro = dir.mkdir();
		
		new File(projectDir + "materials/").mkdir();
		new File(projectDir + "shaders/").mkdir();
		new File(projectDir + "sprites/").mkdir();
		new File(projectDir + "textures/").mkdir();
		new File(projectDir + "scenes/").mkdir();
		new File(projectDir + "sounds/").mkdir();
		new File(projectDir + "sheets/").mkdir();
		new File(projectDir + "scripts/").mkdir();
		
		if(newPro) {
			// Creative projectconfig
		} else {
			// Load project config
		}
	}
	
	public static boolean initEConfig() {
		if(configInit == 1) return false;
		configInit = 1;
		boolean bool = false;
		
		File configFile = new File(workspaceDir + "config.properties");
		
		try {
			FileReader fr = new FileReader(configFile);
			Properties p = new Properties();
			p.load(fr);
			
			String recordVersion = p.getProperty("version");
			EconfigEditorVsync = Boolean.parseBoolean(p.getProperty("editor_vsync"));
			
			if(!editorVersion.equalsIgnoreCase(recordVersion)) {
				System.out.println("Version changed!");
				if(!editorVersion.contains("-SNAPSHOT")) bool = true;
			} else System.out.println("Same version");
			
			fr.close();
			
			saveEConfig(configFile);
		} catch (FileNotFoundException e) {
			System.out.println("New User");
			saveEConfig(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bool;
	}
	
	public static void saveEConfig() {
		File configFile = new File(workspaceDir + "config.properties");
		saveEConfig(configFile);
	}
	
	private static void saveEConfig(File configFile) {
		try {
			File dir = new File(workspaceDir);
			dir.mkdir();
			
			Properties p = new Properties();
			p.setProperty("version", editorVersion);
			p.setProperty("editor_vsync", Boolean.toString(EconfigEditorVsync));
			
			FileWriter writer = new FileWriter(configFile);
			p.store(writer, "Amber Engine Configuration");
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
