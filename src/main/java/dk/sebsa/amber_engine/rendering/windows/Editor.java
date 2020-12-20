package dk.sebsa.amber_engine.rendering.windows;

import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.rendering.Window;

public class Editor extends Window {
	@Override
	public boolean render() {
		// Render game
		if(Main.inPlayMode) Renderer.render(new Rect(0, 30, Main.window.getWidth(), Main.window.getHeight()-30));
		else Renderer.render(new Rect(300, 30, Main.window.getWidth()-600, Main.window.getHeight()-430));
		
		// Render editor
		Renderer.prepare();
		if(!Main.inPlayMode) dk.sebsa.amber_engine.editor.Editor.render();
		Component.willRenderAll();
		GUI.drawPopup();
		return false;
	}

	@Override
	public void close() {}
}
