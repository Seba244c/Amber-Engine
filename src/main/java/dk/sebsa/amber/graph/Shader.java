package dk.sebsa.amber.graph;

import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.lwjgl.BufferUtils;

import dk.sebsa.amber.Asset;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Matrix4x4;
import dk.sebsa.amber.math.Vector2f;

public class Shader extends Asset {	
	private int program;
	private int vs;
	private int fs;
	
	private Color boundColor = Color.transparent();
	
	//Static variables to handle all of our shaders
	private static List<Shader> shaders = new ArrayList<Shader>();
	private static int i;
	
	public Shader(String fileName) throws IOException {		
		// Program
		program = glCreateProgram();
		if (program == 0)
            throw new IllegalStateException("Could not create Shader");
        
		String[] shader = createShader(fileName);
		
		// Creates and stores the vertex shader. Then compiles it and checks for errors
		vs = glCreateShader(GL_VERTEX_SHADER);
		if (vs == 0)
            throw new IllegalStateException("Error creating shader. Type: Vertex Shader");
        
		glShaderSource(vs, shader[0]);
		glCompileShader(vs);
		
		if(glGetShaderi(vs, GL_COMPILE_STATUS) != 1) 
			throw new IOException("Error compiling Shader code: " + glGetShaderInfoLog(vs, 1024));
		
		// Fragment
		fs = glCreateShader(GL_FRAGMENT_SHADER);
		if (fs == 0)
            throw new IllegalStateException("Error creating shader. Type: Fragment Shader");
        
		glShaderSource(fs, shader[1]);
		glCompileShader(fs);			
		if(glGetShaderi(fs, GL_COMPILE_STATUS) != 1) 
			throw new IOException("Error compiling Shader code: " + glGetShaderInfoLog(fs, 1024));
		
		// Attach shaders to program
		glAttachShader(program, vs);
		glAttachShader(program, fs);
		
		// Bind attrib
		glBindAttribLocation(program, 0, "verticies");
		glBindAttribLocation(program, 1, "uv");
		
		// Link, validate and check program
		glLinkProgram(program);
		
		if (glGetProgrami(program, GL_LINK_STATUS) == 0) {
            throw new UnknownError("Error linking Shader code: " + glGetProgramInfoLog(program, 1024));
        }
		
		glValidateProgram(program);
		if (glGetProgrami(program, GL_VALIDATE_STATUS) == 0) {
        	System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(program, 1024));
        }
		
		shaders.add(this);
	}
	
	private String[] createShader(String fileName) {
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader;
		try {
			if(fileName.startsWith("/")) {
				bufferedReader = new BufferedReader(new InputStreamReader(Shader.class.getResourceAsStream("/shaders" + fileName + ".glsl")));
				name = fileName.replaceFirst("/", "");
			} else {
				bufferedReader = new BufferedReader(new FileReader(new File(fileName + ".glsl")));
				String[] split = fileName.replaceAll(Pattern.quote("\\"), "\\\\").split("\\\\");
				name = split[split.length - 1];
			}
			
			String line;
			while((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append("\n");
			}
			
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuilder.toString().split("ENDVERTEX");
	}
	
	public void setUniform(String name, Color c) {
		int location = glGetUniformLocation(program, name);
		if(location != -1) glUniform4f(location, c.r, c.g, c.b, c.a);
	}
	
	public void setUniform(String name, Matrix4x4 matrix) {
		int location = glGetUniformLocation(program, name);
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		matrix.getBuffer(buffer);
		
		if(location != -1) glUniformMatrix4fv(location, false, buffer);
		buffer.flip();
	}
	
	public void setMatColor(Color c) {
		if(!boundColor.Compare(c)) {
			setUniform("matColor", c);
			boundColor = c;
		}
	}
	
	public void setUniform(String name, float x, float y) {
		int location = glGetUniformLocation(program, name);
		if(location != -1) glUniform2f(location, x, y);
	}
	
	public void setUniform(String name, Vector2f v) {
		setUniform(name, v.x, v.y); 
	}
	
	public void setUniform(String name, float x, float y, float z, float w) {
		int location = glGetUniformLocation(program, name);
		if(location != -1) glUniform4f(location, x, y, z, w);
	}
	
	public void bind() {
		glUseProgram(program);
	}
	
	public void unbind() {
		glUseProgram(0);
	}
	
	public void cleanup() {
        unbind();
        if (program != 0) {
            glDeleteProgram(program);
        }
    }
	
	public static Shader findShader(String name) {
		for(i = 0; i < shaders.size(); i++ ) {
			if(shaders.get(i).name.equals(name)) return shaders.get(i);
		}
		return null;
	}

	public static List<Shader> getShaders() {
		return shaders;
	}
}
