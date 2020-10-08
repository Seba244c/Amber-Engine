package dk.sebsa.amber_engine.editor.windows;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import dk.sebsa.amber.Entity;
import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber.util.Logger;
import dk.sebsa.amber_engine.editor.Editor;
import dk.sebsa.amber_engine.utils.ComponentAttributes;


public class Inspector {
	private int i;
	private List<ComponentAttributes> ca = new ArrayList<ComponentAttributes>();
	private int offsetY = 0;
	public Sprite windowStyle;
	
	public Inspector() {
		windowStyle = Editor.window;
	}
	
	public void render(Rect r) {
		Object inspected = Editor.getInspected();
		if(inspected == null) return;
	
		if(inspected instanceof Entity) {
			// Get Selected
			Entity selected = (Entity) inspected;

			// Render transform info and name
			selected.name = GUI.textField(new Rect(0, 0, r.width, 22), "Entity", selected.name, 100);
			selected.setPosition(GUI.vectorField(new Rect(0, 24, r.width, 22), "Position", selected.getPosition(), 100));
			selected.setScale(GUI.vectorField(new Rect(0, 48, r.width, 22), "Scale", selected.getScale(), 100));
			selected.setRotation(GUI.floatField(new Rect(0, 72, r.width, 22), "Rotation", selected.getRotation(), 100));
			offsetY = 96;
			
			// Render components
			for(i = 0; i < ca.size(); i++) {
				ComponentAttributes att = ca.get(i);				
				
				float h = att.height + (windowStyle.padding.y + windowStyle.padding.height);
				GUI.window(new Rect(0, offsetY, r.width, h), ((Component) att.component).getName(), this::drawVariables, windowStyle);
				offsetY += h + 2;
			}
		}
	}
	
	public void drawVariables(Rect r) {
		ComponentAttributes a = ca.get(i);
		
		String[] fields = ca.get(i).fields;
		int padding = 0;
		for(int f = 0; f < fields.length; f++) {
			String[] split = fields[f].split(" ");
			
			if(split[1].equals("String")) {
				try {
					Field s = a.c.getDeclaredField(split[0]);
					try {
						String p = s.get(a.component).toString();
						String v = GUI.textField(new Rect(0, f * 22 + padding, r.width, 22), split[0], p, 100);
						if(!p.equals(v))
							s.set(a.component, v);
					}
					catch(IllegalArgumentException e) { e.printStackTrace(); }
					catch(IllegalAccessException e) { e.printStackTrace(); }
				}
				catch (NoSuchFieldException e) { e.printStackTrace(); }
				catch (SecurityException e) { e.printStackTrace(); }
			}
			else if(split[1].equals("float")) {
				try {
					Field s = a.c.getDeclaredField(split[0]);
					try {
						String p = s.get(a.component).toString();
						
						float fl = Float.parseFloat(p);
						float v = GUI.floatField(new Rect(0, f * 22 + padding, r.width, 22), split[0], fl, 100);
						if(!p.equals(String.valueOf(v)))
							s.set(a.component, v);
						
					}
					catch(IllegalArgumentException e) { e.printStackTrace(); }
					catch(IllegalAccessException e) { e.printStackTrace(); }
				}
				catch (NoSuchFieldException e) { e.printStackTrace(); }
				catch (SecurityException e) { e.printStackTrace(); }
			}
			else if(split[1].equals("int")) {
				try {
					Field s = a.c.getDeclaredField(split[0]);
					try {
						String p = s.get(a.component).toString();
						String v = GUI.textField(new Rect(0, f * 22 + padding, r.width, 22), split[0], p, 100);
						if(!p.equals(v)) {
							try { s.set(a.component, Integer.parseInt(v)); }
							catch (NumberFormatException e) {
								Logger.errorLog("Inspector", "drawVaribels", "Int field input is inviliad!");
							}
						}	
					}
					catch(IllegalArgumentException e) { e.printStackTrace(); }
					catch(IllegalAccessException e) { e.printStackTrace(); }
				}
				catch (NoSuchFieldException e) { e.printStackTrace(); }
				catch (SecurityException e) { e.printStackTrace(); }
			}
			else if(split[1].equals("Vector2f")) {
				try {
					Field s = a.c.getDeclaredField(split[0]);
					try {
						Vector2f p = (Vector2f) s.get(a.component);
						Vector2f v = GUI.vectorField(new Rect(0, f * 22 + padding, r.width, 22), split[0], p, 100);
						if(!p.equals(v)) {
							s.set(a.component, v);
						}	
					}
					catch(IllegalArgumentException e) { e.printStackTrace(); }
					catch(IllegalAccessException e) { e.printStackTrace(); }
				}
				catch (NoSuchFieldException e) { e.printStackTrace(); }
				catch (SecurityException e) { e.printStackTrace(); }
			}
			else { GUI.textField(new Rect(0, f * 22 + padding, r.width, 22), split[0], "", 100); }
			padding += 2;
		}
	}
	
	public void setAttributes(Object o) {
		ca.clear();
		if(o instanceof Entity) {
			List<Component> c = ((Entity) o).getComponents();
			
			for(i = 0; i < c.size(); i++) {
				ComponentAttributes a = new ComponentAttributes(o);
				if(a != null) ca.add(new ComponentAttributes(c.get(i)));
			}
			return;
		}
		ComponentAttributes a = new ComponentAttributes(o);
		if(a != null) ca.add(a);
	}
}
