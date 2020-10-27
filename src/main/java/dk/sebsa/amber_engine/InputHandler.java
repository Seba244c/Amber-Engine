package dk.sebsa.amber_engine;

import org.lwjgl.glfw.GLFW;

import dk.sebsa.amber.Entity;
import dk.sebsa.amber_engine.editor.Editor;
import dk.sebsa.amber_engine.windows.EngineRenderer;

public class InputHandler {
	public static void update() {
		if(Main.input.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
			EngineRenderer.escape();
		}

		if(Main.input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
			if(Main.input.isKeyPressed(GLFW.GLFW_KEY_C)) {
				// Copy
				Object inspected = Editor.getInspected();
				if(inspected != null) {
					// Copy Worldview
					if(inspected instanceof Entity) {
						Editor.worldView.copiedEntity = (Entity) Editor.getSelected();
					}
					else {
						Editor.assets.copied = inspected;
					}
				}
			} else if(Main.input.isKeyPressed(GLFW.GLFW_KEY_V)) {
				// Copy
				Object inspected = Editor.getInspected();
				if(inspected != null) {
					if(inspected instanceof Entity) {
						Editor.worldView.paste();
					}
					else {
						// PASTE ASSETS
					}
				}
			}
		}
	}
}
