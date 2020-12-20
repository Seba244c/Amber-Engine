package dk.sebsa.amber_engine.rendering;

import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;

public class EngineRenderer {
	private static Overlay overlay;
	private static Window window = new dk.sebsa.amber_engine.rendering.windows.Editor();
	
	public static void renderScreen() {
		if(Main.window.isMinimized()) return;
		
		// Render window
		if(window.render()) { escape(false); }
		
		// Render overlay
		if(overlay != null && overlay.render()) escape(true);
		renderLabel();
		
		Renderer.unprepare();
	}
	
	public static void renderLabel() {
		if(!Main.snapshot) return;
		int tw = GUI.defaultFont.getStringWidth("Devlopment Version");
		int x = Main.window.getWidth()-tw-6;
		
		GUI.box(new Rect(x, Main.window.getHeight()-26, tw+4, 24), "Box");
		GUI.label("Devlopment Version", x+2, Main.window.getHeight()-24);
	}
	
	public static void escape() {
		if(overlay != null) {
			escape(true);
		} else { escape(false); }
	}
	
	public static void escape(boolean o) {
		if(o) {
			overlay.close();
			overlay = null;
		} else { window.close(); window = new dk.sebsa.amber_engine.rendering.windows.Editor(); }
	}
	
	public static void setOverlay(Overlay overlayIn) {
		overlay = overlayIn;
	}
	
	public static void setWindow(Window windowIn) {
		window = windowIn;
	}
}
