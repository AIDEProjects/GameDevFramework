package com.goldsprite.gamedevframework;

import android.opengl.*;
import java.nio.*;
import com.goldsprite.appdevframework.math.*;
import com.goldsprite.appdevframework.log.*;

public class ShapeBatch
{
    private final GLGameView glGameView;
    private final GLGameView.GLRenderer renderer;
    private final OrthoCamera camera;
    private float[] currentColor = Color.black.clone(); // 默认颜色为黑色

	private StrokeCircle strokeCircle;
	public StrokeCircle StrokeCircle() { return strokeCircle = strokeCircle == null ?new StrokeCircle() : strokeCircle; }
	private Circle circle;
	public Circle Circle() { return circle = circle == null ?new Circle() : circle; }

	private StrokeRectangle strokeRectangle;
	public StrokeRectangle StrokeRectangle() { return strokeRectangle = strokeRectangle == null ?new StrokeRectangle() : strokeRectangle; }
	private Rectangle rectangle;
	public Rectangle Rectangle() { return rectangle = rectangle == null ?new Rectangle() : rectangle; }


    public ShapeBatch(GLGameView glGameView) {
        this.glGameView = glGameView;
		this.renderer = glGameView.Renderer();
		this.camera = renderer.Camera();
    }

    public float[] setColor(float[] color) {
        System.arraycopy(color, 0, currentColor, 0, 4);
		return currentColor;
    }

    public void drawStrokeRect(float x, float y, float width, float height, float lineWidth, Align align) {
		float[] alignRec = toAlignRec(align, x, y, width, height);
		StrokeRectangle().draw(
			camera.VpMatrix(), 
			alignRec[0], alignRec[1], 
			width, height, 
			lineWidth, 
			currentColor);
	}
    public void drawRect(float x, float y, float width, float height, Align align) {
		float[] alignRec = toAlignRec(align, x, y, width, height);
		Rectangle().draw(
			camera.VpMatrix(), 
			alignRec[0], alignRec[1], 
			width, height, 
			currentColor);
	}

    public void drawStrokeCircle(float x, float y, float radius, float lineWidth, Align align) {
		float diam = radius * 2;
		float[] alignRec = toAlignRec(align, x, y, diam, diam);
		StrokeCircle().draw(
			camera.VpMatrix(), 
			alignRec[0], alignRec[1], 
			radius, 
			lineWidth, 
			currentColor);
	}

    public void drawCircle(float x, float y, float radius, Align align) {
		float diam = radius * 2;
		float[] alignRec = toAlignRec(align, x, y, diam, diam);
		Circle().draw(
			camera.VpMatrix(), 
			alignRec[0], alignRec[1], 
			radius, 
			currentColor);
	}


	float[] tempAlginRec = new float[2];
	public float[] toAlignRec(Align align, float x, float y, float width, float height) {
		if (Align.LEFTDOWN.equals(align)) {
			tempAlginRec[0] = x + width / 1f;
			tempAlginRec[1] = y + height / 1f;
		}
		else if (Align.CENTER.equals(align)) {
			tempAlginRec[0] = x;
			tempAlginRec[1] = y;
		}
		return tempAlginRec;
	}

	public float toNdcX(float width) {
		if (true) return width;
		//归一化
		width /= camera.getViewportSize().x;
		//映射到-1~+1
		width *= 2;
		width -= width / 2;
		return width;
	}
	public float toNdcY(float height) {
		if (true) return height;
		//归一化
		height /= camera.getViewportSize().y;
		//映射到-1~+1
		height *= 2;
		height -= height / 2;
		//y轴↓转↑
		height *= -1;
		return height;
	}
	public void toNdc(Vector2 vec) {
		vec.set(toNdcX(vec.x), toNdcY(vec.y));
	}

}

