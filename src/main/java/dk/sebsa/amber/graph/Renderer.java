package dk.sebsa.amber.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

import dk.sebsa.amber.entity.components.SpriteRenderer;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Matrix4x4;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber_engine.Main;

public class Renderer {
	private static Map<Material, List<SpriteRenderer>> batch = new HashMap<Material, List<SpriteRenderer>>();
	private static Mesh mesh;
	private static Matrix4x4 projection;
	private static FBO fbo;
	private static Matrix4x4 ortho;
	
	public static void init() {
		float[] square = new float[] {
				0, 1, 1, 1, 1, 0,
				1, 0, 0, 0, 0, 1
		};
		float[] uv = new float[] {
				0, 0, 1, 0, 1, 1,
				1, 1, 0, 1, 0, 0
		};
		mesh = new Mesh(square, uv);
		
		updateFBO(Main.window.getWidth(), Main.window.getHeight());
	}
	
	public static void cleanup() {
		mesh.cleanup();
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
		glClearColor(0, 1, 1, 1);
		glClear(GL_COLOR_BUFFER_BIT);
		
		float w = Main.window.getWidth();
		float h = Main.window.getHeight();
		float halfW = w * 0.5f;
		float halfH = h * 0.5f;
		
		projection = Matrix4x4.ortho(-halfW, halfW, halfH, -halfH, -1, 1);
		
		mesh.bind();
		
		for(Material material : batch.keySet()) {
			material.bind();
			material.shader.setUniform("projection", projection);
			material.shader.setMatColor(material.color);
			
			List<SpriteRenderer> renderers = batch.get(material);
			for(SpriteRenderer renderer : renderers) {
				renderer.setUniforms();
				mesh.render();
			}
			
			material.unbind();
		}
		
		mesh.unbind();
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
		ortho = Matrix4x4.ortho(0, Main.window.getWidth(), Main.window.getHeight(), 0, -1, 1);
		Main.engineShader.setUniform("projection", ortho);
		mesh.bind();
	}
	
	public static void unprepare() {
		Main.engineShader.unbind();
		mesh.unbind();
	}
	
	public static void drawTextureWithTextCoords(Texture tex, Rect r, Rect u) {
		glDisable(GL_DEPTH_TEST);
		tex.bind();
		
		Main.engineShader.setUniform("offset", u.x, u.y, u.width, u.height);
		Main.engineShader.setUniform("pixelScale", r.width, r.height);
		Main.engineShader.setUniform("screenPos", r.x, r.y);
		Main.engineShader.setMatColor(Color.white());
		
		mesh.render();
		tex.unbind();
	}
	
	public static void updateFBO(int width, int height) {
		fbo = new FBO(width, height);
	}
}

