package dk.sebsa.amber_engine.editor.windows;


import dk.sebsa.amber.AssetManager;
import dk.sebsa.amber.AssetManager.Asset;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.editor.Editor;

public class Types {
	private int offsetY = 0;
	public AssetManager.Asset type = Asset.Sprite;
	
	public Types() {
		Editor.assets.setAssets(type);
	}
	
	public void render(Rect r) {
		offsetY = 0;
		for(AssetManager.Asset type : AssetManager.Asset.values()) {
			boolean bool = false;
			if(this.type.equals(type)) bool = GUI.button(type.name(), new Rect(0, offsetY, r.width, 28), Editor.button, Editor.buttonHover, Press.realesed, false, 0);
			else bool = GUI.button(type.name(), new Rect(0, offsetY, r.width, 28), null, Editor.button, Press.realesed, false, 0);
			offsetY+=28;
			if(bool) {
				this.type = type;
				Editor.assets.setAssets(type);
			}
		}
	}
}
