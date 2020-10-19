package dk.sebsa.amber.entity;

import java.util.ArrayList;
import java.util.List;

import dk.sebsa.amber.Asset;

public class Scene extends Asset {
	public static List<Scene> scenes = new ArrayList<>();
	
	public Scene(String name) {
		this.name = name;
		scenes.add(this);
	}
}
