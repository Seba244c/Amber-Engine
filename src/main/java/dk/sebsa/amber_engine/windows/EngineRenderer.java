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
			Renderer.prepare();
			
			Editor.menubar.render();
			Component.willRenderAll();
			
			Renderer.unprepare();
			
			// Render
			Renderer.render(new Rect(0, 30, Main.window.getWidth(), Main.window.getHeight()-30));
			
			
		} else {
			Renderer.prepare();
			
			Editor.render();
			Component.willRenderAll();
			GUI.drawPopup();
			
			Renderer.unprepare();
			
			// Render
			Renderer.render(new Rect(300, 30, Main.window.getWidth()-600, Main.window.getHeight()-430));
		}
	}
	
	public static void setWindow(windows window) {
		currentWindow = window;
	}
}
