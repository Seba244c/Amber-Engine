package dk.sebsa.amber_engine.rendering.windows;

import dk.sebsa.amber.graph.GUI;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.graph.GUI.Press;
import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.EngineConfig;
import dk.sebsa.amber_engine.Main;
import dk.sebsa.amber_engine.rendering.Window;

public class EngineSettings extends Window {
	private boolean close = false;
	private Sprite on, off;
	
	public EngineSettings() {
		on = Sprite.getSprite(GUI.sheet+".ToggleOn");
		off = Sprite.getSprite(GUI.sheet+".ToggleOff");
	}
	
	@Override
	public boolean render() {
		Renderer.prepare();
		GUI.window(Main.window.getRect(), "Amber-Engine, Editor Settings", this::render, "Window");
		return close;
	}
	
	private void render(Rect r) {
		// Close buttons
		close = GUI.toggle(false, r.width-16, 0, null, Sprite.getSprite("BlackGUI.XClose"));
		if(close == false) close = GUI.button("Clsoe Window", new Rect(0, r.height-28, r.width, 28), "Button", "ButtonHover", Press.realesed, false);
		
		// Settings
		GUI.label("Engine VSync", 15, 2);
		EngineConfig.configEditorVsync = GUI.toggle(EngineConfig.configEditorVsync, 0, 0, on, off);
	}

	@Override
	public void close() {
		EngineConfig.saveConfig();
		
		Main.window.setVSync(EngineConfig.configEditorVsync);
	}
}
