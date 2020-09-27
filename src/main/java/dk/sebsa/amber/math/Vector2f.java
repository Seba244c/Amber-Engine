package dk.sebsa.amber.math;

public class Vector2f {
	public float x;
	public float y;
	
	public Vector2f(Vector2f v) { x = v.x; y = v.y; }
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2f() {
		this.x = 0;
		this.y = 0;
	}
	
	public void zero() {
		this.x = 0;
		this.y = 0;
	}
	
	public void set(Vector2f v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2f add(float v) { return new Vector2f(x + v, y + v); }
	public Vector2f add(Vector2f v) { return new Vector2f(x + v.x, y + v.y); }
	public Vector2f add(float x, float y) { return new Vector2f(x + this.x, y + this.y); }
	
	public Vector2f sub(float v) { return new Vector2f(x - v, y - v); }
	public Vector2f sub(Vector2f v) { return new Vector2f(x - v.x, y - v.y); }
	public Vector2f sub(float x, float y) { return new Vector2f(x - this.x, y - this.y); }
	
	public Vector2f div(float v) { return new Vector2f(x / v, y / v); }
	public Vector2f div(Vector2f v) { return new Vector2f(x / v.x, y / v.y); }
	public Vector2f div(float x, float y) { return new Vector2f(x / this.x, y / this.y); }
	
	public Vector2f mul(float v) { return new Vector2f(x * v, y * v); }
	public Vector2f mul(Vector2f v) { return new Vector2f(x * v.x, y * v.y); }
	public Vector2f mul(float x, float y) { return new Vector2f(x * this.x, y * this.y); }
}

