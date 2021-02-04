package dk.sebsa.amber.graph;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import dk.sebsa.amber.graph.text.Font;
import dk.sebsa.amber.graph.text.Glyph;
import dk.sebsa.amber.io.Input;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Rect;
import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber.util.Logger;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class GUI {
	public enum Press {
		pressed,
		down,
		realesed,
		not
	}
	
	private static char[] c;
	public static Font defaultFont;
	private static float tempX;
	private static int i;
	
	public static String sheet = "BlackGUI";
	public static Color textColor = Color.white();
	
	private static Input input;
	
	private static Popup popup;
	
	public static void init(Input inp) {
		defaultFont = new Font(new java.awt.Font("OpenSans", java.awt.Font.PLAIN, 16));
		input = inp;
	}
	
	public static void label(String text, float x, float y) {
		label(text, x, y, textColor, defaultFont);
	}
	
	public static void label(String text, float x, float y, Color color, Font font) {
		Map<Character, Glyph> chars = font.getChars();
		
		c = text.toCharArray();
		tempX = x;
		for(i = 0; i < c.length; i++) {
			Glyph glyph = chars.get(c[i]);
			
			Renderer.drawTextureWithTextCoords(font.getTexture(),new Rect(tempX, y, glyph.scale.x, glyph.scale.y), new Rect(glyph.position.x, glyph.position.y, glyph.size.x, glyph.size.y), color);

			tempX += glyph.scale.x;
		}
		Texture.resetBound();
	}
	
	public static void window(Rect r, String title, Consumer<Rect> f, String style) {
		window(r, title, f, Sprite.getSprite(sheet+"."+style));
	}
	
	public static void window(Rect r, String title, Consumer<Rect> f, Sprite style) {
		Rect center = r;
		if(style != null) {
			center = box(r, style);
			label(title, r.x + style.padding.x, r.y+4);
			Renderer.beginArea(center);
		}
		else {
			label(title, r.x, r.y);
			Renderer.beginArea(r);
		}
		
		f.accept(center);
		Renderer.endArea();
	}

	public static boolean toggle(boolean b, float x, float y, Sprite on, Sprite off, int layer) {
		Sprite s = off;
		if(b)
			s = on;
		
		if(GUI.button("", new Rect(x, y, 15, 15), s, s, Press.realesed, false, layer))
			return !b;
		return b;
	}
	
	public static Rect box(Rect r, String style) {
		return box(r, Sprite.getSprite(sheet+"."+style));
	}
	
	public static Rect box(Rect r, Sprite e) {
		//If there is no style, return null because it needs a style to return the center of it
		if(e == null) return null;
		
		//Cache a short variable for the texture, just so we only have to type a character anytime we use it
		Texture t = e.material.texture;
		Rect uv = e.getUV();
		
		//Get the top left corner of the box using corresponding padding values and draw it using a texture drawing method
		Rect tl = new Rect(r.x, r.y, e.padding.x, e.padding.y);
		Rect tlu = new Rect(uv.x, uv.y, e.getPaddingUV().x, e.getPaddingUV().y);
		Renderer.drawTextureWithTextCoords(t, tl, tlu);
		
		//Get the top right corner of the box using corresponding padding values and draw it using a texture drawing method
		Rect tr = new Rect((r.x + r.width) - e.padding.width, r.y, e.padding.width, e.padding.y);
		Rect tru = new Rect((uv.x + uv.width) - e.getPaddingUV().width, uv.y, e.getPaddingUV().width, e.getPaddingUV().y);
		Renderer.drawTextureWithTextCoords(t, tr, tru);
		
		//Get the bottom left corner of the box using corresponding padding values and draw it using a texture drawing method
		Rect bl = new Rect(r.x, (r.y + r.height) - e.padding.height, e.padding.x, e.padding.height);
		Rect blu = new Rect(uv.x, (uv.y + uv.height) - e.getPaddingUV().height, e.getPaddingUV().x, e.getPaddingUV().height);
		Renderer.drawTextureWithTextCoords(t, bl, blu);
		
		//Get the bottom right corner of the box using corresponding padding values and draw it using a texture drawing method
		Rect br = new Rect(tr.x, bl.y, e.padding.width, e.padding.height);
		Rect bru = new Rect(tru.x, blu.y, e.getPaddingUV().width, e.getPaddingUV().height);
		Renderer.drawTextureWithTextCoords(t, br, bru);
		
		//Get the left side of the box using corresponding padding values and draw it using a texture drawing method
		Rect l = new Rect(r.x, r.y + e.padding.y, e.padding.x, r.height - (e.padding.y + e.padding.height));
		Rect lu = new Rect(uv.x, uv.y + e.getPaddingUV().y, e.getPaddingUV().x, uv.height - (e.getPaddingUV().y + e.getPaddingUV().height));
		Renderer.drawTextureWithTextCoords(t, l, lu);
		
		//Get the right side of the box using corresponding padding values and draw it using a texture drawing method
		Rect ri = new Rect(tr.x, r.y + e.padding.y, e.padding.width, l.height);
		Rect ru = new Rect(tru.x, lu.y, e.getPaddingUV().width, lu.height);
		Renderer.drawTextureWithTextCoords(t, ri, ru);
		
		//Get the top of the box using corresponding padding values and draw it using a texture drawing method
		Rect ti = new Rect(r.x + e.padding.x, r.y, r.width - (e.padding.x + e.padding.width), e.padding.y);
		Rect tu = new Rect(uv.x + e.getPaddingUV().x, uv.y, uv.width - (e.getPaddingUV().x + e.getPaddingUV().width), e.getPaddingUV().y);
		Renderer.drawTextureWithTextCoords(t, ti, tu);
		
		//Get the bottom of the box using corresponding padding values and draw it using a texture drawing method
		Rect b = new Rect(ti.x, bl.y, ti.width, e.padding.height);
		Rect bu = new Rect(tu.x, blu.y, tu.width, e.getPaddingUV().height);
		Renderer.drawTextureWithTextCoords(t, b, bu);
		
		//Get the center of the box using corresponding padding values and draw it using a texture drawing method
		Rect c = new Rect(ti.x, l.y, ti.width, l.height);
		Rect cu = new Rect(tu.x, lu.y, tu.width, lu.height);
		Renderer.drawTextureWithTextCoords(t, c, cu);
		
		//Return the center rectangle
		return c;
	}
	
	public static boolean button(String text, Rect r, String normalStyle, String hoverStyle, Press type, boolean center, int layer) {
		return button(text, r, Sprite.getSprite(sheet+"."+normalStyle), Sprite.getSprite(sheet+"."+hoverStyle), type, center, layer);
	}
	
	public static boolean button(String text, Rect r, Sprite normalStyle, Sprite hoverStyle, Press type, boolean center, int layer) {
		return button(text, r, normalStyle, hoverStyle, center, layer).equals(type);
	}
	
	private static Press button(String text, Rect r, Sprite normalStyle, Sprite hoverStyle, boolean center, int layer) {
		Rect rf = r.copy();
		GUIArea a = Renderer.getArea();
		rf.addPosition(a.rect);
		rf.y -= a.getScroll();
		
		float x = 0;
		float y = 0;
		if(center) {
			x = rf.x + ((rf.width / 2f) - ((float) defaultFont.getStringWidth(text) / 2f));
			y = rf.y + ((rf.height / 2f) - (defaultFont.getFontHeight() / 2f));
			if(y % 1 != 0) y += 0.5f;
			if(x % 1 != 0) x += 0.5f;
		}
		
		if(rf.inRect(input.getMousePosition(layer))) {
			Rect p = box(r, hoverStyle);
			if(center)
				label(text, x, y);
			else {
				if(p!=null)
					label(text, p.x, p.y);
				else
					label(text, r.x, r.y);
			}
			
			if(input.isButtonPressed(0)) return Press.pressed;
			else if(input.isButtonDown(0)) return Press.down;
			else if(input.isButtonReleased(0)) return Press.realesed;
		}
		else {
			Rect p = box(r, normalStyle);
			if(center)
				label(text, x, y);
			else {
				if(p!=null)
					label(text, p.x, p.y);
				else
					label(text, r.x, r.y);
			}
		}
		return Press.not;
	}
	
	public static Popup setPopup(Rect nameRect, List<String> list, Consumer<String> func) {
		popup = new Popup(nameRect, list, func, input);
		return popup;
	}
	
	public static void drawPopup() {
		if(popup != null)  {
			popup = popup.draw();
		}
	}
	
	public static boolean hasPopup() {
		return popup != null;
	}
	
	public static void removePopup() {
		popup = null;
	}
	
	public static String textField(Rect r, String name, String v, float padding, int layer) {
		label(name, r.x, r.y);
		
		// This is temp has to change
		if(button(v, new Rect(r.x + padding, r.y, r.width - padding, r.height), "Box", "Box", Press.realesed, false, layer)) {
			String s = TinyFileDialogs.tinyfd_inputBox("Changing " + name + "!", "What would you like this varible to be?", v);
			if(s!=null)
				s = s.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "");
				if(s==null) return v;
				if(!s.equals("") && !s.startsWith(" "))
					return s;
				else
					Logger.errorLog("GUI", "textField", "Invalid string! A string cannot start with a space!");
		}
		return v;
	}
	
	public static float floatField(Rect r, String name, Float v, float padding, int layer) {
		String ret = GUI.textField(r, name, String.valueOf(v), padding, layer);
		float f = v;
		
		try { f = Float.parseFloat(ret); }
		catch (NumberFormatException e) { Logger.errorLog("GUI", "floaltField", "Float field input is inviliad!"); }
		return f;
	}
	
	public static Vector2f vectorField(Rect r, String name, Vector2f v, float padding, int layer) {
		label(name, r.x, r.y);
		
		float half = (r.width - padding) / 2.0f;
		float x = floatField(new Rect(r.x + padding - 5, r.y, half, r.height), "x", v.x, 10, layer);
		float y = floatField(new Rect(r.x + padding + half, r.y, half, r.height), "y", v.y, 10, layer);
		return new Vector2f(x, y);
	}

	public static Popup getPopup() {
		return popup;
	}
}
