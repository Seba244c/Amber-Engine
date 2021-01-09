package dk.sebsa.amber_engine.rendering.windows;

import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.ProjectManager;
import dk.sebsa.amber_engine.rendering.Window;

public class ProjectSettings extends Window {
	private static boolean close = false;
	
	@Override
	public boolean render() {
		Renderer.prepare();
		GUI.window(Main.window.getRect(), ProjectManager.getProjectName() + ", Settings", this::render, "Window");
		return close;
	}
	
	private void render(Rect r) {
		// Close buttons
		close = GUI.toggle(false, r.width-16, 0, null, Sprite.getSprite("BlackGUI.XClose"), 0);
		if(close == false) close = GUI.button("Clsoe Window", new Rect(0, r.height-28, r.width, 28), "Button", "ButtonHover", Press.realesed, false, 0);
	}

	@Override
	public void close() {}
}
