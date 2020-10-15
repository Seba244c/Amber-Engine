package dk.sebsa.amber_engine.windows;

import java.util.ArrayList;
import java.util.List;

import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.graph.text.Font;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.AutoUpdate;
import dk.sebsa.amber_engine.Main;

public class Changelog {
	private static boolean close = false;
	private static List<String> changeLog;
	private static Font title;
	
	public static void load() {
		List<Object> list = AutoUpdate.getChangelog().toList();
		changeLog = new ArrayList<String>();
		title = new Font(new java.awt.Font("OpenSans", java.awt.Font.PLAIN, 28));
		
		while(list.size() > 0) {
			changeLog.add((String) list.get(0));
			list.remove(0);
		}
	}
	
	public static boolean render() {
		Renderer.prepare();
		GUI.window(Main.window.getRect(), "Amber-Engine, update: " + Main.editorVersion + ", changelog!", Changelog::render, "Window");
		Renderer.unprepare();
		return close;
	}
	
	private static void render(Rect r) {
		// Title
		String s = Main.editorVersion + " - " + AutoUpdate.getUpdateName();
		
		GUI.label(s, r.width/2-title.getStringWidth(s)/2, 0, Color.white(), title);
		close = GUI.toggle(false, r.width-16, 0, null, Sprite.getSprite("BlackGUI.XClose"));
		if(close == false) close = GUI.button("Clsoe Changelog", new Rect(0, r.height-28, r.width, 28), "Button", "ButtonHover", Press.realesed, false);
		
		// Labels
		int offsetY = 32;
		int i = 0;
		while(i < changeLog.size()) {
			GUI.label(" * " + changeLog.get(i), 0, offsetY);
			
			offsetY += 28;
			i++;
		}
	}
}
