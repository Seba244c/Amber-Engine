package dk.sebsa.amber_engine.editor.windows;

import java.awt.Desktop;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dk.sebsa.amber.Asset;
import dk.sebsa.amber.AssetManager;
import dk.sebsa.amber.entity.World;
import dk.sebsa.amber.entity.WorldManager;
import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Popup;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.graph.SpriteSheet;
import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber.util.Logger;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.editor.Editor;
import dk.sebsa.amber_engine.rendering.EngineRenderer;
import dk.sebsa.amber_engine.rendering.Overlay.answer;
import dk.sebsa.amber_engine.rendering.overlays.SaveWorld;

public class Assets {
	private int offsetY = 0;
	private int scroll = 0;
	public List<Asset> selectedAssets;
	
	private Sprite internalAsset;
	
	private List<String> poupStrings = new ArrayList<>();
	private Popup ourPopup;
	public Object copied;
	private World newWorld;
	
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
			
			// BUtton
			Rect buttonRect = new Rect(0, offsetY, r.width, 28);
			
			// Internal asset thing
			if(a.internal) {
				// Make asset rect smaller
				buttonRect = new Rect(0, offsetY, r.width-28, 28);
				
				// Render i icon
				Rect iRect = new Rect(r.width-28, offsetY, 28, 28);
				GUI.button("", iRect, internalAsset, internalAsset, Press.pressed, false, 0);
				
				// Hover
				Rect iHoverRect = iRect.add(r.x, r.y, 0, 0);
				if(iHoverRect.inRect(new Vector2f((float) Main.input.getMouseX(), (float) Main.input.getMouseY()))) {
					if(!GUI.hasPopup()) {
						GUI.setPopup(iHoverRect, poupStrings, null);
						GUI.getPopup().moveToMouse(Main.input);
						
					} else if(GUI.hasPopup() && GUI.getPopup().equals(ourPopup)) {
						GUI.getPopup().moveToMouse(Main.input);
					}
				}
			}
			
			offsetY+=28;
			int extra = 0;
			
			if(Editor.types.type.equals(AssetManager.Asset.World)) {
				// Scroll (IK its stupid to copy paste everywhere but this is needed due to spreisheets)
				scroll = Renderer.setScrollView(selectedAssets.size()*28, scroll);
				
				// Check if pressed
				if(WorldManager.getWorld().equals(a)) GUI.button(a.name, buttonRect, Editor.button, Editor.buttonHover, Press.realesed, false, 0);
				else GUI.button(a.name, buttonRect, null, Editor.button, Press.realesed, false, 0);
				
				// Open world
				Rect clickRect = buttonRect.add(Renderer.getArea().rect);
				if(Main.input.mouseMultiClicked() && clickRect.inRect(Main.input.getMousePosition())) {
					newWorld = (World) a;
					if(!WorldManager.getWorld().saved) EngineRenderer.setOverlay(new SaveWorld(this::changeWorld));
					else changeWorld(answer.no);
				}
			} else if(Editor.types.type.equals(AssetManager.Asset.SpriteSheet)) {
				// scroll
				int max = 0;
				for(SpriteSheet sheet : Sprite.getSheets()) {
					max += 28;
					if(sheet.isOpen()) {
						max+= 28*sheet.sprites.size();
					}
				}
				scroll = Renderer.setScrollView(max, scroll);
				
				SpriteSheet sheet = ((SpriteSheet) asset);
				// Check if pressed
				if(Editor.getInspected()==null || !Editor.getInspected().equals(asset)) bool = GUI.button(a.name, buttonRect, null, Editor.button, Press.realesed, false, 0);
				else bool = GUI.button(a.name, buttonRect, Editor.button, Editor.buttonHover, Press.realesed, false, 0);
				
				// Expand
				sheet.setOpen(GUI.toggle(((SpriteSheet) asset).isOpen(), GUI.defaultFont.getStringWidth(a.name)+5, offsetY-22, Editor.arrowDown, Editor.arrowRight, 0));
				
				// Render open
				if(sheet.isOpen()) {
					for(int i = 0; i < sheet.sprites.size(); i++) {
						Sprite sprite = sheet.sprites.get(i);
						buttonRect = new Rect(20, offsetY+extra, r.width-20, 28);
						
						// See if selected
						boolean selected = false;
						if(Editor.getInspected() != null && Editor.getInspected().equals(sprite)) selected = true;
						
						// Render
						if(selected) bool = GUI.button(sprite.name, buttonRect, Editor.button, Editor.buttonHover, Press.realesed, false, 0);
						else bool = GUI.button(sprite.name, buttonRect, null, Editor.button, Press.realesed, false, 0);
						
						// Clicks
						Rect clickRect = buttonRect.addToNew(Renderer.getArea().rect);
						if(clickRect.inRect(Main.input.getMousePosition())) {
							// Check double click
							if(Main.input.mouseMultiClicked()) {
								if(a.file != null) {
									try {Desktop.getDesktop().open(a.file);}
									catch (IOException e) {e.printStackTrace();}
								}
							}
						}
						
						extra+=28;
					}
				}
			}
			else if(Editor.getInspected()==null) bool = GUI.button(a.name, buttonRect, null, Editor.button, Press.realesed, false, 0);
			else {
				// Scroll (IK its stupid to copy paste everywhere but this is needed due to spreisheets)
				scroll = Renderer.setScrollView(selectedAssets.size()*28, scroll);
				
				// Check if pressed
				if(Editor.getInspected().equals(asset)) bool = GUI.button(a.name, buttonRect, Editor.button, Editor.buttonHover, Press.realesed, false, 0);
				else bool = GUI.button(a.name, buttonRect, null, Editor.button, Press.realesed, false, 0);
				
				// Clicks
				Rect clickRect = buttonRect.add(Renderer.getArea().rect);
				if(clickRect.inRect(Main.input.getMousePosition())) {
					// Check double click
					if(Main.input.mouseMultiClicked()) {
						if(a.file != null) {
							try {Desktop.getDesktop().open(a.file);}
							catch (IOException e) {e.printStackTrace();}
						}
					}
				}
			}
			
			// Delete asset
			if(!WorldManager.getWorld().equals(a) && !a.internal && GUI.toggle(false, r.width-18, offsetY-22, null, Editor.x, 0)) {
				if(a.file != null) a.file.delete();
				AssetManager.typeToList(AssetManager.assetToE(a)).remove(a);
				return;
			}
			
			if(bool) {
				Editor.setInspected(asset);
			}
			
			offsetY += extra;
		}
	}
	
	public void changeWorld(answer answerIn) {
		if(answerIn == answer.cancel) { EngineRenderer.setOverlay(null); return; }
		else if(answerIn == answer.yes) {
			try {
				WorldManager.saveWorld();
			} catch (IOException e) {
				Logger.errorLog("InputHandler", "update", "Could not save scene: " + WorldManager.getWorld());
			}
		}

		WorldManager.openWorld(newWorld);
		EngineRenderer.setOverlay(null);
	}
	
	@SuppressWarnings("unchecked")
	public void setAssets(AssetManager.Asset type) {
		selectedAssets = (List<Asset>) AssetManager.typeToList(type);
	}
}
