package dk.sebsa.amber_engine.editor;

import java.io.File;
import java.io.IOException;

import org.lwjgl.util.tinyfd.TinyFileDialogs;

import dk.sebsa.amber.AssetManager.Asset;
import dk.sebsa.amber.Entity;
import dk.sebsa.amber.entity.World;
import dk.sebsa.amber.entity.WorldManager;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.ProjectManager;
import dk.sebsa.amber_engine.editor.windows.Assets;
import dk.sebsa.amber_engine.editor.windows.Inspector;
import dk.sebsa.amber_engine.editor.windows.Types;
import dk.sebsa.amber_engine.editor.windows.WorldView;

public class Editor {	
	public static Menubar menubar;
	
	public static WorldView worldView;
	public static Inspector inspector;
	public static Types types;
	public static Assets assets;
	
	private static Entity selected;
	private static Object inspected;
	
	public static Sprite arrowDown;
	public static Sprite arrowRight;
	public static Sprite window;
	public static Sprite button;
	public static Sprite buttonHover;
	public static Sprite x;
	private static Sprite addCircle;
	
	public static void init() {
		menubar = new Menubar();
		
		arrowDown = Sprite.getSprite(GUI.sheet+".ArrowDown");
		arrowRight = Sprite.getSprite(GUI.sheet+".ArrowRight");
		window = Sprite.getSprite(GUI.sheet+".Window");
		button = Sprite.getSprite(GUI.sheet +".Button");
		buttonHover = Sprite.getSprite(GUI.sheet +".ButtonHover");
		addCircle = Sprite.getSprite(GUI.sheet+".AddCircle");
		x = Sprite.getSprite(GUI.sheet +".XClose");
		
		worldView = new WorldView();
		inspector = new Inspector();
		assets = new Assets();
		types = new Types();
	}
	
	public static void render() {
		menubar.render();
		GUI.window(new Rect(0, 30, 300, Main.window.getHeight()-430), "World View", worldView::render, window);
		GUI.window(new Rect(Main.window.getWidth()-300, 30, 300, Main.window.getHeight()-30), "Inspector", inspector::render, window);
		GUI.window(new Rect(0, Main.window.getHeight()-400, 300, 400), "Asset Types", types::render, window);
		GUI.window(new Rect(300, Main.window.getHeight()-400, Main.window.getWidth()-600, 400), "Assets", assets::render, window);
		if(GUI.toggle(false, Main.window.getWidth()-319, Main.window.getHeight()-395, addCircle, addCircle)) newAsset();
	}
	
	public static void newAsset() {
		newAsset(types.type);
	}
	
	public static void newAsset(Asset assetType) {
		if(assetType.equals(Asset.World)) {
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
		} else if (assetType.equals(Asset.Sprite)) {
			// Get name
			String name = askForName("sprite");
			if(name==null) return;
			
			// Crate File
			String path = ProjectManager.getProjectDir() + "sprites/" + name;
			File f = new File(path+ ".spr");
			try { f.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
			
			// Create sprite
			Sprite newSprite = new Sprite(path);
			newSprite.name = name;
		}
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
	
	public static Object getSelected() {
		return selected;
	}
	
	public static void setSelected(Entity select) {
		selected = select;
		setInspected(select);
	}
	
	public static Object getInspected() {
		return inspected;
	}
	
	public static void setInspected(Object inspect) {
		inspected = inspect;
		if(inspect!=null) inspector.setAttributes(inspect);
	}
	
	public static void action() {
		WorldManager.getWorld().saved = false;
	}
}
