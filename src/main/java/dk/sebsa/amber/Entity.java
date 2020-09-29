package dk.sebsa.amber;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.math.Matrix4x4;
import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber.util.Logger;

public class Entity {
	public boolean enabled = true;
	private int i;
	
	public String tag = "Untagged";
	public String name = "New Entity";
	
	private byte dirty = 1;
	private Vector2f position = new Vector2f();
	private Vector2f scale = new Vector2f(1, 1);
	private float rotation = 0;
	
	private Matrix4x4 matrix = new Matrix4x4();
	private List<Component> components = new ArrayList<Component>();
	private String id;
	
	public Entity() {
		id = UUID.randomUUID().toString();
	}
	
	public Entity(String name) {
		id = UUID.randomUUID().toString();
		this.name = name;
	}
	
	public Component getComponent(String name) {
		for(i = 0; i < components.size(); i++) {
			Component c = components.get(i);
			if(c.getName().equals(name)) return c;
		}
		return null;
	}
	
	public void removeComponent(Component c) {
		components.remove(c);
	}
	
	public Component addComponent(String v) {
		Class<?> cls;
		try {
			try { cls = Class.forName(v); }
			catch (NoClassDefFoundError e){ return null; }
			
			try { return addComponent((Component) cls.getConstructor().newInstance()); }
			catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
			{}
		} catch (ClassNotFoundException e) {
			Component c = Component.getComponent(v);
			if(c != null) return addComponent(c);
			
			Logger.errorLog("Entity",  name, "Could not find component!");
		}
		return null;
	}
	
	public Component addComponent(Component c) {
		if(c == null) { Logger.errorLog("Entity",  name, "Could not find component!"); return null; }
		c.init(this);
		components.add(c);
		return c;
	}

	public String getId() {
		return id;
	}

	public List<Component> getComponents() {
		return components;
	}

	public final Vector2f getPosition() { return position; }
	public void setPosition(Vector2f p) {
		position.set(p);
		dirty = 1;
	}
	public void setPosition(float x, float y) {
		position.set(x, y);
		dirty = 1;
	}
	
	public final Vector2f getScale() { return scale; }
	public void setScale(Vector2f s) { scale.set(s); }
	public void setScale(float x, float y) { scale.set(x, y); }

	public final float getRotation() { return rotation; }
	public void setRotation(float r)  {
		rotation = r;
		dirty = 1;
	}
	
	public final boolean isDirty() {
		return dirty == 1;
	}
	
	public void updateMatrix() {
		matrix.setTransform(position, rotation);
	}
	
	public final Matrix4x4 getMatrix() {
		return matrix;
	}
}
