package dk.sebsa.amber.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import dk.sebsa.amber.Asset;
import dk.sebsa.amber.math.Rect;

public class SpriteSheet extends Asset {
	private Material material;
	
	public SpriteSheet(String name) {
		BufferedReader br;
		
		try {
			if(name.startsWith("/")) {
				InputStreamReader isr =  new InputStreamReader(Sprite.class.getResourceAsStream("/sheets" + name + ".sht"));
				br = new BufferedReader(isr);
				this.name = name.replaceFirst("/", "");
			} else {
				File f = new File(name + ".sht");
				
				br = new BufferedReader(new FileReader(f));
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
				
				new Sprite(this.name+"."+sprName, material, offset, padding);
				line = br.readLine();
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
