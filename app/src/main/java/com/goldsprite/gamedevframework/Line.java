package com.goldsprite.gamedevframework;

import android.opengl.*;

public class Line {
    private final float[] vertices = new float[6]; // 存储两个点的坐标
    private final int program;

    public Line() {
        program = ShaderUtils.createProgram();
    }

    public void draw(float[] vpMatrix, float x1, float y1, float x2, float y2, float lineWidth, float[] color) {
        vertices[0] = x1;
        vertices[1] = y1;
        vertices[2] = 0.0f;
        vertices[3] = x2;
        vertices[4] = y2;
        vertices[5] = 0.0f;

        GLES20.glUseProgram(program);

        int positionHandle = GLES20.glGetAttribLocation(program, "a_Position");
        int matrixHandle = GLES20.glGetUniformLocation(program, "u_Matrix");
        int colorHandle = GLES20.glGetUniformLocation(program, "u_Color");

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, BufferUtils.create(vertices));
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, vpMatrix, 0);
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        GLES20.glLineWidth(lineWidth);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);
    }
}

