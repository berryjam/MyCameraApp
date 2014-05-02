/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.camera.control.renderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.camera.model.Triangle;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class must
 * override the OpenGL ES drawing lifecycle methods:
 * <ul>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = "MyGLRenderer";
	// private Triangle mTriangle;
	private List<Triangle> triangles;
	// private Square mSquare;

	// mMVPMatrix is an abbreviation for "Model View Projection Matrix"
	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];
	private final float[] mRotationMatrix = new float[16];

	private float[] scaleMatrix = new float[16];

	private float mAngle;
	private String modelPath;

	private float scaleRatio = 1.0f;

	private float minX = Float.MAX_VALUE;
	private float maxX = Float.MIN_VALUE;
	private float minY = Float.MAX_VALUE;
	private float maxY = Float.MIN_VALUE;
	private float minZ = Float.MAX_VALUE;
	private float maxZ = Float.MIN_VALUE;
	private boolean initialized = false;

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {

		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// mTriangle = new Triangle();
		// mSquare = new Square();

		try {
			parse();
			scaleMatrix = getScaleMatrix();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		float[] scratch = new float[16];

		// Draw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		int rmOffset = 0;
		float eyeX = 3;
		float eyeY = 3;
		float eyeZ = -3;
		float centerX = 0;
		float centerY = 0;
		float centerZ = 0;
		float upX = 0;
		float upY = 1;
		float upZ = 0;
		// Set the camera position (View matrix)
		Matrix.setLookAtM(mViewMatrix, rmOffset, eyeX, eyeY, eyeZ, centerX,
				centerY, centerZ, upX, upY, upZ);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

		// Draw square
		// mSquare.draw(mMVPMatrix);

		// Create a rotation for the triangle

		// Use the following code to generate constant rotation.
		// Leave this code out when using TouchEvents.
		// long time = SystemClock.uptimeMillis() % 4000L;
		// float angle = 0.090f * ((int) time);

		Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);

		// Combine the rotation matrix with the projection and camera view
		// Note that the mMVPMatrix factor *must be first* in order
		// for the matrix multiplication product to be correct.
		Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

		Matrix.scaleM(scratch, 0, scaleRatio, scaleRatio, scaleRatio);

		// Draw triangle
		// mTriangle.draw(scratch);

		// Draw triangles
		for (Triangle triangle : triangles)
			triangle.draw(scratch);
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		// Adjust the viewport based on geometry changes,
		// such as screen rotation
		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) width / height;

		// this projection matrix is applied to object coordinates
		// in the onDrawFrame() method
		Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

	}

	/**
	 * Utility method for compiling a OpenGL shader.
	 * 
	 * <p>
	 * <strong>Note:</strong> When developing shaders, use the checkGlError()
	 * method to debug shader coding errors.
	 * </p>
	 * 
	 * @param type
	 *            - Vertex or fragment shader type.
	 * @param shaderCode
	 *            - String containing the shader code.
	 * @return - Returns an id for the shader.
	 */
	public static int loadShader(int type, String shaderCode) {

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

	/**
	 * Utility method for debugging OpenGL calls. Provide the name of the call
	 * just after making it:
	 * 
	 * <pre>
	 * mColorHandle = GLES20.glGetUniformLocation(mProgram, &quot;vColor&quot;);
	 * MyGLRenderer.checkGlError(&quot;glGetUniformLocation&quot;);
	 * </pre>
	 * 
	 * If the operation is not successful, the check throws an error.
	 * 
	 * @param glOperation
	 *            - Name of the OpenGL call to check.
	 */
	public static void checkGlError(String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}

	/**
	 * Returns the rotation angle of the triangle shape (mTriangle).
	 * 
	 * @return - A float representing the rotation angle.
	 */
	public float getAngle() {
		return mAngle;
	}

	/**
	 * Sets the rotation angle of the triangle shape (mTriangle).
	 */
	public void setAngle(float angle) {
		mAngle = angle;
	}

	public void setModelPath(String path) {
		modelPath = path;
	}

	private void parse() throws IOException, Exception {
		triangles = new ArrayList<Triangle>();

		File file = new File(modelPath);
		if (!file.exists()) {
			if (!file.mkdirs())
				throw new Exception("file can't be created");
		}
		BufferedReader input = new BufferedReader(new FileReader(modelPath));
		String line = null;
		Triangle triangle = null;

		int count = 0;
		float currentVal;
		while ((line = input.readLine().trim()) != null) {
			if (line.startsWith("solid"))
				continue;
			if (line.startsWith("facet")) {
				// TODO 处理法向量信息 line: facet normal
				line = input.readLine();// line:outer loop (skip)
				// 开始解析三角形顶点坐标
				float[] coords = new float[9];
				while ((line = input.readLine().trim()).startsWith("vertex")) {
					String[] vertex = line.split(" ");
					for (int i = count, j = 1; i < count + 3; ++i, ++j) {
						currentVal = Float.parseFloat(vertex[j]);
						coords[i] = currentVal;
						if (!initialized) {
							if (j % 3 == 1) {
								minX = currentVal;
								maxX = currentVal;
							}
							if (j % 3 == 2) {
								minY = currentVal;
								maxY = currentVal;
							}
							if (j % 3 == 0) {
								minZ = currentVal;
								maxZ = currentVal;
							}
						} else {
							if (j % 3 == 1) {
								if (minX > currentVal)
									minX = currentVal;
								if (maxX < currentVal)
									maxX = currentVal;
							}
							if (j % 3 == 2) {
								if (minY > currentVal)
									minY = currentVal;
								if (maxY < currentVal)
									maxY = currentVal;
							}
							if (j % 3 == 0) {
								if (minZ > currentVal)
									minZ = currentVal;
								if (maxZ < currentVal)
									maxZ = currentVal;
							}
						}
					}
					count = (count + 3) % 9;
				}
				triangle = new Triangle(coords);
				triangles.add(triangle);
			}
			if (line.startsWith("endloop") || line.startsWith("endfacet"))
				continue;
		}
		input.close();
	}

	private float[] getScaleMatrix() {
		float[] scaleMatrix;
		float sx = 1.0f, sy = 1.0f, sz = 1.0f;
		// TODO 计算
		float absX = Math.abs(minX) > Math.abs(maxX) ? Math.abs(minX) : Math
				.abs(maxX);
		float absY = Math.abs(minY) > Math.abs(maxY) ? Math.abs(minY) : Math
				.abs(maxY);
		float absZ = Math.abs(minZ) > Math.abs(maxZ) ? Math.abs(minZ) : Math
				.abs(maxZ);
		float abs = absX;
		if (absY > abs)
			abs = absY;
		if (absZ > abs)
			abs = absZ;
		if (abs > 1) {
			float ratio = 1 / abs;
			sx = ratio;
			sy = ratio;
			sz = ratio;
		}
		scaleMatrix = new float[] {// 4*4矩阵
		// 1
				sx, 0, 0, 0,
				// 2
				0, sy, 0, 0,
				// 3
				0, 0, sz, 0,
				// 4
				0, 0, 0, 1 };
		return scaleMatrix;
	}
}