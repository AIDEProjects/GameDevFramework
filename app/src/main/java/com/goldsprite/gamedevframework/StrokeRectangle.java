package com.goldsprite.gamedevframework;

import android.opengl.*;

public class StrokeRectangle
{
    private final float[] vertices = {
        -0.5f, -0.5f, 0.0f,
		0.5f, -0.5f, 0.0f,
		0.5f,  0.5f, 0.0f,
        -0.5f,  0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f // 重新闭合矩形
    };
    private final int vertexCount = vertices.length / 3;
    private final int program;

    public StrokeRectangle() {
		//vertices转-1~+1
		for (int i=0;i < vertices.length;i++) vertices[i] *= 2;
        program = ShaderUtils.createProgram();
    }

    public void draw(float[] vpMatrix, float cx, float cy, float width, float height, float lineWidth, float[] color) {
        GLES20.glUseProgram(program);

        int positionHandle = GLES20.glGetAttribLocation(program, "a_Position");
        int matrixHandle = GLES20.glGetUniformLocation(program, "u_Matrix");
        int colorHandle = GLES20.glGetUniformLocation(program, "u_Color");

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, BufferUtils.create(vertices));
        GLES20.glEnableVertexAttribArray(positionHandle);

        float[] modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, cx, cy, 0);
        Matrix.scaleM(modelMatrix, 0, width, height, 1);
        Matrix.multiplyMM(modelMatrix, 0, vpMatrix, 0, modelMatrix, 0);

        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, modelMatrix, 0);
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        GLES20.glLineWidth(lineWidth);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertexCount);
		GLES20.glDisableVertexAttribArray(positionHandle);
    }
}


