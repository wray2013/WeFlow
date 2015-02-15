package com.cmmobi.looklook.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;
/**
 *  @author zhangwei 
 */
public class DumpString {
	private static String TAG = "DumpString";
	private static String FILENAME = "DumpString.txt";

	public static void dump(String s) {
		// Create the file reference
		File dataFile = new File(Environment.getExternalStorageDirectory(),
				FILENAME);

		Log.e(TAG, "write dataFile to " + dataFile.getPath());

		// Check if external storage is usable
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.e(TAG, "find no external storage");
			return;
		}

		// Create a new file and write some data
		try {
			FileOutputStream mOutput = new FileOutputStream(dataFile, false);
			String data = s;// "THIS DATA WRITTEN TO A FILE";
			mOutput.write(data.getBytes());
			mOutput.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String read(String path){
		String display = null;
		//Read the created file and display to the screen
		try {
			File dataFile = new File(path);
			FileInputStream mInput = new FileInputStream(dataFile);
			long contentLength = dataFile.length();
			byte[] data = new byte[(int)contentLength];
			
			mInput.read(data);
			mInput.close();
			display = new String(data);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return display;
	}
}