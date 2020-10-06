package dk.sebsa.amber.graph;

import java.util.List;
import java.util.function.Consumer;

import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.io.Input;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber.math.Vector2f;

public class Popup {
	private Rect nameArea;
	private List<String> list;
	private Consumer<String> func;
	private int i;
	private Rect listArea;
	private Sprite box;
	private Sprite hover;
	private Color prevColor;
	private Input input;
	
	public Popup(Rect nameArea, List<String> list, Consumer<String> func, Input input) {
		this.nameArea = nameArea;
		this.list = list;
		
		// Get widest string inside of list
		float width = 0;
		for(i = 0; i < list.size(); i ++) {
			float w = GUI.font.getStringWidth(list.get(i));
			if(w > width) width = w;
		}
		listArea = new Rect(nameArea.x, nameArea.y + nameArea.height - 1, width + 12, list.size() * 28 + 4);
		
		box = Sprite.getSprite(GUI.sheet+".Box");
		hover = Sprite.getSprite(GUI.sheet+".HoverButton");
		this.func = func;
		this.input = input;
	}
	
	public final Rect getListArea() { return listArea; }
	
	public Popup draw() {
		Vector2f mousePos = input.getMousePosition();
		if(!listArea.inRect(mousePos) && !nameArea.inRect(mousePos)) return null;
		Rect drawArea = GUI.box(listArea, box);
		
		prevColor = GUI.textColor;
		GUI.textColor = Color.white();
		
		for(i = 0; i < list.size(); i++) {
			if(GUI.button(list.get(i), new Rect(drawArea.x, drawArea.y + (i * 28), drawArea.width, 28), box, hover, Press.realesed, true)) {
				func.accept(list.get(i));
				GUI.textColor = prevColor;
				return null;
			}
		}
		
		GUI.textColor = prevColor;
		return this;
	}
}
