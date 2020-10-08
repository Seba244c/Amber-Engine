package dk.sebsa.amber_engine.editor.windows;

import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.AssetManager;
import dk.sebsa.amber_engine.AssetManager.Asset;
import dk.sebsa.amber_engine.editor.Editor;

public class Types {
	private int offsetY = 0;
	public AssetManager.Asset type = Asset.Sprite;
	
	private Sprite button;
	private Sprite buttonHover;
	
	public Types() {
		button = Sprite.getSprite(GUI.sheet +".Button");
		buttonHover = Sprite.getSprite(GUI.sheet +".ButtonHover");
		Editor.assets.setAssets(type);
	}
	
	public void render(Rect r) {
		offsetY = 0;
		for(AssetManager.Asset type : AssetManager.Asset.values()) {
			boolean bool = false;
			if(this.type.equals(type)) bool = GUI.button(type.name(), new Rect(0, offsetY, r.width, 28), button, buttonHover, Press.realesed, false);
			else bool = GUI.button(type.name(), new Rect(0, offsetY, r.width, 28), null, button, Press.realesed, false);
			offsetY+=28;
			if(bool) {
				this.type = type;
				Editor.assets.setAssets(type);
			}
		}
	}
}
