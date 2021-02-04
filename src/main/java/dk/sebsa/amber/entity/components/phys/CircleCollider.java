package dk.sebsa.amber.entity.components.phys;

import dk.sebsa.amber.entity.components.SpriteRenderer;
import dk.sebsa.amber.math.Vector2f;

public class CircleCollider extends Collider
{
	public float radius = 16;
	public Vector2f anchor = new Vector2f();
	
	public void init() {
		SpriteRenderer sr = (SpriteRenderer) entity.getComponent("SpriteRenderer");
		if(sr != null) {
			if(sr.sprite != null) {
				Vector2f scale = sr.sprite.offset.getSize().mul(entity.getScale()).mul(0.5f);
				radius = scale.min();
				
				//This anchor setting is wrong
				anchor = scale.mul(sr.anchor).neg().add(radius).sub(scale.mul(sr.anchor));
			}
		}
	}
	
	public Vector2f center() {return entity.getPosition().add(anchor);}
}
