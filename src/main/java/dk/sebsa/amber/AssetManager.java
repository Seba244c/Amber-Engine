package dk.sebsa.amber;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;

import dk.sebsa.amber.entity.ComponentImporter;
import dk.sebsa.amber.entity.Scene;
import dk.sebsa.amber.graph.Material;
import dk.sebsa.amber.graph.Shader;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.graph.SpriteSheet;
import dk.sebsa.amber.graph.Texture;
import dk.sebsa.amber.sound.AudioClip;
import dk.sebsa.amber_engine.Main;

public class AssetManager {
	private static Class<AssetManager> clazz = AssetManager.class;
	private static ClassLoader cl = clazz.getClassLoader();
	
	private static List<String> textures = new ArrayList<String>();
	private static List<String> shaders = new ArrayList<String>();
	private static List<String> materials = new ArrayList<String>();
	private static List<String> sprites = new ArrayList<String>();
	private static List<String> sounds = new ArrayList<String>();
	private static List<String> spiteSheets = new ArrayList<String>();
	private static List<String> scripts = new ArrayList<String>();
	private static List<String> scenes = new ArrayList<String>();
	private static int i = 0;
	
	public enum Asset {
		Sprite,
		Shader,
		Texture,
		Material,
		Scene,
		Sound,
		SpriteSheet,
		Script
	}
	
	public static List<?> typeToList(Asset type) {
		if(type.equals(Asset.Sprite)) return Sprite.getSprites();
		else if(type.equals(Asset.Shader)) return Shader.getShaders();
		else if(type.equals(Asset.Texture)) return Texture.getTextureInstances();
		else if(type.equals(Asset.Material)) return Material.getMaterials();
		else if(type.equals(Asset.Sound)) return AudioClip.getClips();
		else if(type.equals(Asset.Script)) return ComponentImporter.getImportedClasses();
		else if(type.equals(Asset.Scene)) return Scene.scenes;
		//else if(type.equals(Asset.SpriteSheet)) return SpriteSheet.
		return null;
	}
	
	public static void loadAllResources(String externalDir) throws IOException {
		int i = 0;
		initResourcePaths(externalDir);
		
		// Textures
		Main.loadingScreen.setStatus("Loading textures", 10);
		if(!textures.isEmpty()) for(i = 0; i < textures.size(); i++) new Texture(textures.get(i));
		
		// Shaders
		Main.loadingScreen.setStatus("Loading shaders", 30);
		if(!shaders.isEmpty()) for(i = 0; i < shaders.size(); i++) new Shader(shaders.get(i));
		
		// Materials
		Main.loadingScreen.setStatus("Loading materials", 40);
		if(!materials.isEmpty()) for(i = 0; i < materials.size(); i++) new Material(materials.get(i));
		
		// Sprites
		Main.loadingScreen.setStatus("Loading sprites, and sprite sheets", 50);
		if(!sprites.isEmpty()) for(i = 0; i < sprites.size(); i++) new Sprite(sprites.get(i));
		if(!spiteSheets.isEmpty()) for(i = 0; i < spiteSheets.size(); i++) new SpriteSheet(spiteSheets.get(i));
		
		// Components
		Main.loadingScreen.setStatus("Loading components", 60);
		if(!scripts.isEmpty()) for(i = 0; i < scripts.size(); i++) ComponentImporter.importClass(scripts.get(i));
		
		// Sounds
		Main.loadingScreen.setStatus("Loading sounds", 80);
		if(!sounds.isEmpty()) for(i = 0; i < sounds.size(); i++) new AudioClip(sounds.get(i), Main.sm);
		
		// Scenes
		if(!scenes.isEmpty()) for(i = 0; i < scenes.size(); i++) new Scene(scenes.get(i));
	}
	
	private static void initResourcePaths(String externalDir) {
		URL dirUrl = cl.getResource("dk/sebsa/amber_engine");
		String protocol = dirUrl.getProtocol();
		
		try {
			if(dirUrl != null && protocol.equals("file")) importFromDir(externalDir);
			else importFromJar(externalDir);
		} catch (IOException e) { System.out.println("Error loading assets:"); e.printStackTrace(); }
		
	}
	
	private static void importFromJar(String externalDir) throws UnsupportedEncodingException, IOException {
		// Loads the engine resources from a jar
		Main.loadingScreen.setStatus("Loading internal assets", 0);
		String me = clazz.getName().replace(".", "/") + ".class";
		URL dirUrl = cl.getResource(me);
		
		if(dirUrl.getProtocol().equals("jar")) {
			String jarPath = dirUrl.getPath().substring(5, dirUrl.getPath().indexOf("!"));
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entires = jar.entries();
			
			while(entires.hasMoreElements()) {
				String name = entires.nextElement().getName();
				
				if(name.endsWith("/")) continue;
				else if(name.startsWith("textures")) { textures.add("/" +name.split("/")[1]); }
				else if(name.startsWith("scripts")) { scripts.add("/" +name.split("/")[1]); }
				else if(name.startsWith("shaders")) { shaders.add("/" + name.split("/")[1].split("\\.")[0]); }
				else if(name.startsWith("materials")) { materials.add("/" +name.split("/")[1].split("\\.")[0]); }
				else if(name.startsWith("sprites")) { sprites.add("/" +name.split("/")[1].split("\\.")[0]); }
				else if(name.startsWith("sounds")) { sounds.add("/" +name.split("/")[1].split("\\.")[0]); }
				else if(name.startsWith("sheets")) { spiteSheets.add("/" +name.split("/")[1].split("\\.")[0]); }
				else if(name.startsWith("scenes")) { spiteSheets.add("/" +name.split("/")[1].split("\\.")[0]); }
			}
			jar.close();
		}

		Main.loadingScreen.setStatus("Loading external assets", 10);
		textures.addAll(importFromExternalDir("textures", 1, externalDir));
		scripts.addAll(importFromExternalDir("scripts", 1, externalDir));
		shaders.addAll(importFromExternalDir("shaders", 0, externalDir));
		materials.addAll(importFromExternalDir("materials", 0, externalDir));
		sprites.addAll(importFromExternalDir("sprites", 0, externalDir));
		sounds.addAll(importFromExternalDir("sounds", 1, externalDir));
		spiteSheets.addAll(importFromExternalDir("sheets", 0, externalDir));
		scenes.addAll(importFromExternalDir("scenes", 0, externalDir));
	}

	private static void importFromDir(String externalDir) throws IOException {
		// Loads engine resources from folders
		Main.loadingScreen.setStatus("Loading internal assets", 0);
		textures = importFromLocalDir("textures", 1);
		scripts = importFromLocalDir("scripts", 1);
		shaders = importFromLocalDir("shaders", 0);
		materials = importFromLocalDir("materials", 0);
		sprites = importFromLocalDir("sprites", 0);
		sounds = importFromLocalDir("sounds", 1);
		spiteSheets = (importFromLocalDir("sheets", 0));
		scenes = (importFromLocalDir("scenes", 0));
		
		// Load other assets from external folders
		Main.loadingScreen.setStatus("Loading external assets", 10);
		textures.addAll(importFromExternalDir("textures", 1, externalDir));
		scripts.addAll(importFromExternalDir("scripts", 1, externalDir));
		shaders.addAll(importFromExternalDir("shaders", 0, externalDir));
		materials.addAll(importFromExternalDir("materials", 0, externalDir));
		sprites.addAll(importFromExternalDir("sprites", 0, externalDir));
		sounds.addAll(importFromExternalDir("sounds", 1, externalDir));
		spiteSheets.addAll(importFromExternalDir("sheets", 0, externalDir));
		scenes.addAll(importFromExternalDir("scenes", 0, externalDir));
	}

	private static List<String> importFromLocalDir(String path, int useExt) throws IOException {
		List<String> paths = new ArrayList<String>();
		InputStream in = cl.getResourceAsStream(path);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		
		while((line = br.readLine()) != null) {
			if(useExt == 1)
				paths.add("/" + line);
			else
				paths.add("/" + line.split("\\.")[0]);
		}
		
		in.close();
		br.close();
		return paths;
	}
	
	private static List<String> importFromExternalDir(String path, int useExt, String externalDir) {
		List<String> paths = new ArrayList<String>();
		File dir = new File(externalDir + path);
		File[] files = dir.listFiles();
		for(i = 0; i < files.length; i ++) {
			String aPath = files[i].getAbsolutePath();
			if(aPath.endsWith("/")) continue;
			if(useExt == 0) aPath = aPath.split("\\.")[0];
			paths.add(aPath);
		}
		return paths;
	}
	
	public static void cleanUpAll() {
		for(Shader s : Shader.getShaders()) { s.cleanup(); }
		Texture.cleanup();
	}
}