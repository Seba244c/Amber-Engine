package dk.sebsa.amber;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.math.Mathf;
import dk.sebsa.amber.math.Matrix4x4;
import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber.util.Logger;
import dk.sebsa.amber_engine.editor.Editor;

public class Entity {
	private boolean enabled = true;
	private int i;
	public float depth = 0;
	
	public String tag = "Untagged";
	public String name = "New Entity";
	
	private byte dirty = 1;
	
	// Transform vv
	private Vector2f position = new Vector2f();
	private Vector2f scale = new Vector2f(1, 1);
	private float rotation = 0;
	private Vector2f localPosition = new Vector2f();
	private Vector2f localScale = new Vector2f(1, 1);
	private float localRotation = 0;
	// Transform AA
	
	private Matrix4x4 matrix = new Matrix4x4();
	private static Matrix4x4 temp = new Matrix4x4();
	private List<Component> components = new ArrayList<Component>();
	private String id;
	
	private static List<Entity> instances = new ArrayList<Entity>();
	private Entity parent;
	private List<Entity> children = new ArrayList<Entity>();
	private int inline = 0;
	private boolean expanded = false;
	
	private static Entity master = new Entity(false).setExpanded(true);;
	
	//private Collider collider;
	
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
	
	public static Entity find(String id) {
		for(Entity e : instances) {
			if(e.getId().equals(id)) return e;
		}
		return null;
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
		for(Entity e : master.children) {
			e.delete((byte) 0);
		}
		master.children.clear();
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
		if(parent == master) return null;
		return parent;
	}

	public List<Entity> getChildren() {
		return children;
	}
	
	public void parent(Entity e) {		
		if(e == null) e = master;
		if(parent != null) {
			if(parent != e) parent.removeChild(this);
			else return;
		}
		parent = e;
		parent.children.add(this);
		setInline(parent.inline + 1);
		recalculateLocalTransformation();
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
	
	// -------------------------- TRANSFORM
	// POS
	public final Vector2f getPosition() {return position;}
	public void setPosition(Vector2f v) {setPosition(v.x, v.y);}
	public void setPosition(float x, float y){position.set(x, y); recalculateLocalTransformation();}
	public final Vector2f getLocalPosition() {return localPosition;}
	public void setLocalPosition(Vector2f v) {setLocalPosition(v.x, v.y);}
	public void setLocalPosition(float x, float y) {localPosition.set(x, y); recalculateGlobalTransformations();}
	public void move(Vector2f v) { setPosition(position.add(v)); }
	public void move(float x, float y) { move(new Vector2f(x, y)); }
	// Scale
	public final Vector2f getScale() {return scale;}
	public void setScale(Vector2f v) {setScale(v.x, v.y);}
	public void setScale(float x, float y) {scale.set(x, y); recalculateLocalTransformation();}
	public final Vector2f getLocalScale() {return localScale;}
	public void setLocalScale(Vector2f v) {setLocalScale(v.x, v.y);}
	public void setLocalScale(float x, float y) {localScale.set(x, y); recalculateGlobalTransformations();}
	// ROT
	public final float getRotation() {return rotation;}
	public void setRotation(float v) {rotation = Mathf.wrap(v, 0, 360); recalculateLocalTransformation();}
	public final float getLocalRotation() {return localRotation;}
	public void setLocalRotation(float v) {localRotation = Mathf.wrap(v, 0, 360); recalculateGlobalTransformations();}
	// Recalculate
	private void recalculateLocalTransformation()
	{
		dirty = 1;
		matrix.setDirty(true);
		localScale = scale.div(parent.scale);
		localRotation = Mathf.wrap(rotation - parent.rotation, 0, 360);
		temp.setTransformation(null, -parent.rotation, new Vector2f(1, 1).div(parent.scale));
		localPosition.set(temp.transformPoint(position.sub(parent.position)));
		
		for(int i = 0; i < children.size(); i++) children.get(i).recalculateGlobalTransformations();
	}
	private void recalculateGlobalTransformations()
	{
		dirty = 1;
		matrix.setDirty(true);
		scale = parent.scale.mul(localScale);
		rotation = Mathf.wrap(parent.rotation + localRotation, 0, 360);
		position.set(parent.getMatrix().transformPoint(localPosition));
		
		for(int i = 0; i < children.size(); i++) children.get(i).recalculateGlobalTransformations();
	}
	
	public static void recalculate() {for(int i = 0; i < master.children.size(); i++) master.children.get(i).recalculateGlobalTransformations();}
	// -------------------------- TRANSFORM END
	
	public final boolean isDirty() {
		return dirty == 1;
	}
	
	public void resetDirty() {dirty = 0;}
	
	public final Matrix4x4 getMatrix() {
		if(matrix.isDirt()) matrix.setTransformation(position, rotation, scale); matrix.setDirty(false);
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
		while(!components.isEmpty()) {
			components.get(0).delete();
		}
		this.enabled = false;
		instances.remove(this);
		
		// Delete children
		for(Entity e : children) {
			e.delete((byte) 0);
		}
		
		// Remove from parent
		if(first == 1) parent.children.remove(this);
	}
	
	public Entity duplicate() {
		return duplicate(parent, (byte) 1);
	}
	
	private Entity duplicate(Entity p, byte not) {
		Entity e = copy(not);

		e.parent(p);
		return e;
	}
	
	public Entity copy(byte not) {
		Editor.action();
		Entity e;
		if(not == 1) e = new Entity(this.name + " Clone");
		else e = new Entity(this.name);
		
		// Transform
		e.position = position;
		e.scale = scale;
		e.rotation = rotation;
		e.depth = depth;
		
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
		return e;
	}
}
