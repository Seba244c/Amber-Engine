package dk.sebsa.amber.graph;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class FBO {
	private int frameBufferID;
	private int depthBufferID;
	private Texture texture;
	private int width;
	private int height;
	
	protected FBO(int width, int height) {
		this.width = width;
		this.height = height;
		frameBufferID = createFrameBuffer();
		depthBufferID = createDepthBufferAttachment();
		
		int textureID = createTextureAttachment();
		texture = new Texture(textureID, width, height);
		unBind();
	}
	
	public final Texture getTexture() { return texture; }
	
	public void bindFrameBuffer() {
		GL11.glBindTexture(GL_TEXTURE_2D, 0);
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBufferID);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBufferID);
	}
	
	public void unBind() {
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	private int createTextureAttachment() {
		int texture = GL11.glGenTextures();
		
		GL11.glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, texture, 0);
		
		return texture;
	}
	
	private int createDepthBufferAttachment() {
		int buffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, buffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,buffer);
		return buffer;
	}

	private int createFrameBuffer() {
		int buffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		return buffer;
	}
	
	public void cleanup() {
		GL30.glDeleteFramebuffers(frameBufferID);
		Texture.cleanup();
	}
}

