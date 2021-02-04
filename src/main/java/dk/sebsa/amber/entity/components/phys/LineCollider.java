package dk.sebsa.amber.entity.components.phys;

import dk.sebsa.amber.entity.components.SpriteRenderer;
import dk.sebsa.amber.math.Vector2f;

public class LineCollider extends Collider {
	public Vector2f start = new Vector2f();
	public Vector2f end = new Vector2f(16, 0);
	
	public void init() {

		SpriteRenderer sr = (SpriteRenderer) entity.getComponent("SpriteRenderer");
		if(sr != null) {
			if(sr.sprite != null) {
				Vector2f scale = sr.sprite.offset.getSize().mul(entity.getScale());
				start = scale.mul(sr.anchor).neg();
				end = start.add(new Vector2f(scale.x, 0));
			}
		}
	}
	
	public Vector2f getWorldStart() {return entity.getPosition().add(start);}
	public Vector2f getWorldEnd() {return entity.getPosition().add(end);}
}
