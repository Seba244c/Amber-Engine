package dk.sebsa.amber_engine;

import static org.lwjgl.glfw.GLFW.*;

import java.io.IOException;

import dk.sebsa.amber.Entity;
import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.entity.components.SpriteRenderer;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.graph.Shader;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.io.DevWindow;
import dk.sebsa.amber.io.Input;
import dk.sebsa.amber.io.Window;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber.sound.SoundListener;
import dk.sebsa.amber.sound.SoundManager;

public class Main {
	public static Window window;
	public static Input input;
	public static Shader engineShader;
	
	public static SoundManager sm;
	public static SoundListener sl;
	
	public static Loading loadingScreen;
	
	public static void main(String[] args) {
		// Project loading
		ProjectManager.init();
		
		// Loading screen
		loadingScreen = new Loading();
		loadingScreen.reset("Starting Engine");
		
		// Main loop
		init();
		
		try {
			start();
			
			// Dispose loading screen
			loadingScreen.close();
			
			loop();
		} catch (IOException e) { e.printStackTrace(); }
		
		cleanup();
	}
	
	public static void init() {
		// Dev Console
		loadingScreen.setStatus("Loading Dev Console", 0);
		DevWindow.useDevWindow("Amber Engine Dev Console");
		if (System.getProperty("os.name").contains("Mac"))
	        System.setProperty("java.awt.headless", "true");
		
		// Create Classes
		loadingScreen.setStatus("Creating Classes", 10);
		window = new Window("Amber Engine", 960, 800, true, false);
		input = new Input(window);
		sm = new SoundManager();
	}
	
	public static void start() throws IOException {
		loadingScreen.setStatus("Initializing, Window & Input", 20);
		window.init(Color.white());
		loadingScreen.setStatus("Initializing, Window & Input", 50);
		input.init();
		
		try {
			loadingScreen.setStatus("Initializing, SoundManager & SoundListener", 70);
			// Sound
			sm.init();
			sl = new SoundListener();
			sm.setListener(sl);
		} catch (Exception e1) { e1.printStackTrace(); }
		
		loadingScreen.setStatus("Initializing, Renderer", 85);
		Editor.init();
		Renderer.init();

		// Set loading text
		loadingScreen.reset("Loading Project");
		
		// Load all assets
		AssetManager.loadAllResources();
		
		engineShader = Shader.findShader("engine");
	}
	
	public static void loop() throws IOException {
		Entity e = new Entity("Jens");
		e.addComponent(new SpriteRenderer());
		((SpriteRenderer) e.getComponent("SpriteRenderer")).sprite = Sprite.getSprite("player_idle");
		/*
		SoundSource sc = new SoundSource(true, false);
		sc.setClip(AudioClip.getClip("arena1.ogg"));
		sc.setPosition(new Vector2f(0, 0));
		sc.play();*/
		
		while(!window.shouldClose()) {
			glfwPollEvents();
			if(!window.isMinimized()) {
				// Updates
				window.update();
				DevWindow.update();
				input.update();
				
				// Logic
				Component.updateAll();
				sm.updateListenerPosition(new Vector2f(0, 0));
				
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
		sm.cleanup();
		
		// Other
		System.gc();
		
	}
}
