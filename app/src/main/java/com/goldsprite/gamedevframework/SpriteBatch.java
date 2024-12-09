package com.goldsprite.gamedevframework;

import android.graphics.drawable.shapes.*;
import android.opengl.*;
import com.goldsprite.appdevframework.math.*;
import com.goldsprite.gamedevframework.*;
import com.goldsprite.gamedevframework.app.*;
import java.nio.*;

public class SpriteBatch {
	//画布引用
	private final GLGameView glGameView;
	private final GLGameView.GLRenderer renderer;
	private final OrthoCamera camera;

	//程序数据相关
	public final ShaderUtils.ShaderMode programMode = ShaderUtils.ShaderMode.Texture;
	private float[] alignCoord;
	private final int coordDimen = 2;

	//着色器相关
	private int program;
	private int positionHandle;
	private int matrixHandle;
	private int texCoordHandle;
	private int textureHandle;

	public SpriteBatch(GLGameView glGameView) {
		//初始化引用
		this.glGameView = glGameView;
		this.renderer = glGameView.Renderer();
		this.camera = renderer.Camera();

		//获取程序句柄
		program = ShaderUtils.createProgram(programMode);
		positionHandle = GLES20.glGetAttribLocation(program, ShaderUtils.a_Position);
		matrixHandle = GLES20.glGetUniformLocation(program, ShaderUtils.u_Matrix);
		texCoordHandle = GLES20.glGetAttribLocation(program, ShaderUtils.a_TexCoord);
		textureHandle = GLES20.glGetUniformLocation(program, ShaderUtils.u_Texture);
		//GLDebug.checkGLError("获取程序句柄");
	}

	public void begin() {
		//开始时使用颜色模式着色器程序并启用属性
		GLES20.glUseProgram(program);
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glEnableVertexAttribArray(texCoordHandle);
	}

	public void end() {
		GLES20.glUseProgram(0);
		GLES20.glDisableVertexAttribArray(positionHandle);
		GLES20.glDisableVertexAttribArray(texCoordHandle);
	}
	
	public void drawTex(Texture tex, float x, float y, float width, float height, Axis.Align align) {//转换锚点坐标
		//转换到锚点坐标
		alignCoord = camera.getAlignCoord(align, x, y, width, height);
		x = alignCoord[0];y = alignCoord[1];
		
		//获取引用
		FloatBuffer vertexBuffer = tex.vertexBuffer;
		FloatBuffer texCoordBuffer = tex.texCoordBuffer;
		float[] modelMatrix = tex.updateMatrix(camera.VpMatrix(), x, y, width, height);

		//应用指针
		GLES20.glVertexAttribPointer(positionHandle, coordDimen, GLES20.GL_FLOAT, false, 0, vertexBuffer);
		GLES20.glVertexAttribPointer(texCoordHandle, coordDimen, GLES20.GL_FLOAT, false, 0, texCoordBuffer);

		//应用矩阵
		GLES20.glUniformMatrix4fv(matrixHandle, 1, false, modelMatrix, 0);

		// 绑定纹理
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.textureId);
		GLES20.glUniform1i(textureHandle, 0);
		//绘制
		tex.draw();
	}
}
