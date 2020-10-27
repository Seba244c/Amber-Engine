package dk.sebsa.amber_engine.editor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import dk.sebsa.amber.Entity;
import dk.sebsa.amber.entity.Scene;
import dk.sebsa.amber.entity.SceneManager;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber.util.Logger;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.ProjectManager;
import dk.sebsa.amber_engine.windows.EngineRenderer;
import dk.sebsa.amber_engine.windows.EngineRenderer.windows;

public class Menubar {
	public Map<String, List<MenuItem>> menu = new LinkedHashMap<String, List<MenuItem>>();
	
	private Sprite box;
	private Sprite empty;
	
	private String selected;
	private Color prevColor;
	
	public Sprite playMode;
	public Sprite stopButton, playButton;
	
	public Menubar() {
		box = Sprite.getSprite(GUI.sheet+".Box");
		playButton = Sprite.getSprite(GUI.sheet+".PlayButton");
		stopButton = Sprite.getSprite(GUI.sheet+".StopButton");
		playMode = playButton;
		
		add("File", new MenuItem("New Scene", this::file));
		add("File", new MenuItem("Save Scene", this::file));
		//add("File", new MenuItem("Export", this::file));
		add("File", new MenuItem("Quit", this::file));
		
		add("Asset", new MenuItem("New Entity", this::asset));
		add("Asset", new MenuItem("New Sprite", this::asset));
		add("Asset", new MenuItem("New SpriteSheet", this::asset));
		add("Asset", new MenuItem("New Material", this::asset));
		add("Asset", new MenuItem("New Shader", this::asset));
		add("Asset", new MenuItem("Import Sound", this::asset));
		add("Asset", new MenuItem("Import Texture", this::asset));
		
		add("Edit", new MenuItem("Project Settings", this::edit));
		add("Edit", new MenuItem("Engine Settings", this::edit));
	}
	
	public void render() {
		float w = Main.window.getWidth();
		if(selected != null && !GUI.hasPopup()) {
			selected = null;
		}
		GUI.box(new Rect(0, 0, w, 30), box);
		
		prevColor = GUI.textColor;
		GUI.textColor = Color.white();
		
		float offset = 0;
		float index = 0;
		
		for(String m : menu.keySet()) {
			float width = GUI.defaultFont.getStringWidth(m) + 10;
			Rect nameRect = new Rect(offset-index, 0, width, 30);
			if(selected != null && selected.equals(m)) {
				if(GUI.button(m, nameRect, box, box, Press.pressed, true)) {
					selected = null;
					GUI.removePopup();
				}
			} else {
				if(GUI.button(m, nameRect, empty, box, Press.pressed, true)) {
					List<MenuItem> list = menu.get(m);
					List<String> v = new ArrayList<String>();
					for(int i = 0; i < list.size(); i++) v.add(list.get(i).name);
					GUI.setPopup(nameRect, v, this::clicked);
					selected = m;
				}
			}
			offset += width;
			index++;
		}
		
		GUI.textColor = prevColor;
		
		if(GUI.button("", new Rect(Main.window.getWidth()/2-16, 6, 18, 18), playMode, playMode, Press.pressed, false)) {
			Main.togglePlaymode();
		}
	}
	
	public void clicked(String v) {
		List<MenuItem> list = menu.get(selected);
		for(MenuItem item : list)
			if(item.name.equals(v)) {
				item.accept();
				return;
		}
	}
	
	public void add(String parent, MenuItem item) {
		List<MenuItem> menuItems = menu.get(parent);
		if(menuItems == null) {
			menuItems = new ArrayList<MenuItem>();
			menu.put(parent, menuItems);
		}
		menuItems.add(item);
	}
	
	public void file(MenuItem m) {
		if(m.name.equals("Quit")) GLFW.glfwSetWindowShouldClose(Main.window.windowId, true);
		else if(m.name.equals("New Scene")) {
			String name = TinyFileDialogs.tinyfd_inputBox("Creating new scene!", "What should the scene be named?", "");
			
			// Handle string
			if(name==null) return;
			name = name.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "");
			if(name==null) return;
			if(name.equals("") || name.startsWith(" ")) return;
			
			// New scene
			String path = ProjectManager.getProjectDir() + "scenes/" + name + ".amw";
			File f = new File(path);
			try { f.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
			SceneManager.loadScene(path);
			new Scene(name);
		} else if(m.name.equals("Save Scene")) {
			try {
				SceneManager.saveScene(SceneManager.getCurrentScene());
			} catch (IOException e) {
				Logger.errorLog("Menubar", "file", "Could not save scene: " + SceneManager.getCurrentScene());
			}
		}
	}
	
	public void asset(MenuItem m) {
		if(m.name.equals("New Entity")) {
			Editor.setSelected(new Entity(true));
		} else if(m.name.equals("New Sprite")) {
			
		} else if(m.name.equals("New SpriteSheet")) {
			
		} else if(m.name.equals("New Material")) {
			
		} else if(m.name.equals("New Shader")) {
			
		} else if(m.name.equals("Import Texture")) {
			
		} else if(m.name.equals("Import Sound")) {
			
		}
	}
	
	public void edit(MenuItem m) {
		if(m.name.equals("Project Settings")) {
			EngineRenderer.setWindow(windows.projectSettings);
		} else if(m.name.equals("Engine Settings")) {
			EngineRenderer.setWindow(windows.engineSettings);
		}
	}
}

