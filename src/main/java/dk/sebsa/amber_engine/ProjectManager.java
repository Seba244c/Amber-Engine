package dk.sebsa.amber_engine;

import java.io.File;
import java.io.IOException;

public class ProjectManager {
	public static final String workspaceDir = System.getProperty("user.dir") + "/Amber-Workspace/";
	private static String projectDir = "";
	private static String projectName = "";

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
		try {
			new File(projectDir + "tags.txt").createNewFile();
		} catch (IOException e) { e.printStackTrace(); }
		
		if(newPro) {
			// Creative projectconfig
		} else {
			// Load project config
		}
	}
}
