package dk.sebsa.amber_engine.editor;

import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.editor.windows.WorldView;

public class Editor {
	public static Menubar menubar;
	public static WorldView worldView;
	private static Object selected;
	
	public static Sprite arrowDown;
	public static Sprite arrowRight;
	public static Sprite window;
	
	public static void init() {
		menubar = new Menubar();
		worldView = new WorldView();
		
		arrowDown = Sprite.getSprite(GUI.sheet+".ArrowDown");
		arrowRight = Sprite.getSprite(GUI.sheet+".ArrowRight");
		window = Sprite.getSprite(GUI.sheet+".Window");
	}
	
	public static void render() {
		menubar.render();
		GUI.window(new Rect(0, 30, 300, Main.window.getHeight()-430), "World View", worldView::render, window);
		GUI.window(new Rect(Main.window.getWidth()-300, 30, 300, Main.window.getHeight()-30), "Inspector", Editor::test, window);
		GUI.window(new Rect(0, Main.window.getHeight()-400, Main.window.getWidth()-300, 400), "Assets", Editor::test, window);
	}
	
	public static void cleanup() {
		
	}
	
	public static void test(Rect rect) {
		
	}
	
	public static Object getSelected() {
		return selected;
	}
	
	public static void setSelected(Object select) {
		selected = select;
	}
}
