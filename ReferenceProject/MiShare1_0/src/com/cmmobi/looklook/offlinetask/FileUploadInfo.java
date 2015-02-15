package com.cmmobi.looklook.offlinetask;


/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-8-26
 */
public class FileUploadInfo{
	public String id;
	public String uuid;
	public String diaryID;
	public String target_userids;//私信目标用户id
	public String ip;//连接IP
	public int port;//连接端口
	public String localFilePath;//本地上传文件路径
	public String uploadPath;//服务器上传路径
	public int porcess;//上传进度
	public int status;//上传状态 0-未上传 1-上传中 2-上传完成
}
