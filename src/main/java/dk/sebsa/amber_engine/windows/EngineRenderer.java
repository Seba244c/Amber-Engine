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
		main
	}
	
	public static void renderScreen() {
		if(currentWindow.equals(windows.changelog)) {
			boolean bool = Changelog.render();
			if(bool) currentWindow = windows.main;
		} else if(!Main.window.isMinimized()) {
			// Render
			Renderer.render(new Rect(300, 30, Main.window.getWidth()-600, Main.window.getHeight()-430));
			
			Renderer.prepare();
			
			Editor.render();
			Component.willRenderAll();
			GUI.drawPopup();
			
			Renderer.unprepare();
		}
	}
	
	public static void setWindow(windows window) {
		currentWindow = window;
	}
}
