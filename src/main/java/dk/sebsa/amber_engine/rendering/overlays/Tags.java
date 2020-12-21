package dk.sebsa.amber_engine.rendering.overlays;

import org.lwjgl.util.tinyfd.TinyFileDialogs;

import dk.sebsa.amber.Entity;
import dk.sebsa.amber.entity.TagManager;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.editor.Editor;
import dk.sebsa.amber_engine.rendering.Overlay;

public class Tags extends Overlay {
	private static int offsetY = 0;
	
	@Override
	public boolean render() {
		GUI.window(new Rect(Main.window.getWidth()/2-300, Main.window.getHeight()/2-200, 600, 400), "Tag Manager", this::renderWindow, Editor.window);
		
		// CLose
		if(GUI.toggle(false, Main.window.getWidth()/2+282, Main.window.getHeight()/2-200+Editor.window.padding.height, null, Editor.x)) return true;
		return false;
	}
	
	public void renderWindow(Rect r) {
		// Tag list
		offsetY = 0;
		for(String tag : TagManager.getTags()) {
			boolean bool = false;
			if(((Entity) Editor.getInspected()).tag.equals(tag)) bool = GUI.button(tag, new Rect(0, offsetY, r.width-20, 28), Editor.button, Editor.buttonHover, Press.realesed, false);
			else bool = GUI.button(tag, new Rect(0, offsetY, r.width-20, 28), null, Editor.button, Press.realesed, false);
			
			// Delete tag
			if(!tag.equals("Untagged") && GUI.toggle(false, r.width-18, offsetY+6, null, Editor.x)) {
				TagManager.removeTag(tag);
				// RETURNING TO EVADE EXCEPTION
				return;
			}
			
			// Bool
			if(bool) {
				((Entity) Editor.getInspected()).tag = tag;
			}
			
			offsetY+=28;
		}
		
		// Create tag
		if(GUI.button("+ Create Tag +", new Rect(0, offsetY, r.width, 26), Editor.button, Editor.buttonHover, Press.pressed, false)) {
			String output = TinyFileDialogs.tinyfd_inputBox("Create Tag", "What should the tag be named?", "");
			if(output == null) return;
			
			TagManager.addTag(output);
		}
	}

	@Override
	public boolean close() { TagManager.save(TagManager.path); return true; }
}
