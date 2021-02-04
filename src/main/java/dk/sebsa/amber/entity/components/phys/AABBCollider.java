package dk.sebsa.amber.entity.components.phys;

import java.util.ArrayList;
import java.util.List;

import dk.sebsa.amber.entity.components.SpriteRenderer;
import dk.sebsa.amber.math.Vector2f;

public class AABBCollider extends Collider{
	public Vector2f size = new Vector2f(16, 16);
	public Vector2f anchor = new Vector2f();
	
	public void init() {
		SpriteRenderer sr = (SpriteRenderer) entity.getComponent("SpriteRenderer");
		if(sr != null)
		{
			if(sr.sprite != null)
			{
				size = sr.sprite.offset.getSize().mul(entity.getScale());
				anchor = size.mul(sr.anchor).neg();
			}
		}
	}
	
	public List<Vector2f> getCorners()
	{
		List<Vector2f> corners = new ArrayList<Vector2f>();
		Vector2f anchorPos = entity.getPosition().add(anchor);
		corners.add(anchorPos);
		corners.add(anchorPos.add(new Vector2f(0, size.y)));
		corners.add(anchorPos.add(size));
		corners.add(anchorPos.add(new Vector2f(size.x, 0)));
		return corners;
	}
}