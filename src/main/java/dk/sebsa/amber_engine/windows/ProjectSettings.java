package dk.sebsa.amber_engine.windows;

import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.ProjectManager;

public class ProjectSettings {
private static boolean close = false;
	
	public static boolean render() {
		Renderer.prepare();
		GUI.window(Main.window.getRect(), ProjectManager.getProjectName() + ", Settings", ProjectSettings::render, "Window");
		Renderer.unprepare();
		return close;
	}
	
	private static void render(Rect r) {
		// Close buttons
		close = GUI.toggle(false, r.width-16, 0, null, Sprite.getSprite("BlackGUI.XClose"));
		if(close == false) close = GUI.button("Clsoe Changelog", new Rect(0, r.height-28, r.width, 28), "Button", "ButtonHover", Press.realesed, false);
	}
}
