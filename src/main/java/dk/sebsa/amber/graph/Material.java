package dk.sebsa.amber.graph;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import dk.sebsa.amber.Asset;
import dk.sebsa.amber.math.Color;

public class Material extends Asset {
	public Texture texture;
	public Color color;
	public Shader shader;

	private static List<Material> materials = new ArrayList<Material>();
	private static int i;
	
	public Material(String n, Texture t, Color c, Shader s) {
		name = n;
		texture = t;
		color = c;
		shader = s;
	}
	
	public Material(String name) {
		BufferedReader br;
	
		try {
			if(name.startsWith("/")) {
				InputStreamReader isr =  new InputStreamReader(Material.class.getResourceAsStream("/materials" + name + ".mat"));
				br = new BufferedReader(isr);
				this.name = name.replaceFirst("/", "");
				internal = true;
			} else {
				file = new File(name+".mat");
				br = new BufferedReader(new FileReader(file));
				String[] split = name.replaceAll(Pattern.quote("\\"), "\\\\").split("\\\\");
				this.name = split[split.length - 1];
			}
			
			// Get texture
			texture = Texture.findTexture(br.readLine().split(" ")[1]);		
			if(texture == null) {
				br.close();
				return;
			}
				
			// Get color
			String[] c = br.readLine().split(" ")[1].split(",");
			color = new Color(Float.parseFloat(c[0]), Float.parseFloat(c[1]), Float.parseFloat(c[2]), Float.parseFloat(c[3]));
				
			// Get shader
			shader = Shader.findShader(br.readLine().split(" ")[1]);
				
			// Add to list
			materials.add(this);

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void bind() {
		shader.bind();
		texture.bind();
	}
	
	public void unbind() {
		shader.unbind();
		texture.unbind();
	}

	public static final List<Material> getMaterials() {
		return materials;
	}
	
	public static Material getMat(String name) {
		for(i = 0; i < materials.size(); i++ ) {
			if(materials.get(i).name.equals(name)) return materials.get(i);
		}
		return null;
	}
}
