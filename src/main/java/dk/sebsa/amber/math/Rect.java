package dk.sebsa.amber.math;

import org.joml.Math;

public class Rect {
	public float x;
	public float y;
	public float width;
	public float height;
	
	public Rect(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	
	public boolean inRect(Vector2f v) {
		return v.x > x && v.x < x+width && v.y > y && v.y < y+height;
	}
	
	public boolean intersects(Rect r) {
		return !(x > r.x + r.width || x + width < r.x || y > r.y + r.height || y + height < r.y);
	}
	
	public Rect getIntersection(Rect r) {
		if(!intersects(r)) return null;
		Vector2f v = new Vector2f(Math.max(x, r.x), Math.max(y, r.y));
		return new Rect(v.x, v.y, Math.min(x + width, r.x + r.width) - v.x, Math.min(y + height, r.y + r.height) - v.y);
	}
	
	public void set(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	
	public Rect add(float x, float y, float w, float h) {
		this.x += x;
		this.y += y;
		this.width += w;
		this.height += h;
		return this;
	}
	
	public Rect add(Rect r) {
		this.x += r.x;
		this.y += r.y;
		this.width += r.width;
		this.height += r.height;
		return this;
	}
	
	public Rect addToNew(Rect r) {
		return copy().add(r);
	}
	
	public void addPosition(float x, float y) {
		this.x += x;
		this.y += y;
	}
	
	public void addPosition(Rect r) {
		this.x += r.x;
		this.y += r.y;
	}
	
	public Rect copy() {
		return new Rect(x, y, width, height);
	}
}
