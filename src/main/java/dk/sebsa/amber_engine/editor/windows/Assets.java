package dk.sebsa.amber_engine.editor.windows;

import java.awt.Desktop;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dk.sebsa.amber.Asset;
import dk.sebsa.amber.AssetManager;
import dk.sebsa.amber.entity.SceneManager;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Popup;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.ProjectManager;
import dk.sebsa.amber_engine.editor.Editor;

public class Assets {
	private int offsetY = 0;
	public List<Asset> selectedAssets;
	
	private Sprite internalAsset;

	private List<String> poupStrings = new ArrayList<>();
	private Popup ourPopup;
	public Object copied;
	
	public Assets() {
		internalAsset = Sprite.getSprite(GUI.sheet+".InternalAsset");
		poupStrings.add("Asset used by the engine");
	}
	
	public void render(Rect r) {
		if(selectedAssets == null) return;
		offsetY = 0;
		
		for(Object asset : selectedAssets) {
			boolean bool = false;
			dk.sebsa.amber.Asset a = (Asset) asset;
			
			// Internal asset thing
			Rect buttonRect = new Rect(0, offsetY, r.width, 28);
			if(a.internal) {
				// Make asset rect smaller
				buttonRect = new Rect(0, offsetY, r.width-28, 28);
				
				// Render i icon
				Rect iRect = new Rect(r.width-28, offsetY, 28, 28);
				GUI.button("", iRect, internalAsset, internalAsset, Press.pressed, false);
				
				// Hover
				Rect iHoverRect = iRect.add(r);
				if(iHoverRect.inRect(new Vector2f((float) Main.input.getMouseX(), (float) Main.input.getMouseY()))) {
					if(!GUI.hasPopup()) {
						GUI.setPopup(iHoverRect, poupStrings, null);
						GUI.getPopup().moveToMouse(Main.input);
						
					} else if(GUI.hasPopup() && GUI.getPopup().equals(ourPopup)) {
						GUI.getPopup().moveToMouse(Main.input);
					}
				}
			}
			
			// Render assets
			if(Editor.getInspected()==null) bool = GUI.button(a.name, buttonRect, null, Editor.button, Press.realesed, false);
			else {
				// Check if pressed
				if(Editor.getInspected().equals(asset)) bool = GUI.button(a.name, buttonRect, Editor.button, Editor.buttonHover, Press.realesed, false);
				else bool = GUI.button(a.name, buttonRect, null, Editor.button, Press.realesed, false);
				
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
							SceneManager.loadScene(ProjectManager.getProjectDir() + "scenes/" + ((Asset) asset).name + ".amw");
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
