package dk.sebsa.amber_engine;

import org.lwjgl.glfw.GLFW;

import dk.sebsa.amber_engine.windows.EngineRenderer;

public class InputHandler {
	public static void update() {
		if(Main.input.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
			EngineRenderer.escape();
		}

		if(Main.input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
			
		}
	}
}
