package dk.sebsa.amber_engine;

import static org.lwjgl.glfw.GLFW.*;

import java.io.IOException;

import dk.sebsa.amber.Entity;
import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.entity.components.SpriteRenderer;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.graph.Shader;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.graph.text.Font;
import dk.sebsa.amber.io.DevWindow;
import dk.sebsa.amber.io.Input;
import dk.sebsa.amber.io.Window;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber.sound.SoundListener;
import dk.sebsa.amber.sound.SoundManager;
import dk.sebsa.amber_engine.editor.Editor;
import dk.sebsa.amber_engine.windows.Changelog;
import dk.sebsa.amber_engine.windows.Loading;
import dk.sebsa.amber_engine.windows.EngineRenderer;
import dk.sebsa.amber_engine.windows.EngineSettings;
import dk.sebsa.amber_engine.windows.EngineRenderer.windows;

public class Main {
	public static Window window;
	public static Input input;
	public static Shader engineShader;
	
	public static SoundManager sm;
	public static SoundListener sl;
	
	public static Loading loadingScreen;
	
	public static boolean changeLog;
	public static boolean inPlayMode = false;
	
	public static void main(String[] args) {
		// auto update
		if(!ProjectManager.editorVersion.contains("SNAPSHOT")) {
			System.out.println("Checking for updates");
			
			if(AutoUpdate.needUpdate()) {
				AutoUpdate.update();
			} else {
				System.out.println("Already using newest version");
			}
			
		} else {
			System.out.println("Using dev-version not checking for updates");
		}
		
		// Project loading
		changeLog = ProjectManager.init();
		
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
		window = new Window("Amber Engine", 960, 800, ProjectManager.EconfigEditorVsync, false);
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
		Renderer.init(input);
		
		// Set loading text
		loadingScreen.reset("Loading Project");
		
		// Load all assets
		AssetManager.loadAllResources();
		
		Editor.init();
		EngineSettings.init();
		engineShader = Shader.findShader("engine");
		
		if(changeLog) { Changelog.load(); EngineRenderer.setWindow(windows.changelog); }
	}
	
	public static void loop() throws IOException {
		Entity yang = new Entity("Yang");
		
		Entity b = new Entity("b"); b.parent(yang);
		Entity a = new Entity("a"); a.parent(yang);
		Entity c = new Entity("c"); c.parent(b);
		
		Entity ying = new Entity("Ying");
		ying.addComponent(new SpriteRenderer());
		ying.addComponent(new Foo());
		((SpriteRenderer) ying.getComponent("SpriteRenderer")).sprite = Sprite.getSprite("player.idle");
		while(!window.shouldClose()) {
			glfwPollEvents();
			
			// Updates
			window.update();
			input.update();
			
			// Logic
			if(inPlayMode) {
				Component.updateAll();
				sm.updateListenerPosition(new Vector2f(0, 0));
			}
			
			// Render
			EngineRenderer.renderScreen();
			
			// Lates
			input.late();
			
			glfwSwapBuffers(window.windowId);
		}
	}
	
	public static void cleanup() {
		AutoUpdate.close();

		// General cleanup
		window.cleanup();
		input.cleanup();
		Renderer.cleanup();
		Font.cleanUPAll();
		
		// Assets
		AssetManager.cleanUpAll();
		sm.cleanup();
		
		// Other
		System.gc();
		
	}
}
