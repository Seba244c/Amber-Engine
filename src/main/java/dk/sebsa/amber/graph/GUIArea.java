package dk.sebsa.amber.graph;

import dk.sebsa.amber.io.Input;
import dk.sebsa.amber.math.Mathf;
import dk.sebsa.amber.math.Rect;

public class GUIArea {
	public Rect rect;
	public int maxScroll;
	private int scroll = 0;
	
	public GUIArea(Rect area) {
		if(area.equals(null)) area = new Rect(0, 0, 0, 0);
		else rect = area;
	}

	public final int getScroll() {
		return scroll;
	}

	public final int scroll(int offset) {
		if(rect.inRect(Input.instance.getMousePosition())) {
			float leftOver = maxScroll - rect.height;
			if(leftOver <= 0) scroll = 0;
			else scroll = (int)Mathf.clamp(offset + (-Input.instance.getScrollY()*15), 0, leftOver);
		} else scroll = offset;
		return scroll;
	}
}
