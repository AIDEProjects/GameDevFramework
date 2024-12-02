package com.goldsprite.gamedevframework;
import android.opengl.*;
import com.goldsprite.appdevframework.math.*;

public class OrthoCamera
{
	
	private final float[] projectionMatrix = new float[16];
	private final float[] viewMatrix = new float[16];
	private final float[] vpMatrix = new float[16];

	public float[] VpMatrix() { return vpMatrix; }

	private float scale = 1.0f;
	private Vector2 translate = new Vector2().set(0f);
	private Vector2 sclTranslateOffset = new Vector2();

	private Vector2 viewportSize = new Vector2();
	public Vector2 setViewportSize(float width, float height) { return viewportSize.set(width, height); }
	public Vector2 getViewportSize() { return viewportSize; }

	public Vector2 SclTranslate() {
		sclTranslateOffset.set(translate.clone().scl(scale * 2.5f));
		return sclTranslateOffset;
	}
	

	public OrthoCamera(){}
	public OrthoCamera(float width, float height){ setViewportSize(width, height); }

	
	public void updateMatrix() {
		Matrix.setIdentityM(viewMatrix, 0);
		Matrix.translateM(viewMatrix, 0, translate.x + SclTranslate().x, translate.y + SclTranslate().y, 0);
		Matrix.scaleM(viewMatrix, 0, scale, scale, 1);
		Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
	}
	
	public void updateViewport(int width, int height) {
		setViewportSize(width, width);
		float ratio = (float) width / height;
		Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1, 1, -1, 1);
	}
	

	public void translate(float dx, float dy) {
		float scl = Math.max(1, scale);
		translate.add(dx / scl, dy / scl);
	}

	public void scale(float factor) {
		scale *= factor;
	}
	
}
