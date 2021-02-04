package dk.sebsa.amber.entity.components;

import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.math.Matrix4x4;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber.math.Vector2f;

public class SpriteRenderer extends Component {
	public Sprite sprite;
	public Vector2f anchor = new Vector2f(0.5f, 0.5f);
	
	public void setUniforms() {
		sprite.material.shader.setUniform("anchor", anchor);
		
		sprite.material.shader.setUniform("transformMatrix", entity.getMatrix());
		sprite.material.shader.setUniform("objectScale", entity.getScale());
		
		sprite.material.shader.setUniform("screenPos", entity.getPosition());
		
		sprite.material.shader.setUniform("pixelScale", sprite.offset.width, sprite.offset.height);
		
		Rect uvRect = sprite.getUV();
		sprite.material.shader.setUniform("offset", uvRect.x, uvRect.y, uvRect.width, uvRect.height);
		sprite.material.shader.setUniform("depth", entity.depth);
	}
	
	public void onWillRender() {
		if(sprite != null)
			Renderer.addToRender(this);
	}
	
	public boolean contains(Vector2f v) {
		if(sprite == null) return false;
		
		Vector2f[] points = getCorners();
		
		for(int i = 0; i < points.length; i++) {
			Vector2f direction;
			if(i < 3) direction = points[i + 1].sub(points[i]);
			else direction = points[0].sub(points[i]);
			
			if(Math.signum(direction.x * (v.y - points[i].y) - direction.y * (v.x - points[i].x)) >= 0) return false;
		}
		return true;
	}
	
	public Vector2f[] getCorners() {
		if(sprite == null) return null;
		
		Matrix4x4 m = entity.getMatrix();
		Vector2f offset = new Vector2f(sprite.offset.width, sprite.offset.height).mul(anchor).neg();
		
		Vector2f[] ret = new Vector2f[4];
		ret[0] = m.transformPoint(offset);
		ret[1] = m.transformPoint(new Vector2f(offset.x, offset.y + sprite.offset.height));
		ret[2] = m.transformPoint(new Vector2f(offset.x + sprite.offset.width, offset.y + sprite.offset.height));
		ret[3] = m.transformPoint(new Vector2f(offset.x + sprite.offset.width, offset.y));
		
		return ret;
	}
}
