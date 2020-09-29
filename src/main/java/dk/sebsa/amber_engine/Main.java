package dk.sebsa.amber_engine;

import static org.lwjgl.glfw.GLFW.*;

import java.io.IOException;

import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.graph.Shader;
import dk.sebsa.amber.io.DevWindow;
import dk.sebsa.amber.io.Input;
import dk.sebsa.amber.io.Window;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Rect;

public class Main {
	public static Window window;
	public static Input input;
	public static Shader engineShader;
	
	public static void main(String[] args) {
		// Project loading
		ProjectManager.init();
		
		// Main loop
		init();
		
		try {
			start();
			loop();
		} catch (IOException e) { e.printStackTrace(); }
		
		cleanup();
	}
	
	public static void init() {
		DevWindow.useDevWindow("Amber Engine Dev Console");
		if (System.getProperty("os.name").contains("Mac"))
	        System.setProperty("java.awt.headless", "true");
		window = new Window("Amber Engine", 960, 800, true, false);
		input = new Input(window);
	}
	
	public static void start() throws IOException {
		window.init(Color.white());
		input.init();
		
		try {
			AssetManager.loadAllResources();
		} catch (IOException e1) { e1.printStackTrace(); }
		
		Editor.init();
		Renderer.init();

		engineShader = Shader.findShader("engine");
	}
	
	public static void loop() throws IOException {
		while(!window.shouldClose()) {
			glfwPollEvents();
			if(!window.isMinimized()) {
				// Updates
				window.update();
				DevWindow.update();
				input.update();
				
				// Logic
				Component.updateAll();
				
				// Render
				Component.willRenderAll();
				Editor.render();
				Renderer.render(new Rect(300, 30, window.getWidth()-600, window.getHeight()-30));
				
				// Lates
				input.late();
			}
			glfwSwapBuffers(window.windowId);
		}
	}
	
	public static void cleanup() {
		// General cleanup
		window.cleanup();
		input.cleanup();
		Editor.cleanup();
		Renderer.cleanup();
		
		// Assets
		AssetManager.cleanUpAll();
		
		// Other
		System.gc();
		
	}
}
