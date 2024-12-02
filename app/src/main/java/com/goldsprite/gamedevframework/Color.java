package com.goldsprite.gamedevframework;

public class Color
{
	public static float[] black = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
	public static float[] red = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
	public static float[] deepRed = new float[]{0.8f, 0.0f, 0.0f, 1.0f};
	public static float[] green = new float[]{0.0f, 1.0f, 0.0f, 1.0f};
	public static float[] blue = new float[]{0.0f, 0.6f, 1.0f, 1.0f};
	public static float[] deepBlue = new float[]{0.0f, 0.4f, 0.8f, 1.0f};

	public static float[] rgba(float r, float g, float b, float a) { return new float[]{r, g, b, a}; }
}
