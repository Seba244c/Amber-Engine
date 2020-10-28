package dk.sebsa.amber.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import dk.sebsa.amber.Asset;

public class World extends Asset {
	public static List<World> worlds = new ArrayList<>();
	
	public World(String name) {
		String[] split = name.replaceAll(Pattern.quote("\\"), "\\\\").split("\\\\");
		this.name = split[split.length - 1];
		worlds.add(this);
	}
}
