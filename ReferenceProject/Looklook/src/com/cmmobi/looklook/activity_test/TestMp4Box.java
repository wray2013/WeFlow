package com.cmmobi.looklook.activity_test;

import android.util.Log;
import effect.XMp4Box;

public class TestMp4Box {
	private static final String TAG = "ZC_TestMp4Box";

	private XMp4Box mMp4Box;
	
	public TestMp4Box() {
		Log.i(TAG, "setUp");
		mMp4Box = new XMp4Box();
	}
	
	public void testFileSplit(){
		String inputFile = "/mnt/sdcard/videos/test0001.mp4";
		String outputFile = "/mnt/sdcard/videos/test0001_split.mp4";
		mMp4Box.splitFile(inputFile,outputFile,0f,2f*60f);//1s
		String outputFile2 = "/mnt/sdcard/videos/test0001_split2.mp4";
		mMp4Box.splitFile(inputFile,outputFile2,0f,3f*60f);//1s
	}
	
	public void testFileCat(){
		mMp4Box = new effect.XMp4Box();
//		String outputFilePath = "/sdcard/videos/cat.mp4";
//		String catFile = "/sdcard/videos/output_320_240.mp4";
//		String catFile2 = "/sdcard/videos/output_taijong.mp4";

		String outputFilePath = "/sdcard/videos/cat2.mp4";
		String catFile = "/sdcard/videos/test0001_split.mp4";
		String catFile2 = "/sdcard/videos/test0001_split2.mp4";
		
		if(!mMp4Box.isAppendOpen()){
			mMp4Box.appendOpen(outputFilePath);
		}
		if(mMp4Box.isAppendOpen()){
			mMp4Box.appendFile(catFile);
			mMp4Box.appendFile(catFile2);
		}
		mMp4Box.appendClose();
	}
	
	/*
	private void splitFile(){
		mMp4Box = new effect.Mp4Box();
//		String inputFile = "/sdcard/videos/zhiqingchun01_320_240.mp4";
////		String outputFile = "/sdcard/videos/output_320_240.mp4";
//		String inputFile = "/sdcard/videos/taijong.mp4";
//		String outputFile = "/sdcard/videos/output_taijong.mp4";
		String inputFile = "/sdcard/videos/buzhui.mp3";
		String outputFile = "/sdcard/videos/output_buzhui.mp3";
		mMp4Box.native_splitFile(inputFile,outputFile,0.1f,3.2f);
	}
	
	private void catFile(){
		mMp4Box = new effect.Mp4Box();
//		String outputFilePath = "/sdcard/videos/cat.mp4";
//		String catFile = "/sdcard/videos/output_320_240.mp4";
//		String catFile2 = "/sdcard/videos/output_taijong.mp4";

		String outputFilePath = "/sdcard/videos/cat2.mp4";
		String catFile = "/sdcard/videos/output_320_240.mp4";
		String catFile2 = "/sdcard/videos/zhiqingchun01_320_240.mp4";
		
		mMp4Box.native_appendOpen(outputFilePath);
		mMp4Box.native_appendFile(catFile);
		mMp4Box.native_appendFile(catFile2);
		mMp4Box.native_appendClose();
	}*/
	
}
