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

import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.entity.ComponentImporter;
import dk.sebsa.amber.entity.World;
import dk.sebsa.amber.graph.Material;
import dk.sebsa.amber.graph.Shader;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.graph.SpriteSheet;
import dk.sebsa.amber.graph.Texture;
import dk.sebsa.amber.sound.AudioClip;
import dk.sebsa.amber_engine.Main;

/**
 * @author Sebsa
 * @since Before 0.1.0
 * 
 * The AssetManager automaticly loads all internal and external assets
 */
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
	private static List<String> worlds = new ArrayList<String>();
	private static int i = 0;
	
	/**
	 * @author Sebsa
	 * All the types of asset
	 */
	public enum Asset {
		Sprite,
		Shader,
		Texture,
		Material,
		World,
		Sound,
		SpriteSheet,
		Script
	}
	
	/**
	 * Finds the list associated with the param type
	 * @param type The Asset type wich to return
	 * @return Returns the list associated with the param type
	 */
	public static List<?> typeToList(Asset type) {
		if(type.equals(Asset.Sprite)) return Sprite.getSprites();
		else if(type.equals(Asset.Shader)) return Shader.getShaders();
		else if(type.equals(Asset.Texture)) return Texture.getTextureInstances();
		else if(type.equals(Asset.Material)) return Material.getMaterials();
		else if(type.equals(Asset.Sound)) return AudioClip.getClips();
		else if(type.equals(Asset.Script)) return ComponentImporter.getImportedClasses();
		else if(type.equals(Asset.World)) return World.worlds;
		else if(type.equals(Asset.SpriteSheet)) return Sprite.getSheets();
		return null;
	}
	
	/**
	 * Returns the Asset enum representation of a asset class
	 * @param asset An asset wich to evalualte
	 * @return the Asset enum representation of a asset class
	 */
	public static Asset assetToE(dk.sebsa.amber.Asset asset) {
		if(asset.getClass().equals(Sprite.class)) return Asset.Sprite;
		else if(asset.getClass().equals(Shader.class)) return Asset.Shader;
		else if(asset.getClass().equals(Texture.class)) return Asset.Texture;
		else if(asset.getClass().equals(Material.class)) return Asset.Material;
		else if(asset.getClass().equals(AudioClip.class)) return Asset.Sound;
		else if(asset.getClass().equals(Component.class)) return Asset.Script;
		else if(asset.getClass().equals(World.class)) return Asset.World;
		else if(asset.getClass().equals(SpriteSheet.class)) return Asset.SpriteSheet;
		return null;
	}
	
	/**
	 * Loads ALL jar assets and external assets and intializes them
	 * @param externalDir The folder wich the external assets is located in
	 * @throws IOException If an file is not found or could not be loaded
	 */
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
		if(!worlds.isEmpty()) for(i = 0; i < worlds.size(); i++) new World(worlds.get(i));
	}
	
	/**
	 * Finds ALL jar assets and external assets
	 * @param externalDir The folder wich the external assets is located in
	 */
	private static void initResourcePaths(String externalDir) {
		URL dirUrl = cl.getResource("dk/sebsa/amber_engine");
		String protocol = dirUrl.getProtocol();
		
		try {
			if(dirUrl != null && protocol.equals("file")) importFromDir(externalDir);
			else importFromJar(externalDir);
		} catch (IOException e) { System.out.println("Error loading assets:"); e.printStackTrace(); }
		
	}
	
	/**
	 * This function is called if the program is run from an jar, loads all internal and external assets
	 * @param externalDir The folder wich the external assets is located in
	 * @throws UnsupportedEncodingException
	 * @throws IOException If an file is not found or could not be loaded
	 */
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
				else if(name.startsWith("worlds")) { spiteSheets.add("/" +name.split("/")[1]); }
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
		worlds.addAll(importFromExternalDir("worlds", 1, externalDir));
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
		worlds = (importFromLocalDir("worlds", 1));
		
		// Load other assets from external folders
		Main.loadingScreen.setStatus("Loading external assets", 10);
		textures.addAll(importFromExternalDir("textures", 1, externalDir));
		scripts.addAll(importFromExternalDir("scripts", 1, externalDir));
		shaders.addAll(importFromExternalDir("shaders", 0, externalDir));
		materials.addAll(importFromExternalDir("materials", 0, externalDir));
		sprites.addAll(importFromExternalDir("sprites", 0, externalDir));
		sounds.addAll(importFromExternalDir("sounds", 1, externalDir));
		spiteSheets.addAll(importFromExternalDir("sheets", 0, externalDir));
		worlds.addAll(importFromExternalDir("worlds", 1, externalDir));
	}

	/**
	 * The actual method wich finds the internal(jar) assets
	 * @param path The sub folder wich to loacte assets from
	 * @param useExt Wether the file extension should be used
	 * @return A list of asset file locatuions
	 * @throws IOException If an file is not found or could not be loaded
	 */
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
	
	/**
	 * @param path The sub folder wich to loacte assets from
	 * @param useExt Wether the file extension should be used
	 * @param externalDir The folder wich the path lies in
	 * @return A list of asset file locatuions
	 */
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
	
	/**
	 * Cleans up all assets with a cleanup function
	 */
	public static void cleanUpAll() {
		for(Shader s : Shader.getShaders()) { s.cleanup(); }
		Texture.cleanup();
	}
}
