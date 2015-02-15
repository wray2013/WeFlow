package com.cmmobi.looklook.info.profile;

import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.constant.Constant.SEND_MSG_STATUS;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;

/**
 *  发送给对方的私信类
 *  
 *  语音私信发送流程：
 *  1. 录音开始，先站桩，分配一个本地的pendingMsgID（本地唯一）和sendmessage/PrivateCommonMessage条目结构，将msg结构加入到本地队列，status为message_init
 *  
 *  2. 录音结束后，handler的obj为对应pendingMsgID，将队列中的msg的status改为record_done
 *  
 *  3. 语音识别结束，handler的obj为对应pendingMsgID，reg_done
 *  1) 识别出结果，纯语音
 *  2）未识别结果，语音+文字
 *  
 *  4. 从队列中取元素，发起ria（sendmessage）创建私信结构，（3结束触发4，或用户手动重试触发）
 *  1）成功，status为ria_done
 *  2）失败，status为ria_fail
 *  3）超时，status为ria_timeout,ria_fail
 *  
 *  5. 将本地音频上传至指定socket（提交attach任务），状态为upload_pending
 *  1） 若文件不存在，提交任务，直接状态改为upload_fail
 *  
 *  6. 接收广播，根据attachID来找对应元素
 *  1）上传成功，状态改为upload_done all_done
 *  2) 上传失败，状态改为upload_fail
 *  3）上传超时，状态改为upload_timeout,upload_fail
 *  
 *  @author zhangwei
 * 
 * */
public class PrivateSendMessage{
	
	public SEND_MSG_STATUS status; //发送状态， 0失败 ，1成功， 2发送中
	
	public String userid; //发送者（自己）的id
	public String target_userid; //目的接收者的id
	
	public String privatemsgid; //私信id,ria返回的
	public String servertime;// 发送时间
	public String ip;//:"v.looklook.cn",  //SOCKET IP地址
	public String port; //:"7878",  //SOCKET端口

	  
	public String content; //私信内容
	public String privatemsgtype; //私信类型，1文字 2语音，3日记，4语音加文字
	
	public String audiopath; //服务器存储的短路径，data/video/looklook.mp3
	public String localaudiopath; // /looklook/<uid>/shortaudio/key 相对于sdcard的相对路径
	public String playtime;
	
	public String diaryid;// 日记id，当privatemsgtype==3有效
	public MyDiary diaries;
	
	
	public String uuid;//本地唯一
	
	/**
	 * step1, 录音开始，先站桩，分配一个本地的pendingMsgID
	 * @param userid
	 * @param target_userid
	 * @param pendingMsgID
	 */
	public PrivateSendMessage(String userid, String target_userid, String pendingMsgID){
		this.uuid = pendingMsgID;
		this.userid = userid;
		this.target_userid = target_userid;
	}
	
	public void updateStatus(SEND_MSG_STATUS status){
		this.status = status;
	}
	
	public void updatePlaytime(String playtime){
		this.playtime = playtime;
	}
	
	public void updateType(String privatemsgtype){
		this.privatemsgtype = privatemsgtype;
	}
	
	public void updateContent(String content){
		this.content = content;
	}
	
	/**
	 * step3，语音识别结束，有语音则设置content
	 * @param content
	 */
	public void setContent(String content){
		this.content = content;
	}
	
	public void setLocalFilePath(String path){
		this.localaudiopath = path;
	}
	
	public void setPrivateMsgtype(String privatemsgtype){
		this.privatemsgtype = privatemsgtype;
	}
	
	/**
	 * step4, 从队列中取元素，发起ria（sendmessage）创建私信结构, ria响应后更新sendmessage
	 * @param resp
	 */
	public void update(GsonResponse2.sendmessageResponse resp){
		if(resp!=null){
			if("0".equals(resp.status)){
				status = Constant.SEND_MSG_STATUS.RIA_DONE;
				if(resp.servertime!=null){
					servertime = resp.servertime;
				}
				
				if(resp.privatemsgid!=null){
					privatemsgid = resp.privatemsgid;
				}
				
				if(resp.audiopath!=null){
					audiopath = resp.audiopath;
				}
				
				if(resp.ip!=null){
					ip = resp.ip;
				}
				
				if(resp.port!=null){
					port = resp.port;
				}
				
			}else{
				status = Constant.SEND_MSG_STATUS.RIA_FAIL;
			}
		}else{
			status = Constant.SEND_MSG_STATUS.RIA_FAIL;
		}
	}
	

	
}