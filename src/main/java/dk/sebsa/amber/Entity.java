package dk.sebsa.amber;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.entity.components.phys.Collider;
import dk.sebsa.amber.math.Mathf;
import dk.sebsa.amber.math.Matrix4x4;
import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber.util.Logger;
import dk.sebsa.amber_engine.editor.Editor;

public class Entity {
	/**
	 * Wether the entities components is active
	 */
	private boolean enabled = true;
	private int i;
	/**
	 * The depth wich the entity is rendered in, lower depth means it will be more in the foreground
	 */
	public float depth = 0;
	
	/**
	 * The general tag used to easily identify groups of entities
	 */
	public String tag = "Untagged";
	/**
	 * The name of entity
	 */
	public String name = "New Entity";
	
	/**
	 * Wether the entities matrix is up to date
	 */
	private byte dirty = 1;
	
	// Transform vv
	private Vector2f position = new Vector2f();
	private Vector2f scale = new Vector2f(1, 1);
	private float rotation = 0;
	private Vector2f localPosition = new Vector2f();
	private Vector2f localScale = new Vector2f(1, 1);
	private float localRotation = 0;
	// Transform AA
	
	/**
	 * The matrix used in rendering of the entity
	 */
	private Matrix4x4 matrix = new Matrix4x4();
	private static Matrix4x4 temp = new Matrix4x4();
	
	/**
	 * All components under the entity
	 */
	private List<Component> components = new ArrayList<Component>();
	/**
	 * The uniqe id of this entity
	 */
	private String id;
	/**
	 * All used entites
	 */
	private static List<Entity> instances = new ArrayList<Entity>();
	/**
	 * The parent wich this entity is below.
	 * The "transform" is always calculated base off the parents
	 */
	private Entity parent;
	/**
	 * The children parented under this entity
	 */
	private List<Entity> children = new ArrayList<Entity>();
	/**
	 * Used to render the entity in the amber engine worldview
	 */
	private int inline = 0;
	/**
	 * Wether the entity is expanded in the amber engine editor worldview
	 */
	private boolean expanded = false;
	
	/**
	 * The master entity wich nearly all entities is parented under
	 */
	private static Entity master = new Entity(false).setExpanded(true);
	
	/**
	 * The collider used by the entity when calculating collisions
	 */
	private Collider collider;
	
	/**
	 * @param addToWorlView Wether to parent it to master
	 */
	public Entity(boolean addToWorlView) {
		id = UUID.randomUUID().toString();
		
		instances.add(this);
		if(addToWorlView) parent(master);
	}
	
	/**
	 * @param name The name of the new entity
	 */
	public Entity(String name) {
		id = UUID.randomUUID().toString();
		this.name = name;
		
		instances.add(this);
		parent(master);
	}
	
	/**
	 * Sets wether the entities components is active, with some default values
	 * More info in setEnabled(boolean, boolean)
	 * @param e The new value
	 */
	public void setEnabled(boolean e) {
		setEnabled(e, true);
	}
	
	/**
	 * Sets wether the entities components is active
	 * @param e The new value
	 * @param childs Wether to change the childrens value to(alwaus true)
	 */
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
	
	/**
	 * @return Wether the entities components is active
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Sets the id of the entity to newId
	 * @param newId The new id
	 */
	public void setId(String newId) {
		id = newId;
	}

	/**
	 * deletes all entities
	 */
	public static void clear() {
		for(Entity e : master.children) {
			e.delete((byte) 0);
		}
		master.children.clear();
	}
	
	/**
	 * @return A list of ALL current entities
	 */
	public static List<Entity> getInstances() {
		return instances;
	}
	
	/**
	 * @return Wether the entity is expanded in the editors WorldView
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * Sets wether the entity should be expanded in the editor WorldView
	 * @param expanded The new value
	 * @return this
	 */
	public Entity setExpanded(boolean expanded) {
		this.expanded = expanded;
		return this;
	}

	/**
	 * @return This entities parent null if master
	 */
	public Entity getParent() {
		if(parent == master) return null;
		return parent;
	}

	/**
	 * @return A list of children
	 */
	public List<Entity> getChildren() {
		return children;
	}
	
	/**
	 * Changes the åarent of this entity to e
	 * @param e The new parent
	 */
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
	
	/**
	 * Remove child e
	 * @param e The child
	 */
	public void removeChild(Entity e) {
		for(i = 0; i < children.size(); i++) {
			if(children.get(i)==e) {
				removeChild(i);
				return;
			}
		}
	}
	
	/**
	 * Remove child and index v
	 * @param v
	 */
	public void removeChild(int v) {
		if(v >= children.size()) return;
		Entity e = children.get(v);
		
		children.remove(v);
		e.inline = 0;
	}

	/**
	 * @return Inline
	 */
	public int getInline() {
		return inline;
	}

	/**
	 * Sets inline to inline
	 * @param inline The new inline value
	 */
	public void setInline(int inline) {
		this.inline = inline;
		for(i = 0; i < children.size(); i++) {
			children.get(i).setInline(inline+1);
		}
	}
	
	/**
	 * Finds a component with the same class.SimpleName() as name
	 * @param name The name to look for
	 * @return The componenent, may be null if it does not exist
	 */
	public Component getComponent(String name) {
		for(i = 0; i < components.size(); i++) {
			Component c = components.get(i);
			if(c.getName().equals(name)) return c;
		}
		return null;
	}
	
	/**
	 * Removes c from components
	 * @param c A component that is on this entity
	 */
	public void removeComponent(Component c) {
		if(c == collider) collider = null;
		components.remove(c);
	}
	
	/**
	 * Adds a new component to this entity
	 * @param c The component
	 * @return The component
	 */
	public Component addComponent(Component c) {
		if(c == null) { Logger.errorLog("Entity",  name, "Could not find component!"); return null; }
		else if(c instanceof Collider) {
			if(collider != null) {
				Logger.errorLog("Entity",  name, "Only one collider pr entity!"); return null;
			} else collider = (Collider) c;
		}
		components.add(c);
		c.init(this);
		return c;
	}

	/**
	 * @return This entities id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return A list containing all of this entities components
	 */
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
	
	/**
	 * @return Wether this entity is dirty
	 */
	public final boolean isDirty() {
		return dirty == 1;
	}
	
	/**
	 * Resest its dirt value
	 */
	public void resetDirty() {dirty = 0;}
	
	/**
	 * @return This entities matrix
	 */
	public final Matrix4x4 getMatrix() {
		if(matrix.isDirt()) matrix.setTransformation(position, rotation, scale); matrix.setDirty(false);
		return matrix;
	}

	/**
	 * @return The master entity
	 */
	public static Entity master() {
		return master;
	}
	
	/**
	 * Finds the entity with an specific id
	 * @param entityId The id of the entity we are trying to find
	 * @return The entity
	 */
	public static Entity getEntity(String entityId) {
		for(Entity entity : instances) {
			if(entity.id == entityId) return entity;
		}
		return null;
	}
	
	/**
	 * Deletes and disables an entity, with some default values
	 * Look at delte(byte) for more details
	 */
	public void delete() {
		this.delete((byte) 1);
	}
	
	/**
	 * Deletes and disables an entity.
	 * @param first Wether the entity should be removed from its parent(always yes if it is the first entity to be removed", all children will have this set to false
	 */
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
	
	/**
	 * Calls duplicate(Entity, byte), with some default values
	 * @return Look at duplicate(Entity, byte)
	 */
	public Entity duplicate() {
		return duplicate(parent, (byte) 1);
	}
	
	/**
	 * Attempts to create an exact copy(excluding id) of this entity
	 * This also clones the children onto the new entity
	 * And parents it to the parent p
	 * @param p The new clones parent
	 * @param not Wether the entity should have "CLone in the name", all children will have this set to false
	 * @return The clone
	 */
	private Entity duplicate(Entity p, byte not) {
		Entity e = copy(not);

		e.parent(p);
		return e;
	}
	
	/**
	 * Attempts to create an exact copy(excluding id) of this entity
	 * This also clones the children onto the new entity
	 * The only diffrence is that the clone is not parented
	 * @param not Wether the entity should have "CLone in the name", all children will have this set to false
	 * @return The clone
	 */
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
	
	/**
	 * Happens on collision if the collider IS an Trigger
	 * Calls all the components of this entitys onTrigger method
	 */
	public void callTriggerCallback() {
		for(int i = 0; i < components.size(); i++) {
			Component component = components.get(i);
			component.onTrigger();
		}
	}
	
	/**
	 * Happens on collision if the collider is NOT an Trigger
	 * Calls all the components of this entitys onCollsion method
	 */
	public void callCollisionCallback() {
		for(int i = 0; i < components.size(); i++) {
			Component component = components.get(i);
			component.onCollision();
		}
	}
}
