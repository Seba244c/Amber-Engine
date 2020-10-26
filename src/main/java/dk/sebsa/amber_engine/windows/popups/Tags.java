package dk.sebsa.amber_engine.windows.popups;

import org.lwjgl.util.tinyfd.TinyFileDialogs;

import dk.sebsa.amber.Entity;
import dk.sebsa.amber.entity.TagManager;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.editor.Editor;

public class Tags {
	private static int offsetY = 0;
	
	public static void render(Rect r) {
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
		if(GUI.button("+ Create Tag +", new Rect(0, offsetY, r.width, 26), Editor.button, Editor.buttonHover, Press.pressed, false))
		{
			String output = TinyFileDialogs.tinyfd_inputBox("Create Tag", "What should the tag be named?", "");
			if(output == null) return;
			
			TagManager.addTag(output);
		}
	}
}
