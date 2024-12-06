package com.goldsprite.gamedevframework;

import android.opengl.*;

public class StrokeCircle
 {
	private final float[] vertices;
	private final int vertexCount;
	private final int program;

	public StrokeCircle() {
		int segments = 100;
		vertices = new float[(segments + 2) * 3];
		vertices[0] = vertices[1] = vertices[2] = 0;
		for (int i = 0; i <= segments; i++) {
			float theta = (float) (2 * Math.PI * i / segments);
			vertices[(i + 1) * 3] = (float) Math.cos(theta);
			vertices[(i + 1) * 3 + 1] = (float) Math.sin(theta);
		}
		vertexCount = vertices.length / 3;

		program = ShaderUtils.createProgram(ShaderUtils.ShaderMode.Color);
	}

	public void draw(float[] vpMatrix, float cx, float cy, float radius, float lineWidth, float[] color) {
		GLES20.glUseProgram(program);

		int positionHandle = GLES20.glGetAttribLocation(program, "a_Position");
		int matrixHandle = GLES20.glGetUniformLocation(program, "u_Matrix");
		int colorHandle = GLES20.glGetUniformLocation(program, "u_Color");

		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, BufferUtils.create(vertices));
		GLES20.glEnableVertexAttribArray(positionHandle);

		float[] modelMatrix = new float[16];
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, cx, cy, 0);
		Matrix.scaleM(modelMatrix, 0, radius, radius, 1);
		Matrix.multiplyMM(modelMatrix, 0, vpMatrix, 0, modelMatrix, 0);

		GLES20.glUniformMatrix4fv(matrixHandle, 1, false, modelMatrix, 0);
		GLES20.glUniform4fv(colorHandle, 1, color, 0);

		GLES20.glLineWidth(lineWidth);
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 1, vertexCount - 1);
	}
}

