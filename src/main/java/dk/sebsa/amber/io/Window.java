package dk.sebsa.amber.io;

import dk.sebsa.amber.graph.Renderer;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber.util.Logger;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.opengl.GL11.*;

import java.nio.IntBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class Window {
	public long windowId;
	private int width;
    private int height;
    private int tempW;
    private int tempH;
    private boolean resized;
    private boolean isFullscreen;
    private String title;
    private int[] posX = new int[1];
    private int[] posY = new int[1];
	private boolean lineView;
	private boolean vSync;
	private boolean isCursorShown = true;
	private int frames;
	public static int fps;
	private static long time;
	private byte minimized;
	private static Rect r;
	
	
	// Vars used in Averege frame length calculation
	private static double averegeFrameTime;
	private static double aft;
	private static double afl;
	
	private boolean showFps;

	private GLFWImage.Buffer iconBuffer;
		
    public Window(String title, int width, int height, boolean vsync, boolean showFpsInTitleBar) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.resized = false;
        this.iconBuffer = null;
        this.vSync = vsync;
        this.showFps = showFpsInTitleBar;
    }
    
	public void init(Color clearColor) {
		Logger.infoLog("Window", "init", "Initializing Window");
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() ) {
			Logger.errorLog("Window", "init", "Unable to initialize GLFW");
			throw new IllegalStateException("[Window.java/init] Unable to initialize GLFW");
		}
		
		// Set Window Rect
		r = new Rect(0, 0, width, height);

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		
		// OSX Sipport
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		
		// Create the window
		windowId = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
		if ( windowId == NULL ) {
			Logger.errorLog("Window", "init", "Failed to create the GLFW window");
			throw new IllegalStateException("[Window.java/init] Failed to create the GLFW window");
		}
		// Setup resize callback
        glfwSetFramebufferSizeCallback(windowId, (window, width, height) -> {
        	if(width == 0 && height == 0) minimized = 1;
    		else minimized = 0;
            this.width = width;
            this.height = height;
            this.setResized(true);
            
            r.set(0, 0, width, height);
            
            glViewport(0, 0, width, height);
            Renderer.updateFBO(width, height);
        });
		
		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(windowId, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			posX[0] = (vidmode.width() - width) / 2;
			posY[0] = (vidmode.height() - height) / 2;
			// Center the window
			glfwSetWindowPos(windowId, posX[0], posY[0]);
		}
		// Make the OpenGL context current
		glfwMakeContextCurrent(windowId);
		// Enable v-sync
		if(vSync) {
			glfwSwapInterval(1);
		} else {
			glfwSwapInterval(0);
		}
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		Logger.infoLog("Window", "init", "Setting up OpenGL");
		GL.createCapabilities();
		
		// Culling
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		
		// Enable transparency
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		// Make the window visible
		glfwShowWindow(windowId);
		glClearColor(clearColor.r, clearColor.g, clearColor.b, 0.0f);
		
		// Input
		glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		
		// FPS counter
		frames = 0;
		fps = 0;
		time = System.currentTimeMillis();
		aft = System.currentTimeMillis();
		averegeFrameTime = 0;
		afl = 0;
	}
	
	public boolean isMinimized() {
		return minimized == 1;
	}
	
	public void cleanup() {
		Logger.infoLog("Window", "cleanup", "Destorying window");
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(windowId);
		glfwDestroyWindow(windowId);

		// Terminate GLFW and free the error callback
		glfwSetErrorCallback(null);
		glfwTerminate();
		
		glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		
		if(iconBuffer != null)
			iconBuffer.close();
	}
	
	public void update() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		frames++;
		
		// Time bewteen frames
		averegeFrameTime = averegeFrameTime + (System.currentTimeMillis() - aft);
		aft = System.currentTimeMillis();
		
		// Fps
		if (System.currentTimeMillis() > time + 1000) {
			if(showFps) {
				glfwSetWindowTitle(windowId, title + " | FPS: " + frames);
			}
			fps = frames;
			time = System.currentTimeMillis();
			frames = 0;
			
			afl = averegeFrameTime/fps/1000;
			averegeFrameTime = 0;
		}
	}
	
	public boolean shouldClose() {
		return glfwWindowShouldClose(windowId);
	}

	public void setTitle(String title) {
		Logger.infoLog("Window", "setTitle", "Setting title of the window to: " + title);
		glfwSetWindowTitle(windowId, title);
	}
	
	public void setClearColor(Color clearColor) {
		glClearColor(clearColor.r, clearColor.g, clearColor.b, 0.0f);
	}

	public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }
    
    public boolean isFullscreen() {
		return isFullscreen;
	}

    public void setFullscreen(boolean isFullscreen) {
    	Logger.infoLog("Window", "setFullscreen", "Setting fullscreen to: " + isFullscreen);
		this.isFullscreen = isFullscreen;
		resized = true;
		if (isFullscreen) {
			tempW = width;
			tempH = height;
			GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwGetWindowPos(windowId, posX, posY);
			glfwSetWindowMonitor(windowId, glfwGetPrimaryMonitor(), 0, 0, videoMode.width(), videoMode.height(), GLFW_DONT_CARE);
			// Enable v-sync
			if(vSync) {
				glfwSwapInterval(1);
			} else {
				glfwSwapInterval(0);
			}
		} else {
			glfwSetWindowMonitor(windowId, 0, posX[0], posY[0], tempW, tempH, GLFW_DONT_CARE);
		}
	}
    
    public void setLineView(boolean bool) {
    	Logger.infoLog("Window", "setLineView", "Setting line view to: " + bool);
    	if (bool) { glPolygonMode( GL_FRONT_AND_BACK, GL_LINE  ); } else { glPolygonMode( GL_FRONT_AND_BACK, GL_FILL ); }
    	lineView = bool;
    }

    public boolean isLineView() {
    	return lineView;
    }
    
    @Deprecated
    public void setIcon(String path) {
    	/*Logger.infoLog("Window", "setIcon", "Setting icon to file at: " + path);
    	IMG icon = FileUtils.loadImage(path);
    	GLFWImage iconImage = GLFWImage.malloc();
    	iconBuffer = GLFWImage.malloc(1); 
    	iconImage.set(icon.w, icon.h, icon.image);
    	iconBuffer.put(0, iconImage);
		glfwSetWindowIcon(windowId, iconBuffer);
		iconImage.free();*/
    }
    
    public void showCursor(boolean show) {
    	isCursorShown = show;
    	if(!show) {
    		glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    	} else {
    		glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    	}
    }
    
    public boolean isCursorShown() {
		return isCursorShown;
	}
    
    public boolean isVSync() {
    	return vSync;
    }
    
    public void setVSync(boolean vsync) {
    	vSync = vsync;
    	Logger.infoLog("Window", "setVSync", "Setting vsync to: " + vsync);
    	if(vSync) {
    		glfwSwapInterval(1);
    	} else {
    		glfwSwapInterval(0);
    	}
    }

	public int getFps() {
		return fps;
	}
	
	public void setShowFps(boolean showFps) {
		this.showFps = showFps;
	}
	
	public static double getAfl() {
		return afl;
	}
	
	public Rect getRect() {
		return r;
	}
}
