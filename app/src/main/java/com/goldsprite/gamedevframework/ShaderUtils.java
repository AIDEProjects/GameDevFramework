package com.goldsprite.gamedevframework;

import android.opengl.*;

public class ShaderUtils {

	public enum ShaderMode { Color, Texture }

    // 颜色模式的顶点和片段着色器代码
    private static final String COLOR_MODE_VERTEX_SHADER = ""
	+ "uniform mat4 u_Matrix;"
	+ "attribute vec4 a_Position;" 
	+ "void main() {" 
	+ "  gl_Position = u_Matrix * a_Position;" 
	+ "}";

    private static final String COLOR_MODE_FRAGMENT_SHADER = ""
	+ "precision mediump float;" 
	+ "uniform vec4 u_Color;" 
	+ "void main() {" 
	+ "  gl_FragColor = u_Color;" 
	+ "}";

    // 纹理模式的顶点和片段着色器代码
    private static final String TEXTURE_MODE_VERTEX_SHADER = ""
	+ "uniform mat4 u_Matrix;"
	+ "attribute vec4 a_Position;" 
	+ "attribute vec2 aTexCoord;" 
	+ "varying vec2 vTexCoord;" 
	+ "void main() {" 
	+ "  gl_Position = u_Matrix * a_Position;" 
	+ "  vTexCoord = aTexCoord;" 
	+ "}";

    private static final String TEXTURE_MODE_FRAGMENT_SHADER = ""
	+ "precision mediump float;" 
	+ "varying vec2 vTexCoord;" 
	+ "uniform sampler2D uTexture;" 
	+ "void main() {" 
	+ "  vec4 texColor = texture2D(uTexture, vTexCoord);" 
	+ "  gl_FragColor = vec4(texColor.rgb, texColor.a);"
	+ "}";

    /**
     * 创建着色器程序，根据传入的模式（color或texture）选择不同的着色器
     * @param mode 渲染模式，可以是 "color" 或 "texture"
     * @return 编译并链接后的着色器程序
     */
    public static int createProgram(Enum mode) {
        int vertexShader;
        int fragmentShader;

        // 根据传入的模式选择不同的顶点和片段着色器代码
        if (ShaderMode.Color.equals(mode)) {
			vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, COLOR_MODE_VERTEX_SHADER);
			fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, COLOR_MODE_FRAGMENT_SHADER);
		}
		else if (ShaderMode.Texture.equals(mode)) {
			vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, TEXTURE_MODE_VERTEX_SHADER);
			fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, TEXTURE_MODE_FRAGMENT_SHADER);
		}
		else {
			throw new IllegalArgumentException("Unsupported mode: " + mode);
        }

        // 创建着色器程序并链接
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        // 检查程序链接是否成功
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(program);
            throw new RuntimeException("Error creating program: " + GLES20.glGetProgramInfoLog(program));
        }

        return program;
    }

    /**
     * 加载和编译着色器
     * @param type 着色器类型（顶点或片段）
     * @param shaderCode 着色器代码
     * @return 编译后的着色器
     */
    private static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        // 检查编译是否成功
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
        }

        return shader;
    }
}


