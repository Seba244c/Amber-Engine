package dk.sebsa.amber_engine;

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

import dk.sebsa.amber.graph.Material;
import dk.sebsa.amber.graph.Shader;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.graph.Texture;

enum Asset {
	Sprite,
	Shader,
	Texture,
	Material
}

public class AssetManager {
	private static Class<AssetManager> clazz = AssetManager.class;
	private static ClassLoader cl = clazz.getClassLoader();
	
	private static List<String> textures = new ArrayList<String>();
	private static List<String> shaders = new ArrayList<String>();
	private static List<String> materials = new ArrayList<String>();
	private static List<String> sprites = new ArrayList<String>();
	private static int i = 0;
	
	public static void loadAllResources() throws IOException {
		int i = 0;
		initResourcePaths();
		
		if(!textures.isEmpty()) for(i = 0; i < textures.size(); i++) new Texture(textures.get(i));
		if(!shaders.isEmpty()) for(i = 0; i < shaders.size(); i++) new Shader(shaders.get(i));
		if(!materials.isEmpty()) for(i = 0; i < materials.size(); i++) new Material(materials.get(i));
		if(!sprites.isEmpty()) for(i = 0; i < sprites.size(); i++) new Sprite(sprites.get(i));
	}
	
	private static void initResourcePaths() {
		URL dirUrl = cl.getResource("dk/sebsa/amber_engine");
		String protocol = dirUrl.getProtocol();
		
		try {
			if(dirUrl != null && protocol.equals("file")) importFromDir();
			else importFromJar(dirUrl);
		} catch (IOException e) { System.out.println("Error loading assets:"); e.printStackTrace(); }
		
	}
	
	private static void importFromJar(URL dirUrl) throws UnsupportedEncodingException, IOException {
		// Loads the engine resources from a jar
		String me = clazz.getName().replace(".", "/") + ".class";
		dirUrl = cl.getResource(me);
		
		if(dirUrl.getProtocol().equals("jar")) {
			String jarPath = dirUrl.getPath().substring(5, dirUrl.getPath().indexOf("!"));
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entires = jar.entries();
			
			while(entires.hasMoreElements()) {
				String name = entires.nextElement().getName();
				if(name.endsWith("/")) continue;
				else if(name.startsWith("textures")) { textures.add(name.split("/")[1]); }
				else if(name.startsWith("shaders")) { shaders.add("/" + name.split("/")[1].split("\\.")[0]); }
				else if(name.startsWith("materials")) { materials.add(name.split("/")[1].split("\\.")[0]); }
				else if(name.startsWith("sprites")) { sprites.add(name.split("/")[1].split("\\.")[0]); }
			}
			jar.close();
		}
		
		textures.addAll(importFromExternalDir("textures", 1));
		shaders.addAll(importFromExternalDir("shaders", 0));
		materials.addAll(importFromExternalDir("materials", 0));
		sprites.addAll(importFromExternalDir("sprites", 0));
	}

	private static void importFromDir() throws IOException {
		// Loads engine resources from folders
		textures = importFromLocalDir("textures", 1);
		shaders = importFromLocalDir("shaders", 0);
		materials = importFromLocalDir("materials", 0);
		sprites = importFromLocalDir("sprites", 0);
		
		textures.addAll(importFromExternalDir("textures", 1));
		shaders.addAll(importFromExternalDir("shaders", 0));
		materials.addAll(importFromExternalDir("materials", 0));
		sprites.addAll(importFromExternalDir("sprites", 0));
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
	
	private static List<String> importFromExternalDir(String path, int useExt) {
		List<String> paths = new ArrayList<String>();
		File dir = new File(ProjectManager.getProjectDir() + path);
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
