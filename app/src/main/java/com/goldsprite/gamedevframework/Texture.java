package com.goldsprite.gamedevframework;
import android.opengl.*;
import java.nio.*;
import com.goldsprite.appdevframework.math.Axis;
import com.goldsprite.appdevframework.math.Axis.Align;

public class Texture {
	public FloatBuffer vertexBuffer;
	public FloatBuffer texCoordBuffer;
	public int textureId;

	// 纹理的宽和高
	public float textureWidth;
	public float textureHeight;
	public float aspectRatio;

	public final int coordDimen = 2;
	public float[] vertices;
	public float[] texCoords;
	public int VertexCount() { return vertices.length / coordDimen; }
	public float x, y, width, height;
	public float[] modelMatrix = new float[16];

	public Texture(int[] texData) {
		init(texData);
	}

	private void init(int[] texData) {
		// 加载纹理
		textureId = texData[0];
		textureWidth = texData[1];
		textureHeight = texData[2];

		// 顶点坐标，确保它们的比例与纹理一致
		aspectRatio = textureWidth / textureHeight; 
		float width = 1 * aspectRatio;
		float height = 1;

		vertices = new float[]{
			0, 0, // Bottom Left
			1, 0, // Bottom Right
			0, 1, // Top Left

			0, 1, // Top Left
			1, 0, // Bottom Right
			1, 1, // Top Right
		};
		/*for (int i=0;i < vertices.length;i++)
		 vertices[i] *= i % 2 == 0 ? width : height;*/

		// 纹理坐标
		texCoords = new float[] {
			0.0f, 1.0f, // Bottom Left
			1.0f, 1.0f,  // Bottom Right
			0.0f, 0.0f, // Top Left

			0.0f, 0.0f, // Top Left
			1.0f, 1.0f,  // Bottom Right
			1.0f, 0.0f, // Top Right
		};

		vertexBuffer = BufferUtils.create(vertices);
		texCoordBuffer = BufferUtils.create(texCoords);
	}

	public void draw() {
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VertexCount());
	}

	//返回模型矩阵，如果有变化则更新
	public float[] updateMatrix(float[] vpMatrix, float x, float y, float width, float height) {
		//if (isModified(x, y, width, height)) {
			//this.x = x;this.y = y;this.width = width;this.height = height;
			MatrixUtils.createModelMatrix(modelMatrix, vpMatrix, x, y, width, height);
		//}
		return modelMatrix;
	}
	public boolean isModified(float x, float y, float width, float height) {
		if (this.x != x || this.y != y 
			|| this.width != width 
			|| this.height != height) {
			return true;
		}
		return false;
	}

}
