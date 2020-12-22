package dk.sebsa.amber_engine.rendering.windows;

import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.editor.Editor;
import dk.sebsa.amber_engine.rendering.Window;

public class Play extends Window {
	@Override
	public boolean render() {
		// Render game
		Renderer.render(new Rect(0, 30, Main.window.getWidth(), Main.window.getHeight()-30));
		
		// Render menubar, components and popups
		Renderer.prepare();
		Editor.menubar.render();
		Component.willRenderAll();
		GUI.drawPopup();
		return false;
	}

	@Override
	public void close() {
		
	}

}
