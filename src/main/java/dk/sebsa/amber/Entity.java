package dk.sebsa.amber;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.math.Matrix4x4;
import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber.util.Logger;

public class Entity {
	private boolean enabled = true;
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
	
	public void setEnabled(boolean e) {
		setEnabled(e, true);
	}
	
	private void setEnabled(boolean e, boolean childs) {
		if(this.enabled == e) return;
		
		this.enabled = e;
		
		if(childs) {
			for(Entity child : children) {
				child.setEnabled(e);
			}
		}
		
		if(!parent.equals(master) && parent.isEnabled() == false && e == true) { parent.setEnabled(e, false); }
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setId(String newId) {
		id = newId;
	}

	public static void clear() {
		instances.clear();
		master.children.clear();
		Component.clear();
	}
	
	public static List<Entity> getInstances() {
		return instances;
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
	
	public void resetDirty() {dirty = 0;}
	
	public void updateMatrix() {
		matrix.setTransform(position, rotation);
	}
	
	public final Matrix4x4 getMatrix() {
		return matrix;
	}

	public static Entity master() {
		return master;
	}
	
	public static Entity getEntity(String entityId) {
		for(Entity entity : instances) {
			if(entity.id == entityId) return entity;
		}
		return null;
	}
	
	public void delete() {
		this.delete((byte) 1);
	}
	
	private void delete(byte first) {
		// Delete component
		for(Component c : components) {
			c.delete();
		}
		this.enabled = false;
		instances.remove(this);
		
		// Delete children
		for(Entity e : children) {
			parent.children.remove(this);
			e.delete((byte) 0);
		}
		
		// Remove from parent
		if(first == 1) parent.children.remove(this);
	}
	
	public void duplicate() {
		duplicate(parent, (byte) 1);
	}
	
	private void duplicate(Entity p, byte not) {
		Entity e;
		if(not == 1) e = new Entity(this.name + " Clone");
		else e = new Entity(this.name);
		e.parent(p);
		
		// Transform
		e.position = position;
		e.scale = scale;
		e.rotation = rotation;
		
		// Components
		for(Component c : components) {
			Component cc = c.clone(e);
			if(cc!= null) e.addComponent(cc);
		}
		
		// Other values
		e.tag = tag;
		e.setEnabled(enabled);
		
		// Children
		for(Entity child : children) {
			child.duplicate(e, (byte) 0);
		}
	}
}
