package dk.sebsa.amber.entity.components;

import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.graph.Sprite;
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
}
