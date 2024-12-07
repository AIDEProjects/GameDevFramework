package com.goldsprite.gamedevframework;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class BufferUtils {
	public static FloatBuffer create(float[] data) {
		FloatBuffer buffer = ByteBuffer.allocateDirect(data.length * 4)
			. order(ByteOrder.nativeOrder())
			. asFloatBuffer();
		buffer.put(data).position(0);
		return buffer;
	}
}


