package dk.sebsa.amber.math;

public class Vector3f {
	public float x;
	public float y;
	public float z;
	
	public Vector3f(Vector3f v) { x = v.x; y = v.y; z = v.z; }
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3f() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public void zero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public void set(Vector3f v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3f add(float v) { return new Vector3f(x + v, y + v, v+z); }
	public Vector3f add(Vector3f v) { return new Vector3f(x + v.x, y + v.y, z + v.z); }
	public Vector3f add(float x, float y, float z) { return new Vector3f(x + this.x, y + this.y, z + this.z); }
	
	public Vector3f sub(float v) { return new Vector3f(x - v, y - v, z-v); }
	public Vector3f sub(Vector3f v) { return new Vector3f(x - v.x, y - v.y, z - v.z); }
	public Vector3f sub(float x, float y, float z) { return new Vector3f(x - this.x, y - this.y, z - this.z); }
	
	public Vector3f div(float v) { return new Vector3f(x / v, y / v, z/v); }
	public Vector3f div(Vector3f v) { return new Vector3f(x / v.x, y / v.y, z / v.z); }
	public Vector3f div(float x, float y, float z) { return new Vector3f(x / this.x, y / this.y, z / this.z); }
	
	public Vector3f mul(float v) { return new Vector3f(x * v, y * v, z * v); }
	public Vector3f mul(Vector3f v) { return new Vector3f(x * v.x, y * v.y, z * v.z); }
	public Vector3f mul(float x, float y, float z) { return new Vector3f(x * this.x, y * this.y, z * this.z); }
}

