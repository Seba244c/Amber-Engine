package dk.sebsa.amber.math;

import java.util.List;

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
	
	public float min() {return Math.min(x, y);}
	public float max() {return Math.max(x, y);}
	
	public static Vector2f direction(Vector2f start, Vector2f end) {return end.sub(start).normalized();}
	
	public Vector2f normalized() {Vector2f v = new Vector2f(this); v.normalize(); return v;}
	
	public void normalize(Vector2f v) {float normal = (float) (1.0 / Math.sqrt(v.x * v.x + v.y * v.y)); x = v.x * normal;  y = v.y * normal;}
	public void normalize() {float normal = (float) (1.0 / Math.sqrt(x * x + y * y)); x *= normal; y *= normal;}
	
	//Project this vector onto a given line segment
	public Vector2f project(Vector2f lineStart, Vector2f lineEnd) {
		Vector2f dir = direction(lineStart, lineEnd);
		float dot = Mathf.clamp(dot(this.sub(lineStart), dir), 0, distance(lineStart, lineEnd));
		return lineStart.add(dir.mul(dot));
	}
	
	//Project this vector onto a given list of line segments (strip of vertices)
	public Vector2f project(List<Vector2f> vertices) {
		Vector2f closestPoint = project(vertices.get(0), vertices.get(1));
		float closestDistance = distance(closestPoint, this);
		for(int i = 1; i < vertices.size(); i++) {
			int j = (i + 1) % vertices.size();
			
			Vector2f temp = project(vertices.get(i), vertices.get(j));
			float tempDistance = distance(temp, this);
			if(tempDistance < closestDistance) {
				closestPoint.set(temp);
				closestDistance = tempDistance;
			}
		}
		return closestPoint;
	}
	public static float dot(Vector2f v1, Vector2f v2) {return v1.x * v2.x + v1.y * v2.y;}
	public static float distance(Vector2f v1, Vector2f v2) {return (v2.sub(v1)).length();}
	
	public float dot(Vector2f v) {return x * v.x + y * v.y;}
	public float length() {return (float) Math.sqrt(x * x + y * y);}
	public float SqrLength() {return x * x + y * y;}
	
	public Vector2f neg() {return new Vector2f(-x, -y);}
	public boolean equals(Vector2f v) {return v.x == x && v.y == y;}
	
	public String toShortString() {return x + "," + y;}
	public String toString() {return "(" + x + ", " + y + ")";}
	
	public static Vector2f intersection(Vector2f start1, Vector2f end1, Vector2f start2, Vector2f end2) {
		float denom = (end2.y - start2.y) * (end1.x - start1.x) - (end2.x - start2.x) * (end1.y - start1.y);
		if(denom == 0) {return null;}
		
		float a = ((end2.x - start2.x) * (start1.y - start2.y) - (end2.y - start2.y) * (start1.x - start2.x)) / denom;
		float b = ((end1.x - start1.x) * (start1.y - start2.y) - (end1.y - start1.y) * (start1.x - start2.x)) / denom;
		if(a >= 0 && a <= 1 && b >= 0 && b <= 1) {
			return new Vector2f(start1.x + a * (end1.x - start1.x), start1.y + a * (end1.y - start1.y));
		}
		return null;
	}
}

