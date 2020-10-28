package dk.sebsa.amber_engine;


import static org.lwjgl.glfw.GLFW.*;

import java.io.IOException;

import dk.sebsa.amber.AssetManager;
import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.entity.ComponentImporter;
import dk.sebsa.amber.entity.WorldManager;
import dk.sebsa.amber.entity.TagManager;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.graph.Shader;
import dk.sebsa.amber.graph.text.Font;
import dk.sebsa.amber.io.DevWindow;
import dk.sebsa.amber.io.Input;
import dk.sebsa.amber.io.Window;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber.sound.SoundListener;
import dk.sebsa.amber.sound.SoundManager;
import dk.sebsa.amber_engine.editor.Editor;
import dk.sebsa.amber_engine.windows.BootLoader;
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
	public static boolean snapshot = false;
	
	public static final String editorVersion = Main.class.getPackage().getImplementationVersion();
	
	public static void main(String[] args) {
		// auto update
		if(!editorVersion.contains("SNAPSHOT") && AutoUpdate.needUpdate()) {
			AutoUpdate.update();
		} else snapshot = true;
		
		// Project loading
		try {
			ProjectManager.openProject(BootLoader.init());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		changeLog = EngineConfig.initConfig();
		
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
		window = new Window("Amber Engine", 960, 800, EngineConfig.configEditorVsync, false);
		input = new Input(window);
		input.setInstance();
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
			sm.setInstance();
			sl = new SoundListener();
			sm.setListener(sl);
		} catch (Exception e1) { e1.printStackTrace(); }
		
		loadingScreen.setStatus("Initializing, Renderer", 85);
		Renderer.init(input, window);
		
		// Set loading text
		loadingScreen.reset("Loading Project");
		
		// Load all assets
		TagManager.loadTags(ProjectManager.getProjectDir()+"tags.txt");
		AssetManager.loadAllResources(ProjectManager.getProjectDir());
		ComponentImporter.init();
		
		Editor.init();
		EngineSettings.init();
		engineShader = Shader.findShader("engine");
		
		if(changeLog) { Changelog.load(); EngineRenderer.setWindow(windows.changelog); }
		
		// Open default world
		WorldManager.openWorld(ProjectManager.getProjectDir()+"worlds/" +ProjectManager.configDefaultWorld + ".amw");
	}
	
	public static void loop() throws IOException {
		while(!window.shouldClose()) {
			glfwPollEvents();
			
			// Updates
			window.update();
			input.update();
			
			// Engine input handler
			InputHandler.update();
			
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
	
	public static void togglePlaymode() {
		inPlayMode = !inPlayMode;
		sm.stopAll();

		if(Editor.menubar.playMode==Editor.menubar.playButton) Editor.menubar.playMode = Editor.menubar.stopButton;
		else Editor.menubar.playMode = Editor.menubar.playButton;
		
		if(!inPlayMode) EngineRenderer.setWindow(windows.main);
		else 			EngineRenderer.setWindow(windows.playmode);
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
		ComponentImporter.cleanUp();
		
		// Other
		System.gc();
	}
}
