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

    public void drawStrokeRect(float cx, float cy, float width, float height, float lineWidth) {
		StrokeRectangle().draw(
			camera.VpMatrix(), 
			toNdcX(cx), 
			toNdcY(cy), 
			toNdcX(width), 
			toNdcY(height), 
			lineWidth, 
			currentColor);
	}
    public void drawRect(float cx, float cy, float width, float height) {
		Rectangle().draw(
			camera.VpMatrix(), 
			toNdcX(cx), 
			toNdcY(cy), 
			toNdcX(width), 
			toNdcY(height), 
			currentColor);
	}

    public void drawStrokeCircle(float cx, float cy, float radius, float lineWidth) {
		StrokeCircle().draw(
			camera.VpMatrix(), 
			toNdcX(cx), 
			toNdcY(cy), 
			toNdcX(radius), 
			lineWidth, 
			currentColor);
	}

    public void drawCircle(float cx, float cy, float radius) {
		Circle().draw(
			camera.VpMatrix(), 
			toNdcX(cx), 
			toNdcY(cy), 
			toNdcX(radius), 
			currentColor);
	}


	public float toNdcX(float width) {
		//归一化
		width /= camera.getViewportSize().x;
		//映射到-1~+1
		width *= 2;
		width -= width / 2;
		return width;
	}
	public float toNdcY(float height) {
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

