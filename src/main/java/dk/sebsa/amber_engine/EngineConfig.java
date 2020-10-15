package dk.sebsa.amber_engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class EngineConfig {
	public static byte configInit = 0;
	
	public static boolean configEditorVsync = true;
	
	public static boolean initConfig() {
		if(configInit == 1) return false;
		configInit = 1;
		boolean bool = false;
		
		File configFile = new File(ProjectManager.workspaceDir + "config.properties");
		
		try {
			FileReader fr = new FileReader(configFile);
			Properties p = new Properties();
			p.load(fr);
			
			String recordVersion = p.getProperty("version");
			configEditorVsync = Boolean.parseBoolean(p.getProperty("editor_vsync"));
			
			// Check if updated, and changelog needs to be showed
			if(!Main.editorVersion.equalsIgnoreCase(recordVersion)) {
				if(!Main.editorVersion.contains("-SNAPSHOT")) bool = true;
			}
			
			fr.close();
			
			saveConfig(configFile);
		} catch (FileNotFoundException e) {
			saveConfig(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bool;
	}
	
	public static void saveConfig() {
		File configFile = new File(ProjectManager.workspaceDir + "config.properties");
		saveConfig(configFile);
	}
	
	private static void saveConfig(File configFile) {
		try {
			File dir = new File(ProjectManager.workspaceDir);
			dir.mkdir();
			
			Properties p = new Properties();
			p.setProperty("version", Main.editorVersion);
			p.setProperty("editor_vsync", Boolean.toString(configEditorVsync));
			
			FileWriter writer = new FileWriter(configFile);
			p.store(writer, "Amber Engine Configuration");
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
