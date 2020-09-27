package dk.sebsa.amber_engine;

import static org.lwjgl.glfw.GLFW.*;

import dk.sebsa.amber.io.DevWindow;
import dk.sebsa.amber.io.Input;
import dk.sebsa.amber.io.Window;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.util.Logger;

public class Main {
	public static Window window;
	public static Input input;
	
	public static void main(String[] args) {
		init();
		
		start();
		loop();
		
		cleanup();
	}
	
	public static void init() {
		DevWindow.useDevWindow("Amber Engine Dev Console");
		if (System.getProperty("os.name").contains("Mac"))
	        System.setProperty("java.awt.headless", "true");
		window = new Window("Amber Engine", 960, 800, true, false);
		input = new Input(window);
	}
	
	public static void start() {
		window.init(Color.white());
		input.init();
	}
	
	public static void loop() {
		while(!window.shouldClose()) {
			glfwPollEvents();
			if(!window.isMinimized()) {
				// Updates
				window.update();
				DevWindow.update();
				input.update();
				
				// Other
				if(input.isButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) Logger.infoLog("Main", "Left Clicking");
				
				// Lates
				input.late();
			}
			glfwSwapBuffers(window.windowId);
		}
	}
	
	public static void cleanup() {
		window.cleanup();
		input.cleanup();
		//DevWindow.destroyDevWindow();
		System.gc();
	}
}
