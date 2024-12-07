package com.goldsprite.gamedevframework;

public class Color
{
    // 基本颜色定义
    public static float[] black = new float[]{0.0f, 0.0f, 0.0f, 1.0f}; // 黑色
    public static float[] white = new float[]{1.0f, 1.0f, 1.0f, 1.0f}; // 白色
    public static float[] red = new float[]{1.0f, 0.0f, 0.0f, 1.0f}; // 红色
    public static float[] deepRed = new float[]{0.8f, 0.0f, 0.0f, 1.0f}; // 深红色
    public static float[] green = new float[]{0.0f, 1.0f, 0.0f, 1.0f}; // 绿色
    public static float[] deepGreen = new float[]{0.0f, 0.5f, 0.0f, 1.0f}; // 深绿色
    public static float[] blue = new float[]{0.0f, 0.0f, 1.0f, 1.0f}; // 蓝色
    public static float[] deepBlue = new float[]{0.0f, 0.4f, 0.8f, 1.0f}; // 深蓝色

    // 其他常见颜色定义
    public static float[] yellow = new float[]{1.0f, 1.0f, 0.0f, 1.0f}; // 黄色
    public static float[] cyan = new float[]{0.0f, 1.0f, 1.0f, 1.0f}; // 青色
    public static float[] magenta = new float[]{1.0f, 0.0f, 1.0f, 1.0f}; // 品红
    public static float[] orange = new float[]{1.0f, 0.5f, 0.0f, 1.0f}; // 橙色
    public static float[] purple = new float[]{0.5f, 0.0f, 0.5f, 1.0f}; // 紫色
    public static float[] pink = new float[]{1.0f, 0.75f, 0.8f, 1.0f}; // 粉色
    public static float[] gray = new float[]{0.5f, 0.5f, 0.5f, 1.0f}; // 灰色
    public static float[] brown = new float[]{0.65f, 0.16f, 0.16f, 1.0f}; // 棕色
    public static float[] lightGray = new float[]{0.83f, 0.83f, 0.83f, 1.0f}; // 浅灰色
    public static float[] darkGray = new float[]{0.25f, 0.25f, 0.25f, 1.0f}; // 深灰色

    // RGBA 颜色生成方法，可以通过自定义值创建任意颜色
    public static float[] rgba(float r, float g, float b, float a) {
        return new float[]{r, g, b, a}; // 返回 RGBA 数组
    }
}

