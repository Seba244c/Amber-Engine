package dk.sebsa.amber.graph.text;

import static org.lwjgl.opengl.GL11.*;

import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import dk.sebsa.amber.graph.Texture;
import dk.sebsa.amber.math.Vector2f;

public class Font {
	private String name;
	private int fontID;
	private BufferedImage bufferedImage;
	private Vector2f imageSize;
	private java.awt.Font font;
	private FontMetrics fontMetrics;
	private int i;
	private Texture texture;
	private float h;
	
	private static List<Font> fonts = new ArrayList<Font>();
	private Map<Character, Glyph> chars = new HashMap<Character, Glyph>();
	
	public Font(String name, float size) {		
		if(name.startsWith("/")) {
			try {
				font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, Font.class.getResourceAsStream("/font" + name + ".ttf")).deriveFont(size);
				name = name.replaceFirst("/", "");
			} catch (FontFormatException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
		} else {
			try {
				font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, new File(name + ".ttf")).deriveFont(size);
				String[] split = name.replaceAll(Pattern.quote("\\"), "\\\\").split("\\\\");
				name = split[split.length - 1];
			} catch (FontFormatException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
		}
		this.name = name;
		
		generateFont();
		fonts.add(this);
	}
	
	public Font(java.awt.Font font) {
		this.font = font;
		generateFont();
		this.name = font.getFontName();
		fonts.add(this);
	}
	
	private void generateFont() {
		GraphicsConfiguration graphCon = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Graphics2D graphics = graphCon.createCompatibleImage(1, 1, Transparency.TRANSLUCENT).createGraphics();
		graphics.setFont(font);		
		
		fontMetrics = graphics.getFontMetrics();
		h = (float) (fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent());
		imageSize = new Vector2f(2048, 2048);
		bufferedImage = graphics.getDeviceConfiguration().createCompatibleImage((int) imageSize.x, (int) imageSize.y, Transparency.TRANSLUCENT);
		
		fontID = glGenTextures();
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, fontID);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, (int)imageSize.x, (int)imageSize.y, 0, GL_RGBA, GL_UNSIGNED_BYTE, generateImage());
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		texture = new Texture(fontID, (int)imageSize.x, (int)imageSize.y);
	}
	
	private ByteBuffer generateImage() {
		Graphics2D graphics2d = (Graphics2D) bufferedImage.getGraphics();
		graphics2d.setFont(font);
		graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		drawCharaters(graphics2d);
		return createBuffer();
	}

	private ByteBuffer createBuffer() {
		int w = (int)imageSize.x;
		int h = (int)imageSize.y;
		int[] pixels = new int[w*h];
		
		bufferedImage.getRGB(0, 0, w, h, pixels, 0, w);
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(w * h * 4);
		
		for(i = 0; i < pixels.length; i++) {
			byteBuffer.put((byte) ((pixels[i] >> 16) & 0xFF)); 	// Red
			byteBuffer.put((byte) ((pixels[i] >> 8) & 0xFF)); 	// Green
			byteBuffer.put((byte) (pixels[i] >> 0xFF)); 		// Blue
			byteBuffer.put((byte) ((pixels[i] >> 24) & 0xFF)); 	// Alpha
		}
		byteBuffer.flip();
		return byteBuffer;
	}

	private void drawCharaters(Graphics2D graphics2d) {
		int tempX = 0;
		int tempY = 0;
		
		for(i=32; i < 256; i++) {
			if(i==127) continue;
			
			char c = (char) i;
			float charWidth = fontMetrics.charWidth(c);
			
			float advance = charWidth + 8;
			
			if(tempX + advance > imageSize.x) {
				tempX = 0;
				tempY += 1;
			}
			
			chars.put(c, new Glyph(new Vector2f(tempX / imageSize.x, (tempY * h) / imageSize.y), new Vector2f(charWidth / imageSize.x, h/imageSize.y), new Vector2f(charWidth, h)));
			graphics2d.drawString(String.valueOf(c), tempX, fontMetrics.getMaxAscent() + (h* tempY));
			tempX += advance;
		}
	}

	public int getFontID() {
		return fontID;
	}
	
	public Texture getTexture() {
		return texture;
	}

	public Map<Character, Glyph> getChars() {
		return chars;
	}
	
	public int getStringWidth(String s) {
		return fontMetrics.stringWidth(s);
	}
	
	public float getFontHeight() { return h; }
	
	public final String getName() {return name;}
	
	public static final List<Font> getFonts() {
		return fonts;
	}
	
	public static void cleanUPAll() {
		for(int i = 0; i < fonts.size(); i++) {
			glDeleteTextures(fonts.get(i).texture.getId());
		}
	}
	
	public void cleanUp() {
		texture.cleanup();
	}
}

