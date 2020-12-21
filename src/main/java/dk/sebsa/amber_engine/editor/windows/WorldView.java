package dk.sebsa.amber_engine.editor.windows;

import java.util.ArrayList;
import java.util.List;

import dk.sebsa.amber.Entity;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.editor.Editor;

public class WorldView {
	private int i;
	private List<String> poupStrings = new ArrayList<>();
	public Entity copiedEntity;
	
	public WorldView() {
		poupStrings.add("Duplicate");
		poupStrings.add("Delete");
		poupStrings.add("Copy");
		poupStrings.add("Paste");
	}
	
	public void render(Rect r) {
		// Render
		List<Entity> updateList = new ArrayList<Entity>();
		int offsetY = 0;
		
		updateList.add(Entity.master());
		while(updateList.size() > 0) {
			Entity entity = updateList.get(0);
			List<Entity> children = entity.getChildren();
			
			if(children.size() > 0 && entity.isExpanded()) {
				for(i = 0; i < children.size(); i++) {
					updateList.add(0, children.get(i));
				}
			}
			if(entity == Entity.master()) {
				updateList.remove(updateList.size() - 1);
				continue;
			}
			
			float inline = (entity.getInline()) * 16;
			Rect clickRect = new Rect(0, offsetY, r.width, 20);
			
			Object selected = Editor.getSelected();
			if(selected != null && entity.equals(selected)) {
				GUI.box(clickRect, "Box");
			}
			
			Color prevColor = null;
			if(!entity.isEnabled()) { 
				prevColor = GUI.textColor;
				GUI.textColor = Color.grey();
			}
			
			if(children.size() > 0) {
				entity.setExpanded(GUI.toggle(entity.isExpanded(), inline-15, offsetY+2, Editor.arrowDown, Editor.arrowRight));
				GUI.label(entity.name, inline + 0, offsetY);
			}
			else {
				GUI.label(entity.name, inline, offsetY);
			}
			if(!entity.isEnabled()) GUI.textColor = prevColor;
			
			clickRect.set(0, (r.y+offsetY), r.width, 20);
			
			if(clickRect.inRect(Main.input.getMousePosition()) && !GUI.hasPopup()) {
				if(Main.input.isButtonPressed(0)) Editor.setSelected(entity);
				else if(Main.input.isButtonPressed(1)) {
					Editor.setSelected(entity);
					GUI.setPopup(clickRect, poupStrings, this::clicked);
					GUI.getPopup().moveToMouse(Main.input);
				}
			}
			
			offsetY += 20;
			updateList.remove(entity);
		}
	}
	
	public void clicked(String click) {
		if(!(Editor.getSelected() instanceof Entity)) return;
		if(click.equals("Delete")) {
			((Entity) Editor.getSelected()).delete();
			Editor.setSelected(null);
			Editor.action();
		}
		else if(click.equals("Duplicate")) ((Entity) Editor.getSelected()).duplicate();
		else if(click.equals("Copy")) copiedEntity = ((Entity) Editor.getSelected());
		else if(click.equals("Paste")) paste();
	}
	
	public void paste() {
		Editor.action();
		if(copiedEntity!= null) {
			copiedEntity.copy((byte) 1).parent(((Entity) Editor.getSelected()));
			((Entity) Editor.getSelected()).setExpanded(true);
		}
	}
}
