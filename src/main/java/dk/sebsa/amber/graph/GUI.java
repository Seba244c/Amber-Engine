package dk.sebsa.amber.graph;

import java.util.Map;
import java.util.function.Consumer;

import dk.sebsa.amber.graph.text.Font;
import dk.sebsa.amber.graph.text.Glyph;
import dk.sebsa.amber.io.Input;
import dk.sebsa.amber.math.Color;
import dk.sebsa.amber.math.Rect;

public class GUI {
	public enum Press {
		pressed,
		down,
		realesed,
		not
	}
	
	private static char[] c;
	public static Font font;
	private static float tempX;
	private static int i;
	
	public static String sheet = "BlackGUI";
	public static Color textColor = Color.white();
	
	private static Input input;
	
	public static void init(Input inp) {
		font = new Font(new java.awt.Font("TimesRoman", java.awt.Font.PLAIN, 16));
		input = inp;
	}
	
	public static void label(String text, float x, float y) {
		label(text, x, y, textColor);
	}
	
	public static void label(String text, float x, float y, Color color) {
		Map<Character, Glyph> chars = font.getChars();
		
		c = text.toCharArray();
		tempX = x;
		for(i = 0; i < c.length; i++) {
			Glyph glyph = chars.get(c[i]);
			
			Renderer.drawTextureWithTextCoords(font.getTexture(), new Rect(tempX, y, glyph.scale.x, glyph.scale.y), new Rect(glyph.position.x, glyph.position.y, glyph.size.x, glyph.size.y), color);

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
	
	public static boolean button(String text, Rect r, String normalStyle, String hoverStyle, Press type) {
		return button(text, r, Sprite.getSprite(sheet+"."+normalStyle), Sprite.getSprite(sheet+"."+hoverStyle), type);
	}
	
	public static boolean button(String text, Rect r, Sprite normalStyle, Sprite hoverStyle, Press type) {
		Press t = button(text, r, normalStyle, hoverStyle);
		if(t.equals(type)) {
			return true;
		}
		return false;
	}
	
	private static Press button(String text, Rect r, Sprite normalStyle, Sprite hoverStyle) {
		Rect rf = r.copy();
		Rect a = Renderer.area;
		rf.addPosition(a);
		
		if(rf.inRect(input.getMousePosition())) {
			Rect p = box(r, hoverStyle);
			if(p!=null)
				label(text, p.x, p.y);
			else
				label(text, r.x, r.y);
			
			if(input.isButtonPressed(0)) return Press.pressed;
			else if(input.isButtonDown(0)) return Press.down;
			else if(input.isButtonReleased(0)) return Press.realesed;
		}
		else {
			Rect p = box(r, normalStyle);
			if(p!=null)
				label(text, p.x, p.y);
			else
				label(text, r.x, r.y);
		}
		return Press.not;
	}
}
