package com.camera.control.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.view.View;

public class ObjLoader {
	public View loadObj(String filePath) throws Exception {
		File file = new File(filePath);
		if (!file.exists())
			throw new Exception("The file path of selected object wasn't exist");
		BufferedReader input = new BufferedReader(new FileReader(filePath));
		String line = null;
		// TODO ½âÎöobjÎÄ¼þ
		return null;
	}
}
