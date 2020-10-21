package dk.sebsa.amber_engine.editor.windows;

import java.awt.Desktop;
import java.io.IOException;
import java.util.List;

import dk.sebsa.amber.Asset;
import dk.sebsa.amber.AssetManager;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.editor.Editor;

public class Assets {
	private int offsetY = 0;
	public List<Asset> selectedAssets;
	
	private Sprite button;
	private Sprite buttonHover;
	private Sprite internalAsset;
	
	public Assets() {
		button = Sprite.getSprite(GUI.sheet +".Button");
		buttonHover = Sprite.getSprite(GUI.sheet +".ButtonHover");
		internalAsset = Sprite.getSprite(GUI.sheet+".InternalAsset");
	}
	
	public void render(Rect r) {
		if(selectedAssets == null) return;
		offsetY = 0;
		
		for(Object asset : selectedAssets) {
			boolean bool = false;
			dk.sebsa.amber.Asset a = (Asset) asset;
			
			Rect buttonRect;
			if(a.internal) {
				buttonRect = new Rect(0, offsetY, r.width-28, 28);
				GUI.button("", new Rect(r.width-28, offsetY, 28, 28), internalAsset, internalAsset, Press.pressed, false);
			} else {
				buttonRect = new Rect(0, offsetY, r.width, 28);
			}
			
			if(Editor.getInspected()==null) bool = GUI.button(a.name, buttonRect, null, button, Press.realesed, false);
			else {
				// Check if pressed
				if(Editor.getInspected().equals(asset)) bool = GUI.button(a.name, buttonRect, button, buttonHover, Press.realesed, false);
				else bool = GUI.button(a.name, buttonRect, null, button, Press.realesed, false);
				
				// Clicks
				Rect clickRect = buttonRect.add(Renderer.area);
				if(clickRect.inRect(Main.input.getMousePosition())) {
					// Right click
					/*if(Main.input.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT) && !GUI.hasPopup()) {
						
					}*/
					
					// Check double click
					if(Main.input.mouseMultiClicked()) {
						if(a.getClass().getSimpleName().contains("Scene"))
						{
							//SceneManager.LoadScene(asset.name());
						} else {
							if(a.file != null) {
								try {Desktop.getDesktop().open(a.file);}
								catch (IOException e) {e.printStackTrace();}
							}
						}
					}
				}
			}
			offsetY+=28;
			
			if(bool) {
				Editor.setInspected(asset);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setAssets(AssetManager.Asset type) {
		selectedAssets = (List<Asset>) AssetManager.typeToList(type);
	}
}
