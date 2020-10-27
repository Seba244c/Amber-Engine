package dk.sebsa.amber.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import dk.sebsa.amber.Asset;

public class Scene extends Asset {
	public static List<Scene> scenes = new ArrayList<>();
	
	public Scene(String name) {
		String[] split = name.replaceAll(Pattern.quote("\\"), "\\\\").split("\\\\");
		this.name = split[split.length - 1];
		scenes.add(this);
	}
}
