package dk.sebsa.amber_engine;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import dk.sebsa.amber.Asset;
import dk.sebsa.amber.AssetManager;
import dk.sebsa.amber.Entity;
import dk.sebsa.amber.entity.WorldManager;
import dk.sebsa.amber.util.Logger;
import dk.sebsa.amber_engine.editor.Editor;
import dk.sebsa.amber_engine.rendering.EngineRenderer;

public class InputHandler {
	public static void update() {
		if(Main.input.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
			if(Main.inPlayMode)
				try {
					Main.togglePlaymode();
				} catch (IOException e) { e.printStackTrace(); }
			else EngineRenderer.escape();
		}
		
		if(Main.input.isKeyPressed(GLFW.GLFW_KEY_DELETE)) {
			if(Editor.getInspected() == null) return;
			Editor.action();
			Editor.setInspected(null);
			
			Object selected = Editor.getInspected();
			if(selected instanceof Entity) {
				((Entity) selected).delete();
			} else {
				Asset inspected = (Asset) selected;
				if(!WorldManager.getWorld().equals(inspected) && !inspected.internal) {
					if(inspected.file != null)inspected.file.delete();
					AssetManager.typeToList(AssetManager.assetToE(inspected)).remove(inspected);
					return;
				}
			}
		}

		if(Main.input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
			if(Main.input.isKeyPressed(GLFW.GLFW_KEY_C)) {
				// Copy
				Object inspected = Editor.getInspected();
				if(inspected != null) {
					// Copy Worldview
					if(inspected instanceof Entity) {
						Editor.worldView.copiedEntity = (Entity) Editor.getSelected();
					}
					else {
						Editor.assets.copied = inspected;
					}
				}
			} else if(Main.input.isKeyPressed(GLFW.GLFW_KEY_V)) {
				// Copy
				Object inspected = Editor.getInspected();
				if(inspected != null) {
					if(inspected instanceof Entity) {
						Editor.worldView.paste();
						Editor.action();
					}
					else {
						// PASTE ASSETS
					}
				}
			} else if(Main.input.isKeyPressed(GLFW.GLFW_KEY_S)) {
				try {
					WorldManager.saveWorld();
				} catch (IOException e) {
					Logger.errorLog("InputHandler", "update", "Could not save scene: " + WorldManager.getWorld());
				}
			}
		}
	}
}
