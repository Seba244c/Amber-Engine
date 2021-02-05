package dk.sebsa.amber;

import java.io.File;

import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.text.Font;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Rect;

/**
 * @author Sebsa
 * @since Before 0.1.0
 * 
 * A abstract class used by all type of assets.
 * An Amber Asset is an asset wich contains data loaded from files.
 */
public abstract class Asset {
	/**
	 * The name of the asset used for finding and indexing assets
	 */
	public String name;
	
	/**
	 * The file wich the asset is loaded from
	 * May be null if its an internal asset
	 */
	public File file;
	
	/**
	 * Wether the asset is saved within the jar
	 */
	public boolean internal = false;
	
	/**
	 * A big font used in rendering
	 */
	private static Font bigFont = new Font(new java.awt.Font("OpenSans", java.awt.Font.PLAIN, 24));
	
	/**
	 * @param r The maximum area the asset will be rendered in
	 * Renders a GUI representation of the asset and its data
	 */
	public void render(Rect r) {
		if(bigFont.getStringWidth(name) > r.width) {
			GUI.label(name, 0, 0);
		} else {
			GUI.label(name, 0, 0, Color.white(), bigFont);
		}
		GUI.label(this.getClass().getSimpleName(), 0, 26);
	}
}
