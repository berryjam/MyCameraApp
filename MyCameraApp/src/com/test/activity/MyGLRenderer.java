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
package com.test.activity;

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

	// 由包围盒计算得到放缩矩阵
	private float[] scaleMatrix = new float[16];

	private float mAngle;
	private String modelPath;

	private float minX;
	private float maxX;
	private float minY;
	private float maxY;
	private float minZ;
	private float maxZ;
	private boolean initialized = false;

	private float color1[] = { 1.0f, 0.0f, 0.0f, 1.0f, };
	private float color2[] = { 0.0f, 1.0f, 0.0f, 1.0f, };
	private float color3[] = { 0.0f, 0.0f, 1.0f, 1.0f, };
	private float color4[] = { 1.0f, 1.0f, 0.0f, 1.0f, };
	private float color5[] = { 0.0f, 1.0f, 1.0f, 1.0f, };
	private float color6[] = { 1.0f, 0.0f, 1.0f, 1.0f, };

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {

		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// mTriangle = new Triangle();
		// mSquare = new Square();

		try {
			parseSTL();
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

		// Set the camera position (View matrix)
		int rmOffset = 0;
		float eyeX = 3;
		float eyeY = 3;
		float eyeZ = 3;
		float centerX = 0;
		float centerY = 0;
		float centerZ = 0;
		float upX = 0;
		float upY = 1;
		float upZ = 0;
		Matrix.setLookAtM(mViewMatrix, rmOffset, eyeX, eyeY, eyeZ, centerX,
				centerY, centerZ, upX, upY, upZ);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
		calculateScaleMatrix();
		float[] scratchTmp = new float[16];
		Matrix.multiplyMM(scratchTmp, 0, scaleMatrix, 0, mMVPMatrix, 0);
		// Draw square
		// mSquare.draw(mMVPMatrix);

		// Create a rotation for the triangle

		// Use the following code to generate constant rotation.
		// Leave this code out when using TouchEvents.
		// long time = SystemClock.uptimeMillis() % 4000L;
		// float angle = 0.090f * ((int) time);

		// long time = SystemClock.uptimeMillis() % 10000L;
		// float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
		// mAngle = angleInDegrees;
		Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);

		// Combine the rotation matrix with the projection and camera view
		// Note that the mMVPMatrix factor *must be first* in order
		// for the matrix multiplication product to be correct.
		Matrix.multiplyMM(scratch, 0, scratchTmp, 0, mRotationMatrix, 0);

		// Draw triangle
		// mTriangle.draw(scratch);

		// Draw triangles
		int tmp = 0;
		for (Triangle triangle : triangles) {
			tmp = (tmp + 1) % 12;
			if (tmp == 1 || tmp == 2)
				triangle.setColor(color1);
			if (tmp == 3 || tmp == 4)
				triangle.setColor(color2);
			if (tmp == 5 || tmp == 6)
				triangle.setColor(color3);
			if (tmp == 7 || tmp == 8)
				triangle.setColor(color4);
			if (tmp == 9 || tmp == 10)
				triangle.setColor(color5);
			if (tmp == 11 || tmp == 0)
				triangle.setColor(color6);
			triangle.draw(scratch);
		}
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

	private void calculateScaleMatrix() {
		float tmpX = Math.abs(maxX) > Math.abs(minX) ? Math.abs(maxX) : Math
				.abs(minX);
		float tmpY = Math.abs(maxY) > Math.abs(minY) ? Math.abs(maxY) : Math
				.abs(minY);
		float tmpZ = Math.abs(maxZ) > Math.abs(minZ) ? Math.abs(maxZ) : Math
				.abs(minZ);
		float tmp = tmpX;
		if (tmpY > tmp)
			tmp = tmpY;
		if (tmpZ > tmp)
			tmp = tmpZ;
		float sx = 1;
		float sy = 1;
		float sz = 1;
		if (tmp > 1) {
			sx = 1 / tmp;
			sy = 1 / tmp;
			sz = 1 / tmp;
		}
		scaleMatrix = new float[] { sx, 0, 0, 0, 0, sy, 0, 0, 0, 0, sz, 0, 0,
				0, 0, 1 };
	}

	private void parseSTL() throws IOException, Exception {
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
		float current;
		while ((line = input.readLine().trim()) != null) {
			if (line.startsWith("solid"))
				continue;
			if (line.startsWith("facet")) {
				// TODO 处理法向量信息 line: facet normal
				String[] normalCoords = line.trim().split(" ");
				float[] normal = new float[3];
				normal[0] = Float.parseFloat(normalCoords[2]);
				normal[1] = Float.parseFloat(normalCoords[3]);
				normal[2] = Float.parseFloat(normalCoords[4]);
				line = input.readLine();// line:outer loop (skip)
				// 开始解析三角形顶点坐标
				float[] coords = new float[9];
				while ((line = input.readLine().trim()).startsWith("vertex")) {
					String[] vertex = line.split(" ");
					for (int i = count, j = 1; i < count + 3; ++i, ++j) {
						current = Float.parseFloat(vertex[j]);
						if (!initialized) {
							coords[i] = current;
							if (j % 3 == 1) {
								minX = current;
								maxX = current;
							}
							if (j % 3 == 2) {
								minY = current;
								maxY = current;
							}
							if (j % 3 == 0) {
								minZ = current;
								maxZ = current;
							}
							initialized = true;
						} else {
							coords[i] = current;
							if (j % 3 == 1) {
								if (minX > current) {
									minX = current;
								}
								if (maxX < current) {
									maxX = current;
								}
							}
							if (j % 3 == 2) {
								if (minY > current) {
									minY = current;
								}
								if (maxY < current) {
									maxY = current;
								}
							}
							if (j % 3 == 0) {
								if (minZ > current) {
									minZ = current;
								}
								if (maxZ < current) {
									maxZ = current;
								}
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
}