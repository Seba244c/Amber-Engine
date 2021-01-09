package dk.sebsa.amber_engine.rendering;

import dk.sebsa.amber.math.Rect;

public abstract class Overlay {
	public abstract boolean render();
	public abstract boolean close();
	public abstract Rect layer();
	
	public enum answer {
		yes,
		no,
		cancel
	}
	
}
