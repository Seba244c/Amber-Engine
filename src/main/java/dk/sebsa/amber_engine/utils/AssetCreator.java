package dk.sebsa.amber_engine.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import dk.sebsa.amber.Entity;
import dk.sebsa.amber.AssetManager.Asset;
import dk.sebsa.amber.entity.World;
import dk.sebsa.amber.entity.WorldManager;
import dk.sebsa.amber.graph.Material;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber_engine.ProjectManager;
import dk.sebsa.amber_engine.editor.Editor;

public class AssetCreator {
	public static JSONObject templates;
	
	public static void init() {
		InputStreamReader isr =  new InputStreamReader(Sprite.class.getResourceAsStream("/const/defaults"));
		BufferedReader br = new BufferedReader(isr);
		
		try { templates = new JSONObject(br.readLine()); br.close(); } catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void create(Asset type) {
		if(type.equals(Asset.World)) {
			// Get name
			String name = askForName("world");
			if(name==null) return;
			
			// New world
			String path = ProjectManager.getProjectDir() + "worlds/" + name + ".amw";
			File f = new File(path);
			World newWorld = new World(path);
			newWorld.name = name;
			
			try { f.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
			WorldManager.openWorld(newWorld);
			Editor.setSelected(new Entity(true));
			Editor.action();
		} else if (type.equals(Asset.Sprite)) {
			String[] pn = createAssetFromTemplate("sprite", "sprites", "spr");
			// Create sprite
			Sprite newSprite = new Sprite(pn[0]);
			newSprite.name = pn[1];
		} else if (type.equals(Asset.Material)) {
			String[] pn = createAssetFromTemplate("material", "materials", "mat");
			// Create sprite
			Material newMaterial = new Material(pn[0]);
			newMaterial.name = pn[1];
		}
	}
	
	public static String[] createAssetFromTemplate(String type, String folder, String ext) {
		// Get name
		String name = askForName(type);
		if(name==null) return null;
		
		// Crate File
		String path = ProjectManager.getProjectDir() + folder + "/" + name;
		File f = new File(path+ "."+ext);
		try { f.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
		writeToFile(f, templates.getString(type));
		
		String[] pn = new String[] {path, name};
		return pn;
		
	}
	
	public static void writeToFile(File f, String s) {
		FileWriter fw;
		try {
			fw = new FileWriter(f);
			fw.write(s);
			fw.close();
		} catch (IOException e) { e.printStackTrace(); }
		
	}
	
	public static String askForName(String type) {
		String name = TinyFileDialogs.tinyfd_inputBox("Creating new " + type + "!", "What should the " + type + " be named?", "");
		
		// Handle string
		if(name==null) return null;
		name = name.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "");
		if(name==null) return null;
		if(name.equals("") || name.startsWith(" ")) return null;
		return name;
	}
}
