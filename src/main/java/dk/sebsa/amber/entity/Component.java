package dk.sebsa.amber.entity;

import java.util.ArrayList;
import java.util.List;

import dk.sebsa.amber.Asset;
import dk.sebsa.amber.Entity;
import dk.sebsa.amber.util.Logger;

public abstract class Component extends Asset {
	private boolean enabled = true;
	
	protected Entity entity;
	private static List<Component> instances = new ArrayList<>();
	private static int i;
	
	public Component() {
		name = getClass().getSimpleName();
	}
	
	public void init(Entity owner) {
		if(this.entity==null)
			this.entity = owner;
		instances.add(this);
		awake();
	}
	
	public static void clear() {
		instances.clear();
	}
	
	public void awake() {
		// When the component is intiliazed
	}
	public void update() {
		// Occurs on every frame before rendering
	}
	public void onWillRender() {
		// When ready to render
	}
	
	public final Entity getOwner() {
		return entity;
	}
	
	public final String getName() {
		return name;
	}
	
	public static void updateAll() {
		Component c;
		for(i = 0; i < instances.size(); i++) {
			c = instances.get(i);
			if(c.isEnabled()) c.update();
		}
	}
	
	public static void willRenderAll() {
		Component c;
		for(i = 0; i < instances.size(); i++) {
			c = instances.get(i);
			if(c.isEnabled()) c.onWillRender();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void delete() {
		this.enabled = false;
		instances.remove(this);
	}
	
	public Component clone(Entity entity) {
		Logger.debugLog("Component",  "clone", "This has not been implemented yet");
		return null;
	}
}

