package com.goldsprite.gamedevframework;

import android.opengl.*;
import com.goldsprite.gamedevframework.*;
import java.nio.*;

public class Circle
{
	// 多边形扇形边界：将圆拆分为多个固定的线段
	private final int SEGMENT_STEP = 1; // 每段10°，边界更少，生成低多边形
	private final float[] lowPolyCoords;
	private float currentAngle = 360;

	private FloatBuffer vertexBuffer;

	private static int program;

	
	public Circle() {
		// 计算低多边形顶点坐标
		int numVertices = 360 / SEGMENT_STEP + 2; // 顶点数 = 分段数 + 中心点
		lowPolyCoords = new float[numVertices * 3];
		lowPolyCoords[0] = 0f; // 中心点x
		lowPolyCoords[1] = 0f; // 中心点y
		lowPolyCoords[2] = 0f; // 中心点z

		int index = 3;
		for (int angle = 0; angle <= 360; angle += SEGMENT_STEP) {
			lowPolyCoords[index++] = (float) Math.cos(Math.toRadians(angle));  // x
			lowPolyCoords[index++] = (float) Math.sin(Math.toRadians(angle));  // y
			lowPolyCoords[index++] = 0f;									   // z
		}

		// 初始化顶点缓冲区
		vertexBuffer = BufferUtils.create(lowPolyCoords);

		program = ShaderUtils.createProgram(ShaderUtils.ShaderMode.Color);
	}

	public void draw(float[] vpMatrix, float cx, float cy, float radius, float[] color) {
		GLES20.glUseProgram(program);

		int positionHandle = GLES20.glGetAttribLocation(program, "a_Position");
		int matrixHandle = GLES20.glGetUniformLocation(program, "u_Matrix");
		int colorHandle = GLES20.glGetUniformLocation(program, "u_Color");

		vertexBuffer.position(0);
		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);
		GLES20.glEnableVertexAttribArray(positionHandle);

		float[] modelMatrix = new float[16];
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, cx, cy, 0);
		Matrix.scaleM(modelMatrix, 0, radius, radius, 1);
		Matrix.multiplyMM(modelMatrix, 0, vpMatrix, 0, modelMatrix, 0);

		GLES20.glUniformMatrix4fv(matrixHandle, 1, false, modelMatrix, 0);
		GLES20.glUniform4fv(colorHandle, 1, color, 0);

		int maxVertices = (int) (currentAngle / SEGMENT_STEP)+3; // 当前绘制的顶点数
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, maxVertices);
		GLES20.glDisableVertexAttribArray(positionHandle);
		
	}

}
