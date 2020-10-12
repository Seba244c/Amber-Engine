package dk.sebsa.amber.entity;

import java.util.ArrayList;
import java.util.List;

import dk.sebsa.amber.Asset;
import dk.sebsa.amber.Entity;

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
		for(i = 0; i < instances.size(); i++) {
			instances.get(i).update();
		}
	}
	
	public static void willRenderAll() {
		for(i = 0; i < instances.size(); i++) {
			instances.get(i).onWillRender();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}

