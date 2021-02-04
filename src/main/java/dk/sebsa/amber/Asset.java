package dk.sebsa.amber;

import java.io.File;

import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.text.Font;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Rect;

public abstract class Asset {
	public String name;
	public File file;
	public boolean internal = false;
	
	private static Font bigFont = new Font(new java.awt.Font("OpenSans", java.awt.Font.PLAIN, 24));
	
	public void render(Rect r) {
		if(bigFont.getStringWidth(name) > r.width) {
			GUI.label(name, 0, 0);
		} else {
			GUI.label(name, 0, 0, Color.white(), bigFont);
		}
		GUI.label(this.getClass().getSimpleName(), 0, 26);
	}
}
