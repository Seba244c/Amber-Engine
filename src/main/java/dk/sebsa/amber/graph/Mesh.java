package dk.sebsa.amber.graph;

import static org.lwjgl.opengl.GL11.glTexCoord2f;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Mesh {
	private int v_id;
	private int u_id;
	private int vao;
	
	public Mesh(float[] verticies, float[] uvs) {
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		v_id = GL30.glGenBuffers();
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, v_id);
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER, createBuffer(verticies), GL30.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, verticies.length/3, GL30.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		u_id = GL30.glGenBuffers();
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, u_id);
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER, createBuffer(uvs), GL30.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
	
	public void render() {
		GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 6);
	}
	
	public void bind() {
		GL30.glBindVertexArray(vao);
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, v_id);
		GL30.glVertexAttribPointer(0, 2, GL30.GL_FLOAT, false, 0, 0);
		
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, u_id);
		GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 0, 0);
	}
	
	public void unbind() {
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
		GL30.glDisableVertexAttribArray(0);
		GL30.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	public FloatBuffer createBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	public void cleanup() {
		GL30.glDeleteVertexArrays(vao);
		GL15.glDeleteBuffers(v_id);
		GL15.glDeleteBuffers(u_id);
	}
}
