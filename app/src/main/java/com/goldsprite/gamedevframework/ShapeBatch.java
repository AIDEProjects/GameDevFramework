package com.goldsprite.gamedevframework;

import android.opengl.*;
import com.goldsprite.appdevframework.math.*;
import com.goldsprite.appdevframework.math.Axis.*;
import com.goldsprite.gamedevframework.app.*;
import java.nio.*;
import java.util.*;
import com.goldsprite.gamedevframework.ShapeBatch.*;

public class ShapeBatch {

	public enum DrawShapeType {
		StrokeRect(), Rect();
	}

	private final GLGameView glGameView;
	private final GLGameView.GLRenderer renderer;
	private final OrthoCamera camera;

	//预设形状
	private StrokeRectangle strokeRectangle;
	private Rectangle rectangle;
	private Circle circle;
	private StrokeCircle strokeCircle;
	private Line line;

	public final ShaderUtils.ShaderMode programMode = ShaderUtils.ShaderMode.Color;
	private float[] currentColor = Color.black.clone(); // 默认颜色为黑色
	private float[] alignCoord;
	private final int coordDimen = 2;
	private float lineWidth = 200;

	//着色器相关
	private int program;
	private int vertexHandle;
	private int colorHandle;
	private int matrixHandle;

	public ShapeBatch(GLGameView glGameView) {
		//初始化引用
		this.glGameView = glGameView;
		this.renderer = glGameView.Renderer();
		this.camera = renderer.Camera();

		//初始化形状数据
		strokeRectangle = new StrokeRectangle();
		rectangle = new Rectangle();
		circle = new Circle();
		strokeCircle = new StrokeCircle();
		line = new Line();

		//配置着色器
		program = ShaderUtils.getProgram(ShaderUtils.ShaderMode.Color);
		vertexHandle = GLES20.glGetAttribLocation(program, ShaderUtils.a_Position);
		colorHandle = GLES20.glGetUniformLocation(program, ShaderUtils.u_Color);
		matrixHandle = GLES20.glGetUniformLocation(program, ShaderUtils.u_Matrix);
	}

	public void begin() {
		//开始时使用颜色模式着色器程序并启用属性
		GLES20.glUseProgram(program);
		GLES20.glEnableVertexAttribArray(vertexHandle);
	}

	public void end() {
		GLES20.glUseProgram(0);
		GLES20.glDisableVertexAttribArray(vertexHandle);
	}

	public float[] setColor(float[] color) {
		System.arraycopy(color, 0, currentColor, 0, 4);
		//应用颜色
		GLES20.glUniform4fv(colorHandle, 1, currentColor, 0);
		return currentColor;
	}

	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}

	public void drawStrokeRect(float x, float y, float width, float height, Align align) {
		drawShape(strokeRectangle, x, y, width, height, align);
	}

	public void drawRect(float x, float y, float width, float height, Align align) {
		drawShape(rectangle, x, y, width, height, align);
	}

	public void drawCircle(float x, float y, float radius, Align align) {
		drawShape(circle, x, y, radius, radius, align);
	}

	public void drawStrokeCircle(float x, float y, float radius, Align align) {
		drawShape(strokeCircle, x, y, radius, radius, align);
	}

	public void drawDottedLineCircle(float x, float y, float radius, Align align) {
		strokeCircle.dottedLine = true;
		drawShape(strokeCircle, x, y, radius, radius, align);
		strokeCircle.dottedLine = false;
	}
	
	public void drawLine(float x1, float y1, float x2, float y2) {
        line.setLine(x1, y1, x2, y2); // 设置线段的两个端点
        drawShape(line, 0, 0, 1, 1, Axis.Align.LeftDown); // 使用默认参数绘制
    }

	private void drawShape(Shape shape, float x, float y, float width, float height, Axis.Align align) {//转换锚点坐标
		//转换到锚点坐标
		alignCoord = camera.getAlignCoord(align, x, y, width, height);
		x = alignCoord[0];y = alignCoord[1];
		//获取引用
		FloatBuffer vertexBuffer = shape.vertexBuffer;
		float[] modelMatrix = shape.updateMatrix(x, y, width, height);
		//应用顶点指针
		GLES20.glVertexAttribPointer(vertexHandle, coordDimen, GLES20.GL_FLOAT, false, 0, vertexBuffer);
		//应用矩阵
		GLES20.glUniformMatrix4fv(matrixHandle, 1, false, modelMatrix, 0);
		//绘制
		shape.draw();
	}

	public abstract class Shape {
		public final int coordDimen = 2;
		public float[] vertices;
		public int VertexCount() { return vertices.length / coordDimen; }
		public FloatBuffer vertexBuffer;
		public ShaderUtils.ShaderMode programMode;
		public float x, y, width, height;
		public float[] modelMatrix = new float[16];

		public abstract void draw();
		public abstract float[] initVertices();
		public Axis.Align getVerticesAxis() { return Axis.Align.LeftDown; }

		public Shape() {
			vertices = newVerticesWithAxis(initVertices(), camera.Axis());
			vertexBuffer = BufferUtils.create(vertices);
			updateMatrix(0, 0, 1, 1);
		}

		//从源顶点数据创建目标锚点系的顶点数据
		private float[] newVerticesWithAxis(float[] originVertices, Align align) {
			float[] vertices = new float[originVertices.length];
			for (int i=0;i < vertices.length;i += coordDimen) {
				float x=originVertices[i];
				float y=originVertices[i + 1];
				float width=1,height=1;
				float[] coord = Axis.getAlignCoord(
					getVerticesAxis(), align, 
					x, y, width, height
				);
				vertices[i] = coord[0];
				vertices[i + 1] = coord[1];
			}
			return vertices;
		}

		//返回模型矩阵，如果有变化则更新
		public float[] updateMatrix(float x, float y, float width, float height) {
			if (isModified(x, y, width, height)) {
				MatrixUtils.createModelMatrix(modelMatrix, camera.VpMatrix(), x, y, width, height);
			}
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

	public class StrokeRectangle extends Shape {
		//LeftDown坐标系: 左下，右下，右上，左上
		public static final float[] strokeRectVertices = { 0, 0,  1, 0,  1, 1,  0, 1 };
		public float[] initVertices() { return strokeRectVertices; }
		public void draw() {
			GLES20.glLineWidth(lineWidth);
			GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, VertexCount());
		}
	}

	public class Rectangle extends Shape {
		//LeftDown坐标系: 左下，右下，左上，右上
		public static final float[] rectVertices = { 0, 0,  1, 0,  0, 1,  1, 1 };
		public float[] initVertices() { return rectVertices; }
		public void draw() {
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VertexCount());
		}
	}

	public class Circle extends Shape {
		// 多边形扇形边界：将圆拆分为多个固定的线段
		private final int SEGMENT_STEP = 3; // 单段角度，越小越精密
		public Axis.Align getVerticesAxis() { return Axis.Align.Center; }
		public float[] initVertices() {
			// 计算低多边形顶点坐标
			int numVertices = 360 / SEGMENT_STEP + 2; // 顶点数 = 分段数 + 中心点
			float[] lowPolyCoords = new float[numVertices * coordDimen];
			lowPolyCoords[0] = 0f; // 中心点x
			lowPolyCoords[1] = 0f; // 中心点y
			int index = coordDimen;
			for (int angle = 0; angle <= 360; angle += SEGMENT_STEP) {
				lowPolyCoords[index++] = (float) Math.cos(Math.toRadians(angle));  // x
				lowPolyCoords[index++] = (float) Math.sin(Math.toRadians(angle));  // y
			}
			return lowPolyCoords;
		}
		public void draw() {
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, VertexCount());
		}
	}

	public class StrokeCircle extends Circle {
		private boolean dottedLine = false;
		public void draw() {
			GLES20.glLineWidth(lineWidth);
			int drawType = dottedLine ?GLES20.GL_LINES : GLES20.GL_LINE_LOOP;
			GLES20.glDrawArrays(drawType, 0, VertexCount());
		}
	}
	
	public class Line extends Shape {
		private float[] lineVertices = new float[4]; // 只需要存储两点的 x, y

		public Line() {
			// 初始化顶点缓冲
			vertices = lineVertices;
			vertexBuffer = BufferUtils.create(vertices);
		}

		@Override
		public float[] initVertices() {
			// 默认线段起点 (0, 0)，终点 (1, 1)
			return new float[] {0, 0, 1, 1};
		}

		@Override
		public void draw() {
			GLES20.glLineWidth(lineWidth); // 使用批次中的线宽
			GLES20.glDrawArrays(GLES20.GL_LINES, 0, VertexCount());
		}

		public void setLine(float x1, float y1, float x2, float y2) {
			// 更新线段顶点
			lineVertices[0] = x1;
			lineVertices[1] = y1;
			lineVertices[2] = x2;
			lineVertices[3] = y2;
			vertexBuffer.put(lineVertices).position(0); // 更新顶点缓冲
		}
	}

}

