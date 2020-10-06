package dk.sebsa.amber_engine;

import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber.util.Logger;

public class Editor {	
	public static void init() {
		
	}
	
	public static void render() {
		Renderer.prepare();
		GUI.window(new Rect(0, 30, 300, Main.window.getHeight()-30), "World View", Editor::test, "Window");
		GUI.window(new Rect(Main.window.getWidth()-300, 30, 300, Main.window.getHeight()-30), "Inspector", Editor::test, "Window");
		Renderer.unprepare();
	}
	
	public static void cleanup() {
		
	}
	
	public static void test(Rect rect) {
		
	}
}
