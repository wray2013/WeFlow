package com.cmmobi.looklook.offlinetask;

import android.util.Log;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.common.utils.Base64Utils;
import com.cmmobi.looklook.info.profile.ActiveAccount;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-8-26
 */
public class HeaderInfo {

	private static final String TAG="HeaderInfo";
	public long fileLength;//文件大小
	public String userID;
	public int nuid;//第一段文件值为0，依次递增
	public int over;//0 未完成 1完成
	public String fileName;//文件物理路径 年_月_日_时分秒&文件名
	public int type;//上传方式 1 单文件上传 n 边拍边传
	public int rotation;//视频旋转角度
	public int fileType;//文件类型 1 普清视频 2 高清视频 3 录音 4 图片 5语音描述
	public int businesstype;//业务类型 1 日记 2 评论3 私信 4 陌生人消息
	public String diaryid;//日记ID
	public String attachmentid;//附件ID
	public int isencrypt;//音频文件是否加密 0未加密 1已加密
	public String nickname;//用户昵称 base64编码

	/**
	 * 组装请求头;
	 * 
	 * @param file
	 *            : 要上传的文件;
	 * @param nuid
	 *            : 子文件序号 (从0开始);
	 * @param over
	 *            : 是否已经完成上传 (0: 未结束. 1: 已结束);
	 * @param fileName
	 *            : 文件物理路径;
	 * @param type
	 *            : 文件个数 (1: 只有一个文件. n: 有多个文件);
	 * @param rotation
	 *            : 旋转角度 (0/90/180/270);
	 * @param filetype
	 *            : 旋转角度 (0/90/180/270);
	 * @param businesstype
	 *            : 业务类型 (1 日记 2 评论3 私信 4 陌生人消息);
	 * @param diaryid
	 *            : 日记表id;
	 * @param attachmentid
	 *            : 附件表id(根据businesstype的不同对于不同表中的id);
	 * @param isencrypt
	 *            : 音频文件是否加密 (0未加密 1已加密);
	 * @return
	 */
	public String getHeader(){
		String string = "Content-Length="
				+ fileLength
				+ ";userid="
				+ ActiveAccount.getInstance(ZApplication.getInstance())
						.getLookLookID() + ";nuid=" + nuid + ";over=" + over
				+ ";filename=" + fileName + ";type=" + type + ";rotation="
				+ rotation + ";filetype=" + fileType + ";businesstype="
				+ businesstype + ";diaryid=" + diaryid + ";attachmentid="
				+ attachmentid + ";isencrypt=" + isencrypt + ";nickname=" 
				+ Base64Utils.encode(ActiveAccount.getInstance(ZApplication.getInstance()).nickname) +"\r\n";
		String head = String.format("%05d", string.length());
		Log.d(TAG, "getHeader->"+head+string);
		return head + string;
	}
	
	/**
	 * 获取文件上传完成通知
	 */
	public String getCompleteNofity(){
		String string = "Content-Length="
				+ fileLength
				+ ";userid="
				+ ActiveAccount.getInstance(ZApplication.getInstance())
						.getLookLookID() + ";nuid=" + nuid + ";over=" + 1
				+ ";filename=" + fileName + ";type=" + type + ";rotation="
				+ rotation + ";filetype=" + fileType + ";businesstype="
				+ businesstype + ";diaryid=" + diaryid + ";attachmentid="
				+ attachmentid + ";isencrypt=" + isencrypt + ";nickname=" 
				+ Base64Utils.encode(ActiveAccount.getInstance(ZApplication.getInstance()).nickname) +"\r\n";
		String head = String.format("%05d", string.length());
		Log.d(TAG, "getCompleteNofity->"+head+string);
		return head + string;
	}
}
