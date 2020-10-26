package dk.sebsa.amber_engine.windows;

import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.editor.Editor;

public class EngineRenderer {
	public static windows currentWindow = windows.main;
	
	public static enum windows {
		changelog,
		main,
		playmode,
		engineSettings,
		projectSettings
	}
	
	public static void renderScreen() {
		if(Main.window.isMinimized()) return;
		
		if(currentWindow.equals(windows.changelog)) {
			boolean bool = Changelog.render();
			
			if(bool) currentWindow = windows.main;
		} else if(currentWindow.equals(windows.engineSettings)) {
			boolean bool = EngineSettings.render();
			
			if(bool) currentWindow = windows.main;
		} else if(currentWindow.equals(windows.projectSettings)) {
			boolean bool = ProjectSettings.render();
			
			if(bool) currentWindow = windows.main;
		} else if(currentWindow.equals(windows.playmode)) {
			// Render
			Renderer.render(new Rect(0, 30, Main.window.getWidth(), Main.window.getHeight()-30));
			
			// Render GUI, placed after for renderLabel() to work
			Renderer.prepare();
			
			Editor.menubar.render();
			Component.willRenderAll();
			renderLabel();
			
			Renderer.unprepare();
		} else {
			// Render
			Renderer.render(new Rect(300, 30, Main.window.getWidth()-600, Main.window.getHeight()-430));
			
			// Render editor
			Renderer.prepare();
			
			Editor.render();
			Component.willRenderAll();
			GUI.drawPopup();
			renderLabel();
			
			Renderer.unprepare();
		}
	}
	
	public static void renderLabel() {
		if(!Main.snapshot) return;
		int tw = GUI.defaultFont.getStringWidth("Devlopment Version");
		int x = Main.window.getWidth()-tw-6;
		
		GUI.box(new Rect(x, Main.window.getHeight()-26, tw+4, 24), "Box");
		GUI.label("Devlopment Version", x+2, Main.window.getHeight()-24);
	}
	
	public static void setWindow(windows window) {
		currentWindow = window;
	}
	
	public static void escape() {
		if(currentWindow.equals(windows.main)) return;
		
		if(currentWindow.equals(windows.playmode)) {
			Main.togglePlaymode();
		} else if(currentWindow.equals(windows.changelog)) {
			currentWindow = windows.main;
		} else if(currentWindow.equals(windows.engineSettings)) {
			currentWindow = windows.main;
		} else if(currentWindow.equals(windows.projectSettings)) {
			currentWindow = windows.main;
		}
	}
}
