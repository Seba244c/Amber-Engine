package dk.sebsa.amber.entity.components.phys;

public class BoxCollider extends Collider {
	public boolean isTrigger = false; 
	
	public BoxCollider() {Collider.colliders.add((Collider) this); }
	
}
