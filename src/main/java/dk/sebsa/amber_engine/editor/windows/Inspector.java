package dk.sebsa.amber_engine.editor.windows;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.tinyfd.TinyFileDialogs;

import dk.sebsa.amber.Asset;
import dk.sebsa.amber.Entity;
import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.entity.ComponentImporter;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber.util.Logger;
import dk.sebsa.amber_engine.editor.Editor;
import dk.sebsa.amber_engine.rendering.EngineRenderer;
import dk.sebsa.amber_engine.rendering.overlays.Tags;
import dk.sebsa.amber_engine.utils.ComponentAttributes;

public class Inspector {
	private int i;
	private List<ComponentAttributes> ca = new ArrayList<ComponentAttributes>();
	private int offsetY = 0;
	private int scroll = 0;
	public Sprite windowStyle;
	public Sprite on, off;
	
	public Inspector() {
		windowStyle = Editor.window;
		on = Sprite.getSprite(GUI.sheet+".ToggleOn");
		off = Sprite.getSprite(GUI.sheet+".ToggleOff");
	}
	
	public void render(Rect r) {
		Object inspected = Editor.getInspected();
		if(inspected == null) return;
		
		if(inspected instanceof Asset) {
			Asset selected = (Asset) inspected;
			selected.render(r);
		} else if(inspected instanceof Entity) {
			// Scroll
			int addition = 144 + 26;
			
			for(i = 0; i < ca.size(); i++) addition += ca.get(i).height + (windowStyle.padding.y + windowStyle.padding.height) + 2;
			scroll = Renderer.setScrollView(addition, scroll);
			
			// Get Selected
			Entity selected = (Entity) inspected;

			// Render transform info, entity enabled and name
			selected.name = GUI.textField(new Rect(0, 0, r.width-20, 22), "Entity", selected.name, 100, 0);
			
			GUI.label("Tag", 0, 24);
			if(GUI.button(selected.tag, new Rect(100, 24, r.width-100, 22), Editor.button, Editor.buttonHover, Press.realesed, false, 0)) {
				EngineRenderer.setOverlay(new Tags());
			}
			
			selected.setPosition(GUI.vectorField(new Rect(0, 48, r.width, 22), "Position", selected.getPosition(), 100, 0));
			selected.setScale(GUI.vectorField(new Rect(0, 72, r.width, 22), "Scale", selected.getScale(), 100, 0));
			selected.setRotation(GUI.floatField(new Rect(0, 96, r.width, 22), "Rotation", selected.getRotation(), 100, 0));
			selected.depth = GUI.floatField(new Rect(0, 120, r.width, 22), "Depth", selected.depth, 100, 0);
			
			selected.setEnabled(GUI.toggle(selected.isEnabled(), r.width-16, offsetY+3f, on, off, 0));
			offsetY = 144;
			
			// Render components
			for(i = 0; i < ca.size(); i++) {
				// Variables
				ComponentAttributes att = ca.get(i);				
				Component c = (Component) att.component;
				float h = att.height + (windowStyle.padding.y + windowStyle.padding.height);
				GUI.window(new Rect(0, offsetY, r.width, h), ((Component) att.component).getName(), this::drawVariables, windowStyle);
				
				// Component Enabled
				c.setEnabled(GUI.toggle(c.isEnabled(), r.width-36, offsetY+7f, on, off, 0));
				
				// Delete
				if(GUI.toggle(false, r.width-18, offsetY+7f, null, Editor.x, 0)) {
					c.delete();
					setAttributes(selected);
				}
				
				offsetY += h + 2;
			}
			
			if(GUI.button("+ Add Component +", new Rect(0, offsetY, r.width, 26), "Button", "ButtonHover", Press.pressed, false, 0))
			{
				String output = TinyFileDialogs.tinyfd_inputBox("Add Component", "What component would you like to add?", "");
				if(output == null) return;
				
				Component l = selected.addComponent(ComponentImporter.getComponent(output));
				if(l != null) setAttributes(selected);
			}
			offsetY = 0;
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
						String v = GUI.textField(new Rect(0, f * 22 + padding, r.width, 22), split[0], p, 100, 0);
						if(!p.equals(v)) {
							s.set(a.component, v);
							//Editor.action();
						}
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
						float v = GUI.floatField(new Rect(0, f * 22 + padding, r.width, 22), split[0], fl, 100, 0);
						if(!p.equals(String.valueOf(v))) {
							s.set(a.component, v);
							//Editor.action();
						}
						
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
						String v = GUI.textField(new Rect(0, f * 22 + padding, r.width, 22), split[0], p, 100, 0);
						if(!p.equals(v)) {
							//Editor.action();
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
						Vector2f v = GUI.vectorField(new Rect(0, f * 22 + padding, r.width, 22), split[0], p, 100, 0);
						if(!p.equals(v)) {
							//Editor.action();
							s.set(a.component, v);
						}	
					}
					catch(IllegalArgumentException e) { e.printStackTrace(); }
					catch(IllegalAccessException e) { e.printStackTrace(); }
				}
				catch (NoSuchFieldException e) { e.printStackTrace(); }
				catch (SecurityException e) { e.printStackTrace(); }
			}
			else { GUI.textField(new Rect(0, f * 22 + padding, r.width, 22), split[0], "", 100, 0); }
			padding += 2;
		}
	}
	
	public void setAttributes(Object o) {
		scroll = 0;
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
