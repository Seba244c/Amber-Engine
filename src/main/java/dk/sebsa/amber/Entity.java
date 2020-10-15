package dk.sebsa.amber;

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
	
	private static List<Entity> instances = new ArrayList<Entity>();
	private Entity parent;
	private List<Entity> children = new ArrayList<Entity>();
	private int inline = 0;
	private boolean expanded = false;
	
	private static Entity master = new Entity(false).setExpanded(true);;
	
	public Entity(boolean addToWorlView) {
		id = UUID.randomUUID().toString();
		
		instances.add(this);
		if(addToWorlView) parent(master);
	}
	
	public Entity(String name) {
		id = UUID.randomUUID().toString();
		this.name = name;
		
		instances.add(this);
		parent(master);
	}

	public static void clear() {
		instances.clear();
		master.children.clear();
		Component.clear();
	}
	
	public boolean isExpanded() {
		return expanded;
	}

	public Entity setExpanded(boolean expanded) {
		this.expanded = expanded;
		return this;
	}

	public Entity getParent() {
		return parent;
	}

	public List<Entity> getChildren() {
		return children;
	}
	
	public void parent(Entity e) {		
		if(parent != null) {
			if(!parent.equals(e)) parent.removeChild(this);
			else return;
		}
		
		parent = e;
		parent.children.add(this);
		setInline(parent.getInline() + 1);
	}
	
	public void removeChild(Entity e) {
		for(i = 0; i < children.size(); i++) {
			if(children.get(i)==e) {
				removeChild(i);
				return;
			}
		}
	}
	
	public void removeChild(int v) {
		if(v >= children.size()) return;
		Entity e = children.get(v);
		e.parent(master);
		children.remove(v);
		e.inline = 0;
	}

	public int getInline() {
		return inline;
	}

	public void setInline(int inline) {
		this.inline = inline;
		for(i = 0; i < children.size(); i++) {
			children.get(i).setInline(inline+1);
		}
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

	public static Entity master() {
		return master;
	}
}
