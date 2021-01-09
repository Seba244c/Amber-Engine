package dk.sebsa.amber_engine.rendering.overlays;

import java.util.function.Consumer;

import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.editor.Editor;
import dk.sebsa.amber_engine.rendering.Overlay;

public class SaveWorld extends Overlay {
	public boolean close = false;
	private Consumer<answer> consumer;
	private Sprite box = Sprite.getSprite(GUI.sheet+".Box");
	private Rect yesRect, noRect, cancelRect;
	private Rect layer;
	
	public SaveWorld(Consumer<answer> consumer) {
		close = false;
		this.consumer = consumer;
		yesRect = new Rect(5, 135, GUI.defaultFont.getStringWidth("Yes")+10, 24);
		noRect = new Rect(yesRect.x+yesRect.width+5, yesRect.y, GUI.defaultFont.getStringWidth("No")+6, 24);
		cancelRect = new Rect(noRect.x+noRect.width+5, yesRect.y, GUI.defaultFont.getStringWidth("Cancel")+5, 24);
	}
	
	@Override
	public boolean render() {
		GUI.window(layer, "Save World?", this::renderWindow, Editor.window);
		
		// CLose
		if(GUI.toggle(false, Main.window.getWidth()/2+282, Main.window.getHeight()/2-100+Editor.window.padding.height, null, Editor.x, 1)) { close = true; return true; }
		return false;
	}
	
	public void renderWindow(Rect r) {
		GUI.label("You are about to close and UNSAVED world?", 0, 0);
		GUI.label("Do you want to save first?", 0, 16);
		if(GUI.button("Yes", yesRect, Editor.button, Editor.buttonHover, GUI.Press.realesed, false, 1)) { close = true; consumer.accept(answer.yes); }
		else if(GUI.button("No", noRect, box, Editor.button, GUI.Press.realesed, false, 1)) { close = true; consumer.accept(answer.no); }
		else if(GUI.button("Cancel", cancelRect, box, Editor.button, GUI.Press.realesed, false, 1)) { close = true; consumer.accept(answer.cancel); }
	}

	@Override
	public boolean close() {
		if(close) return true;
		return false;
	}

	@Override
	public Rect layer() {
		layer = new Rect(Main.window.getWidth()/2-300, Main.window.getHeight()/2-100, 600, 200);
		return layer;
	}

}
