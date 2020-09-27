package dk.sebsa.amber.math;

public class Color {
	public float r = 1;
	public float g = 1;
	public float b = 1;
	public float a = 1;
	
	public Color(float r, float g, float b, float a) {
		this.r = Mathf.clamp(r, 0, 1);
		this.g = Mathf.clamp(g, 0, 1);
		this.b = Mathf.clamp(b, 0, 1);
		this.a = Mathf.clamp(a, 0, 1);
	}
	public Color(float r, float g, float b) {
		this.r = Mathf.clamp(r, 0, 1);
		this.g = Mathf.clamp(g, 0, 1);
		this.b = Mathf.clamp(b, 0, 1);
	}
	
	public static final Color black() {return new Color(0, 0, 0);}
	public static final Color white() {return new Color(1, 1, 1);}
	public static final Color red() {return new Color(1, 0, 0);}
	public static final Color green() {return new Color(0, 1, 0);}
	public static final Color blue() {return new Color(0, 0, 1);}
	public static final Color grey() {return new Color(0.5f, 0.5f, 0.5f);}
	public static final Color wine() {return new Color(0.5f, 0, 0);}
	public static final Color forest() {return new Color(0, 0.5f, 0);}
	public static final Color marine() {return new Color(0, 0, 0.5f);}
	public static final Color yellow() {return new Color(1, 1, 0);}
	public static final Color cyan() {return new Color(0, 1, 1);}
	public static final Color magenta() {return new Color(1, 0, 1);}
	public static final Color transparent() {return new Color(0, 0, 0, 0);}
	
	public String toString() {
		return "("+String.valueOf(r)+", "+String.valueOf(g)+", "+String.valueOf(b)+", "+String.valueOf(a)+")";
	}
	
	public boolean Compare(Color c) {
		return c.r == r && c.g == g && c.b == b && c.a == a;
	}
}
