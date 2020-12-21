package dk.sebsa.amber_engine.rendering;

public abstract class Overlay {
	public abstract boolean render();
	public abstract boolean close();
	
	public enum answer {
		yes,
		no,
		cancel
	}
}
