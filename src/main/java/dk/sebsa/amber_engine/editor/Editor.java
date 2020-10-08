package dk.sebsa.amber_engine.editor;

import dk.sebsa.amber.Entity;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.editor.windows.Assets;
import dk.sebsa.amber_engine.editor.windows.Inspector;
import dk.sebsa.amber_engine.editor.windows.Types;
import dk.sebsa.amber_engine.editor.windows.WorldView;

public class Editor {
	public static Menubar menubar;
	
	public static WorldView worldView;
	public static Inspector inspector;
	public static Types types;
	public static Assets assets;
	
	private static Entity selected;
	private static Object inspected;
	
	public static Sprite arrowDown;
	public static Sprite arrowRight;
	public static Sprite window;
	
	public static void init() {
		menubar = new Menubar();
		
		arrowDown = Sprite.getSprite(GUI.sheet+".ArrowDown");
		arrowRight = Sprite.getSprite(GUI.sheet+".ArrowRight");
		window = Sprite.getSprite(GUI.sheet+".Window");
		
		worldView = new WorldView();
		inspector = new Inspector();
		assets = new Assets();
		types = new Types();
	}
	
	public static void render() {
		menubar.render();
		GUI.window(new Rect(0, 30, 300, Main.window.getHeight()-430), "World View", worldView::render, window);
		GUI.window(new Rect(Main.window.getWidth()-300, 30, 300, Main.window.getHeight()-30), "Inspector", inspector::render, window);
		GUI.window(new Rect(0, Main.window.getHeight()-400, 300, 400), "Asset Types", types::render, window);
		GUI.window(new Rect(300, Main.window.getHeight()-400, Main.window.getWidth()-600, 400), "Assets", assets::render, window);
	}
	
	public static Object getSelected() {
		return selected;
	}
	
	public static void setSelected(Entity select) {
		selected = select;
		setInspected(select);
	}
	
	public static Object getInspected() {
		return inspected;
	}
	
	public static void setInspected(Object inspect) {
		inspected = inspect;
		if(inspect!=null) inspector.setAttributes(inspect);
	}
}
