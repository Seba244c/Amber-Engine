package dk.sebsa.amber_engine;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import dk.sebsa.amber.AssetManager;
import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.entity.ComponentImporter;
import dk.sebsa.amber.entity.WorldManager;
import dk.sebsa.amber.entity.components.phys.Collider;
import dk.sebsa.amber.entity.TagManager;
import dk.sebsa.amber.entity.World;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.graph.Shader;
import dk.sebsa.amber.graph.text.Font;
import dk.sebsa.amber.io.DevWindow;
import dk.sebsa.amber.io.Input;
import dk.sebsa.amber.io.Window;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Time;
import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber.sound.SoundListener;
import dk.sebsa.amber.sound.SoundManager;
import dk.sebsa.amber.util.Logger;
import dk.sebsa.amber_engine.editor.Editor;
import dk.sebsa.amber_engine.rendering.BootLoader;
import dk.sebsa.amber_engine.rendering.EngineRenderer;
import dk.sebsa.amber_engine.rendering.Loading;
import dk.sebsa.amber_engine.rendering.Overlay.answer;
import dk.sebsa.amber_engine.rendering.overlays.SaveWorld;
import dk.sebsa.amber_engine.rendering.windows.Changelog;
import dk.sebsa.amber_engine.rendering.windows.Play;
import dk.sebsa.amber_engine.utils.AssetCreator;

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
	public static ArrayList<Image> swingIcon = new ArrayList<Image>();
	public static ArrayList<Image> swingIconLite = new ArrayList<Image>();
	private static boolean prevFrame = false;
	
	private static boolean close = false;
	
	public static void main(String[] args) {
		// load icon images
		try {
			swingIcon.add(ImageIO.read(Main.class.getResourceAsStream("/boot/Icon-Simplified - 20.png")));
			swingIcon.add(ImageIO.read(Main.class.getResourceAsStream("/boot/Icon-Simplified - 40.png")));

			swingIconLite.add(ImageIO.read(Main.class.getResourceAsStream("/boot/Icon-light-Simplified - 20.png")));
			swingIconLite.add(ImageIO.read(Main.class.getResourceAsStream("/boot/Icon-light-Simplified - 40.png")));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
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
		DevWindow.setIcon(swingIconLite);
		if (System.getProperty("os.name").contains("Mac"))
	        System.setProperty("java.awt.headless", "true");
		
		// Create Classes
		loadingScreen.setStatus("Creating Classes", 10);
		window = new Window("Amber Engine", 960, 800, EngineConfig.configEditorVsync, false);
		input = new Input(window);
		input.setInstance();
		sm = new SoundManager();
		
		AssetCreator.init();
	}
	
	public static void start() throws IOException {
		loadingScreen.setStatus("Initializing, Window & Input", 20);
		window.init(Color.white());
		try { window.setIcon("/boot/Icon-Simplified - 16.png"); } catch (Exception e) {e.printStackTrace(); }
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
		engineShader = Shader.findShader("engine");
		
		if(changeLog) { Changelog.load(); EngineRenderer.setWindow(new Changelog()); }
		
		// Open default world
		WorldManager.openWorld(World.getWorld(ProjectManager.configDefaultWorld));
	}
	
	public static void loop() throws IOException {
		while(!close) {
			glfwPollEvents();
			
			// Updates
			Time.process();
			window.update();
			input.update();
			EngineRenderer.updateLayer();
			
			// Engine input handler
			InputHandler.update();
			
			// Logic
			if(inPlayMode) {
				if(prevFrame == false) Collider.clearFrame();
				Component.updateAll();
				Component.lateUpdateAll();
				
				sm.updateListenerPosition(new Vector2f(0, 0));
			} else {
				prevFrame = false;
			}
			
			// Render
			Component.willRenderAll();
			EngineRenderer.renderScreen();
			
			// Phys
			if(inPlayMode) {
				Collider.compareCollisions();
				Collider.clearFrame();
			}
			
			// Lates
			input.late();
			
			if(window.shouldClose()) prepareForClose();
			
			glfwSwapBuffers(window.windowId);
			
			prevFrame = true;
		}
	}
	
	public static void prepareForClose() {
		glfwSetWindowShouldClose(window.windowId, false);
		if(!WorldManager.getWorld().saved) EngineRenderer.setOverlay(new SaveWorld(Main::answerClose));
		else answerClose(answer.no);
	}
	
	public static void answerClose(answer answerIn) {
		if(answerIn == answer.cancel) { EngineRenderer.setOverlay(null); return; }
		else if(answerIn == answer.yes) {
			try {
				WorldManager.saveWorld();
			} catch (IOException e) {
				Logger.errorLog("InputHandler", "update", "Could not save scene: " + WorldManager.getWorld());
			}
		}
		
		close = true;
	}
	
	public static void togglePlaymode() throws IOException {
		inPlayMode = !inPlayMode;
		sm.stopAll();

		if(inPlayMode) {
			Editor.setInspected(null);
			Editor.menubar.playMode = Editor.menubar.stopButton;
			EngineRenderer.setWindow(new Play());
			EngineRenderer.setOverlay(null);
			GUI.removePopup();
			if(!WorldManager.getWorld().saved) WorldManager.saveWorld();
		} else {
			Editor.menubar.playMode = Editor.menubar.playButton;
			EngineRenderer.setWindow(new dk.sebsa.amber_engine.rendering.windows.Editor());
			EngineRenderer.setOverlay(null);
			GUI.removePopup();
			WorldManager.openWorld(WorldManager.getWorld());
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
		ComponentImporter.cleanUp();
		
		// Other
		System.gc();
	}
}
