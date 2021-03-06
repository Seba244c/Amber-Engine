package dk.sebsa.amber.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import dk.sebsa.amber.Asset;
import dk.sebsa.amber.math.Rect;

public class SpriteSheet extends Asset {
	private Material material;
	public List<Sprite> sprites = new ArrayList<>();
	private boolean open = false;
	
	public SpriteSheet(String name) {
		BufferedReader br;
		
		try {
			if(name.startsWith("/")) {
				InputStreamReader isr =  new InputStreamReader(Sprite.class.getResourceAsStream("/sheets" + name + ".sht"));
				br = new BufferedReader(isr);
				this.name = name.replaceFirst("/", "");
				internal = true;
			} else {
				file = new File(name + ".sht");
				
				br = new BufferedReader(new FileReader(file));
				String[] split = name.replaceAll(Pattern.quote("\\"), "\\\\").split("\\\\");
				this.name = split[split.length - 1];
			}
			
			// Get material
			material = Material.getMat(br.readLine().split(" ")[1]);	
				
			String line = br.readLine();
			while(line!=null) {
				String sprName = line.split(" ")[1];
				
				String[] o = br.readLine().split(" ")[1].split(",");
				Rect offset = new Rect(Float.parseFloat(o[0]), Float.parseFloat(o[1]), Float.parseFloat(o[2]), Float.parseFloat(o[3]));
				
				String[] p = br.readLine().split(" ")[1].split(",");
				Rect padding = new Rect(Float.parseFloat(p[0]), Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3]));
				
				// Create and manage sprite
				Sprite ns = new Sprite(this.name+"."+sprName, material, offset, padding, this);
				if(internal) ns.internal = true;
				sprites.add(ns);
				
				// Read Line
				line = br.readLine();
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Sprite.newSheet(this);
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
}
