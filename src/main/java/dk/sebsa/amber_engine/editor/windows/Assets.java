package dk.sebsa.amber_engine.editor.windows;

import java.util.List;

import dk.sebsa.amber.Asset;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.AssetManager;
import dk.sebsa.amber_engine.editor.Editor;

public class Assets {
	private int offsetY = 0;
	public List<Asset> selectedAssets;
	
	private Sprite button;
	private Sprite buttonHover;
	
	public Assets() {
		button = Sprite.getSprite(GUI.sheet +".Button");
		buttonHover = Sprite.getSprite(GUI.sheet +".ButtonHover");
	}
	
	public void render(Rect r) {
		if(selectedAssets == null) return;
		offsetY = 0;
		
		for(Object asset : selectedAssets) {
			boolean bool = false;
			dk.sebsa.amber.Asset a = (Asset) asset;
			
			if(Editor.getInspected()==null) bool = GUI.button(a.name, new Rect(0, offsetY, r.width, 28), null, button, Press.realesed, false);
			else {
				if(Editor.getInspected().equals(asset)) bool = GUI.button(a.name, new Rect(0, offsetY, r.width, 28), button, buttonHover, Press.realesed, false);
				else bool = GUI.button(a.name, new Rect(0, offsetY, r.width, 28), null, button, Press.realesed, false);
			}
			offsetY+=28;
			
			if(bool) {
				Editor.setInspected(asset);
			}
		}
	}
	
	public void setAssets(AssetManager.Asset type) {
		selectedAssets = (List<Asset>) AssetManager.typeToList(type);
	}
}
