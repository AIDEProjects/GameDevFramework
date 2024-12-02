package com.goldsprite.gamedevframework;

import android.opengl.*;
import java.nio.*;

public class Rectangle
{
	private int mProgram; // 存储程序ID
	// 创建顶点坐标
	private float[] vertices = {
		-0.5f, -0.5f,  // 左下角
		0.5f, -0.5f,   // 右下角
		-0.5f,  0.5f,  // 左上角
		0.5f,  0.5f    // 右上角
	};

	public Rectangle() {
		//vertices转-1~+1
		//for (int i=0;i < vertices.length;i++) vertices[i] *= 2;
		mProgram = ShaderUtils.createProgram();
	}

    public void draw(float[] vpMatrix, float cx, float cy, float width, float height, float[] color) {
		GLES20.glUseProgram(mProgram); // 使用目标程序

		int positionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        int matrixHandle = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
        int colorHandle = GLES20.glGetUniformLocation(mProgram, "u_Color");

        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, BufferUtils.create(vertices));
        GLES20.glEnableVertexAttribArray(positionHandle);

        float[] modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, cx, cy, 0);
        Matrix.scaleM(modelMatrix, 0, width, height, 1);
        Matrix.multiplyMM(modelMatrix, 0, vpMatrix, 0, modelMatrix, 0);

        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, modelMatrix, 0);
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4); // 绘制四个顶点组成的图形
		GLES20.glDisableVertexAttribArray(positionHandle); // 禁用顶点属性
	}
	
}
