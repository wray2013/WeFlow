package com.cmmobi.looklook.offlinetask;

import java.io.File;

public class VideoCoverUploaderInfo{
	public String mFilePath;
	public File mfile;
	public String ip;
	public int port;
	public int rotation;
	public String filetype;
	public String businesstype;
	public String diaryid = "";
	public String attachmentid = "";
	public int height = 0;
	public int width = 0;
	public int porcess;//上传进度
	public int status;//上传状态 0-未上传 1-上传中 2-上传完成
}
