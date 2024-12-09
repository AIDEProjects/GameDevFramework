package com.goldsprite.gamedevframework;

import android.opengl.*;

public class MatrixUtils {
	public static float[] createModelMatrix(float[] modelMatrix, float[] vpMatrix, float x, float y, float sclX, float sclY) {
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, x, y, 0);
		Matrix.scaleM(modelMatrix, 0, sclX, sclY, 1);
		Matrix.multiplyMM(modelMatrix, 0, vpMatrix, 0, modelMatrix, 0);
		return modelMatrix;
	}
}
	

