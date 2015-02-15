package com.cmmobi.looklook.info.profile;

import java.io.File;

import android.os.Environment;

public class MediaValue {
	/**
	 *  所属于的UID
	 * */
	public String UID;
	
	/**
	 *  存储的位置，相对于存储根目录变量的路径，例如/mnt/sdcard/abc/123，则path为/adb/123
	 * */
	public String localpath;
	
	/**
	 *  文件在服务器上的路径 http开头
	 * */
	public String url;
	
	/**
	 *  文件总大小
	 * */
	public long  totalSize; 
	
	/**
	 *  文件实际大小
	 * */
	public long  realSize; 
	
	/** 
	 * 文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * */
	public int MediaType; 
	
	/** 
	 *  文件属于  0：未定义， 1：浏览数据 2：收藏数据 3：自拍数据 4：私信数据
	 * */
	public int Belong;
	
	/**
	 *  是否同步到服务器  0：未定义， 1：是  2：不是
	 * */
	public int Sync;
	
	/**
	 *  已同步字节数， 当Sync为2时，才有意义
	 * */
	public long SyncSize;
	
	/**
	 *  同步方向， 0：未定义， 1：下载  2：上传。  当Sync为2时才有意义
	 * */
	public int Direction;
	
	public String toString(){
		return localpath;
	}
	
	public static boolean checkMediaAvailable(MediaValue mv, int type){
		if(mv == null || mv.UID==null ||mv.localpath==null || mv.totalSize<=0|| mv.realSize < mv.totalSize || mv.MediaType!=type){
			return false;
		}
		
		File file = new File(Environment.getExternalStorageDirectory() + mv.localpath);

		return file.exists() && file.isFile() && (file.length()==mv.realSize);

	}
	
	
	
	public static boolean checkMediaAvailable(MediaValue mv){
		if(mv == null || mv.UID==null ||mv.localpath==null || mv.totalSize<=0|| mv.realSize < mv.totalSize){
			return false;
		}
		
		File file = new File(Environment.getExternalStorageDirectory() + mv.localpath);

		return file.exists() && file.isFile() && (file.length()==mv.realSize);

	}

}
