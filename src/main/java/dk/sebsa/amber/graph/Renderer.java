package dk.sebsa.amber.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

import dk.sebsa.amber.entity.components.SpriteRenderer;
import dk.sebsa.amber.io.Input;
import dk.sebsa.amber.io.Window;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Matrix4x4;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;

public class Renderer {
	private static Map<Material, List<SpriteRenderer>> batch = new HashMap<Material, List<SpriteRenderer>>();
	private static Mesh guiMesh;
	private static Mesh mainMesh;
	private static Matrix4x4 projection;
	private static FBO fbo;
	private static Matrix4x4 ortho;
	
	private static int area = 0;
	private static List<GUIArea> areas = new ArrayList<>();
	
	private static Window window;
	
	public static void init(Input input, Window win) {
		float[] square = new float[] {
				0, 1, 1, 1, 1, 0,
				1, 0, 0, 0, 0, 1
		};
		float[] uv = new float[] {
				0, 0, 1, 0, 1, 1,
				1, 1, 0, 1, 0, 0
		};
		window = win;
		
		guiMesh = new Mesh(square, square);
		mainMesh = new Mesh(square, uv);
		updateFBO(window.getWidth(), window.getHeight());
		
		GUI.init(input);
	}
	
	public static void cleanup() {
		guiMesh.cleanup();
		mainMesh.cleanup();
		fbo.cleanup();
	}
	
	public static void addToRender(SpriteRenderer renderer) {
		List<SpriteRenderer> matRenderers = batch.get(renderer.sprite.material);
		if(matRenderers == null) {
			matRenderers = new ArrayList<SpriteRenderer>();
			batch.put(renderer.sprite.material, matRenderers);
		}
		
		matRenderers.add(renderer);
	}
	
	public static void render(Rect r) {
		fbo.bindFrameBuffer();
		glEnable(GL_DEPTH_TEST);
		glClearColor(0, 1, 1, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		float w = window.getWidth();
		float h = window.getHeight();
		float halfW = w * 0.5f;
		float halfH = h * 0.5f;
		
		projection = Matrix4x4.ortho(-halfW, halfW, halfH, -halfH, -1, 1);
		
		mainMesh.bind();
		
		for(Material material : batch.keySet()) {
			material.bind();
			material.shader.setUniform("projection", projection);
			material.shader.setMatColor(material.color);
			
			List<SpriteRenderer> renderers = batch.get(material);
			for(SpriteRenderer renderer : renderers) {
				renderer.setUniforms();
				mainMesh.render();
			}
			
			material.unbind();
		}
		
		mainMesh.unbind();
		batch.clear();
		fbo.unBind();
		
		prepare();
		drawTextureWithTextCoords(fbo.getTexture(), r, new Rect(r.x / w, r.y / h, r.width / w, r.height / h));
		unprepare();
	}
	
	public static void prepare() {
		// Disable 3d
		glDisable(GL_DEPTH_TEST);
		
		// Render preparation
		Main.engineShader.bind();
		ortho = Matrix4x4.ortho(0, window.getWidth(), window.getHeight(), 0, -1, 1);
		Main.engineShader.setUniform("projection", ortho);
		guiMesh.bind();
		
		// Area
		areas.clear();
		areas.add(new GUIArea(window.getRect().copy()));
		area = 0;
	}
	
	public static void unprepare() {
		Main.engineShader.unbind();
		guiMesh.unbind();
	}
	
	public static void drawTextureWithTextCoords(Texture tex, Rect drawRect, Rect uvRect) {
		drawTextureWithTextCoords(tex, drawRect, uvRect, Color.white());
	}
	
	public static void drawTextureWithTextCoords(Texture tex, Rect drawRect, Rect uvRect, Color c) {
		// Draw areasd
		GUIArea a = areas.get(area);
		if(a.rect == null) return;
		
		Rect r = a.rect.getIntersection(new Rect(drawRect.x + a.rect.x, (drawRect.y + a.rect.y) - a.getScroll(), drawRect.width, drawRect.height));
		if(r == null) return;
		
		// uvreact
		float x = uvRect.x + ((((r.x - drawRect.x) - a.rect.x) / drawRect.width) * uvRect.width);
		float y = uvRect.y + ((((r.y - drawRect.y) - (a.rect.y - a.getScroll())) / drawRect.height) * uvRect.height);
		Rect u = new Rect(x, y, (r.width / drawRect.width) * uvRect.width, (r.height / drawRect.height) * uvRect.height);
		
		// Draw
		tex.bind();
		
		Main.engineShader.setUniform("offset", u.x, u.y, u.width, u.height);
		Main.engineShader.setUniform("pixelScale", r.width, r.height);
		Main.engineShader.setUniform("screenPos", r.x, r.y);
		Main.engineShader.setMatColor(c);
		
		guiMesh.render();
		tex.unbind();
	}
	
	public static void updateFBO(int width, int height) {
		fbo = new FBO(width, height);
	}
	
	public static int setScrollView(int scrollMax, int offset) {
		GUIArea a = areas.get(area);
		a.maxScroll = scrollMax;
		return a.scroll(offset);
	}
	
	public static void beginArea(Rect r) {
		GUIArea a = areas.get(area);
		areas.add(new GUIArea(a.rect.getIntersection(new Rect(a.rect.x + r.x, (a.rect.y + r.y) - a.getScroll(), r.width, r.height))));
		area += 1;
	}
	
	public static void endArea() {
		if(areas.size() == 1) return;
		areas.remove(area);
		area -= 1;
	}
	
	public static GUIArea getArea() {
		return areas.get(area);
	}
}

