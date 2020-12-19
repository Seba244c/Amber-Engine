package dk.sebsa.amber.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import dk.sebsa.amber.Asset;

public class World extends Asset {
	public static List<World> worlds = new ArrayList<>();
	public String path;
	
	public World(String name) {
		String[] split = name.replaceAll(Pattern.quote("\\"), "\\\\").split("\\\\");
		this.name = split[split.length - 1].split("\\.")[0];
		path = name;
		worlds.add(this);
	}
	
	public static World getWorld(String name) {
		for(int i = 0; i < worlds.size(); i++) {
			if(worlds.get(i).name.equals(name)) return worlds.get(i);
		}
		return null;
	}
}
