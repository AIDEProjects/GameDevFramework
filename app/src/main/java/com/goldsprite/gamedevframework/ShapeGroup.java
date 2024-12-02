package com.goldsprite.gamedevframework;

public class ShapeGroup {
    // 属性：中心位置、原始长宽、缩放因子
    private float centerX;
    private float centerY;
    private float width;
    private float height;
    private float scale;
	public float circleSize = 1f * 20;

    // 构造函数
    public ShapeGroup(float centerX, float centerY, float width, float height, float circleSize, float scale) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
        this.scale = scale;
		this.circleSize = circleSize;
    }

    // 设置位移
    public void translate(float dx, float dy) {
        this.centerX += dx;
        this.centerY += dy;
    }

    // 设置缩放
    public void scale(float scaleFactor) {
        this.scale *= scaleFactor;
    }
	//获取缩放
	public float getScale(){
		return this.scale;
	}

    // 绘制方法
    /*public void draw(float[] vpMatrix, Circle circle, Rectangle rectangle, float lineWidth, float[] circleColor, float[] rectColor) {
        // 绘制圆圈
        circle.draw(vpMatrix, centerX, centerY, scale * 0.01f, lineWidth, circleColor);
        // 绘制矩形
        rectangle.draw(vpMatrix, centerX, centerY, scale * width, scale * height, lineWidth, rectColor);
    }*/
	public void draw(float[] vpMatrix, StrokeCircle circle, StrokeRectangle rectangle, float lineWidth, 
					 float[] circleColor, float[] rectColor, boolean circleFixedSize) {
		// 绘制矩形，应用缩放
		float rectScale = scale;
        rectangle.draw(vpMatrix, centerX, centerY, rectScale * width, rectScale * height, lineWidth, rectColor);

		// 绘制中心圆，根据参数决定是否应用缩放
		float circleScale = circleFixedSize ? 1.0f : scale;
		circle.draw(vpMatrix, centerX, centerY, circleScale*circleSize, lineWidth * circleScale, circleColor);
	}
	

    // 静态方法：连接两组图形中心的线段
    public static void connectCenters(float[] vpMatrix, Line line, ShapeGroup group1, ShapeGroup group2, float lineWidth, float[] color) {
        line.draw(vpMatrix, group1.centerX, group1.centerY, group2.centerX, group2.centerY, lineWidth, color);
    }
	

	public float getCenterX() {
        return centerX;
    }
	public float getCenterY() {
        return centerY;
    }
    // 获取当前中心点（便于调试或扩展）
    public float[] getCenter() {
        return new float[]{centerX, centerY};
    }

    // 设置新的中心点
    public void setCenter(float x, float y) {
        this.centerX = x;
        this.centerY = y;
    }
}

