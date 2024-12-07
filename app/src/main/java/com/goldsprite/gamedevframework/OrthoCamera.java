package com.goldsprite.gamedevframework;
import android.opengl.*;
import com.goldsprite.appdevframework.math.*;
import com.goldsprite.appdevframework.log.*;
import com.goldsprite.gamedevframework.app.*;
import com.goldsprite.appdevframework.utils.*;
import com.goldsprite.appdevframework.math.Axis.*;

public class OrthoCamera {

	private final Align axis = Align.LeftDown;//默认全局坐标系为左下原点，不可更改
	public Align Axis() { return axis; }
	public Vector2 getAxisAlignVector(Align align) { return Axis.getAxisAlignVector(axis, align); }
	public Vector2 getAxisBound() { return Axis.getAxisBound(axis); }
	public float[] getAlignCoord(Align align, float x, float y, float width, float height) {
		return Axis.getAlignCoord(axis, align, x, y, width, height);
	}

	private final float[] projectionMatrix = new float[16];
	private final float[] viewMatrix = new float[16];
	private final float[] vpMatrix = new float[16];

	public float[] VpMatrix() { return vpMatrix; }

	private float scale = 1.0f;
	private Vector2 translate = new Vector2().set(0f);

	private Vector2 viewportSize = new Vector2();
	public Vector2 setViewportSize(float width, float height) { return viewportSize.set(width, height); }
	public Vector2 getViewportSize() { return viewportSize; }


	public OrthoCamera() {}
	public OrthoCamera(float width, float height) {
		setViewportSize(width, height); 
		Log.logT(GLGameView.TAG.LifeCycle, "OrthoCamera相机视口已初始化, viewportSize: %s", getViewportSize());
	}


	public void updateMatrix() {
		Matrix.setIdentityM(viewMatrix, 0);
		Matrix.translateM(viewMatrix, 0, translate.x, translate.y, 0);
		Matrix.scaleM(viewMatrix, 0, scale, scale, 1);
		Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
	}

	public void updateViewport(int width, int height) {
		setViewportSize(width, width);
		Vector2 axisBound = getAxisBound();
		float newXMin = width * axisBound.x;
		float newXMax = width * axisBound.y;
		float newYMin = height * axisBound.x;
		float newYMax = height * axisBound.y;
		Matrix.orthoM(
			projectionMatrix, 0,  
			newXMin, newXMax, 
			newYMin, newYMax, 
			- 1, 1);
		Log.logT(
			TAG.AxisAlignConvert, ""
			+ "更新视口宽高: "
			+ "\n\t" + "当前坐标系: " + StringUtils.getEnumFullName(axis)
			+ "\n\t" + "当前坐标系边界向量: " + axisBound
			+ "\n\t" + "新视口: " + String.format(
				"{xMin: %s, xMax: %s, yMin: %s, yMax: %s}", 
				MathUtils.preciNum(newXMin), 
				MathUtils.preciNum(newXMax), 
				MathUtils.preciNum(newYMin), 
				MathUtils.preciNum(newYMax)
			)
		);
	}


	public void translation(float x, float y) {
		translate.set(x, y);
	}

	public void setScale(float setScale) {
		scale = setScale;
	}

}
