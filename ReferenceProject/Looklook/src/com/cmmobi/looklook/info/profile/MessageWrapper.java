package com.cmmobi.looklook.info.profile;

import java.util.Iterator;
import java.util.LinkedList;

import com.cmmobi.looklook.activity.DiaryDetailActivity.DiaryType;
import com.cmmobi.looklook.common.gson.GsonResponse2.MessageUser;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyMessage;

/**
 *  私信类，每一个对方用户对应一个私信类
 *  
 *  @author zhangwei
 * 
 * */
public class MessageWrapper {

	public LinkedList<PrivateCommonMessage> msgs; //私信
	//public LinkedList<PrivateSendMessage> send_msgs; //发给对方的私信
	
	//（对方）信息：
	public String userid; //（对方）用户ID
	public String nickname;//（对方）昵称，base64编码,关注人如果有备注显示备注名称
	public String headimageurl;//（对方）头像URL，可能为空
	public String isattention;
	public String sex;
	public String signature;
	
	//用户聚合展示时，显示最新的内容
	public String act; //1私信，2活动，3推荐，4附近，5陌生人6LOOKLOOK官方
	public String privmsg_type; //私信类型 --- 1代表纯文字  2代表日记  3代表语音 4语音加文字
	public String content; //私信内容,当私信类型有语音时则不展示该字段
	
	//最后一次会话时间
	public long lastTimeMill;
	
	public int unReadNum;
	
	public MessageWrapper(){
		msgs = new LinkedList<PrivateCommonMessage>();
		//send_msgs = new LinkedList<PrivateSendMessage>();
		lastTimeMill = 0;
		unReadNum = 0;
	}
	
	/**
	 *  批量构造接收的消息
	 * */
	public MessageWrapper(String userid, MyMessage[] array){
		this();
		if(array!=null && array.length>0){
			for(int i=0; i<array.length; i++){
				if(array[i]!=null && array[i].messageid!=null) {
					long this_time = 0;
					try{
						this_time = Long.valueOf(array[i].timemill);
						if(lastTimeMill<this_time){
							lastTimeMill = this_time;
						}
					}catch(NumberFormatException e){
						e.printStackTrace();
					}

					msgs.add(new PrivateCommonMessage(userid, array[i], this_time));

				}
			}
		}

	}
	
	public boolean contains(MyMessage msg){
		boolean ret = false;
		if(msg!=null && msg.messageid!=null){
			for(Iterator<PrivateCommonMessage> it = msgs.iterator(); it.hasNext(); ) {
				PrivateCommonMessage item = it.next();
				if(item!=null && item.r_msg!=null && item.r_msg.messageid!=null && item.r_msg.messageid.equals(msg.messageid)){
					ret = true;
					break;
				}
			} 
			
		}

		return ret;
	}
	
	public void updateMessageUser(MessageUser item){
		
		if(item!=null){
			if(item.headimageurl!=null){
				this.headimageurl = item.headimageurl;
			}
			
			if(item.isattention!=null){
				this.isattention = item.isattention;
			}
			
			if(item.nickname!=null){
				this.nickname = item.nickname;
			}
			
			if(item.sex!=null){
				this.sex = item.sex;
			}
			
			if(item.signature!=null){
				this.signature = item.signature;
			}
			
			if(item.userid!=null){
				this.userid = item.userid;
			}
			
			
			if(item.message!=null && item.message.length>0){

				for(int i=0; i<item.message.length; i++){

					try{
						long msg_time = Long.valueOf(item.message[i].timemill);
						if(item.message[i].timemill!=null && msg_time>lastTimeMill){
							lastTimeMill = msg_time;
						}
					}catch(NumberFormatException e){
						e.printStackTrace();
					}
					
					if(item.message[i].act!=null){
						this.act = item.message[i].act;
					}

					if(item.message[i].privmsg!=null && item.message[i].privmsg.privmsg_type!=null){
						this.privmsg_type = item.message[i].privmsg.privmsg_type;
					}
					
					if(item.message[i].content!=null){
						this.content = item.message[i].content;
					}
				
				}
			
			}
		}



	}
	
	public void consumeUnreadMsgs(){
		synchronized (this) {
			unReadNum = 0;
		}
	}
	
	public int getUnreadMsgs(){
		return unReadNum;
	}
	
	/**
	 *  清理私信
	 *  type：
	 *  -1 全部
	 *  1 文本私信 -》 日记或文本私信
	 *  2 图片私信 -》 日记
	 *  3 音频私信  -》 日记或音频私信
	 *  4 视频私信 -》 日记
	 * */
	public void  cleanMessageByType(int type){
		if(msgs==null || msgs.isEmpty()){
			return;
		}
		
		for(Iterator<PrivateCommonMessage> it = msgs.iterator(); it.hasNext(); ) {
			PrivateCommonMessage item = it.next();
			if(item!=null){
				if(!item.send && item.r_msg!=null && item.r_msg.privmsg!=null){
					if(type<0){
						it.remove();
					}else{
						if(type==1){ //1 文本私信 -》 日记或文本私信
							if("1".equals(item.r_msg.privmsg.privmsg_type)){
								it.remove();
							}else if(item.r_msg.privmsg.diaries!=null){
								DiaryType dt = item.r_msg.privmsg.diaries.getDiaryType();
								if(DiaryType.TEXT.equals(dt)){
									it.remove();
								}
							}
						}else if(type==2){ //2 图片私信 -》 日记
							if(item.r_msg.privmsg.diaries!=null){
								DiaryType dt = item.r_msg.privmsg.diaries.getDiaryType();
								if(DiaryType.PICTURE.equals(dt)){
									it.remove();
								}
							}
						}else if(type==3){ //3 音频私信  -》 日记或音频私信
							if("2".equals(item.r_msg.privmsg.privmsg_type) || "4".equals(item.r_msg.privmsg.privmsg_type) ){
								it.remove();
							}else if(item.r_msg.privmsg.diaries!=null){
								DiaryType dt = item.r_msg.privmsg.diaries.getDiaryType();
								if(DiaryType.AUDIO.equals(dt)){
									it.remove();
								}
							}
						}else if(type==4){ //4 视频私信 -》 日记
							if(item.r_msg.privmsg.diaries!=null){
								DiaryType dt = item.r_msg.privmsg.diaries.getDiaryType();
								if(DiaryType.VEDIO.equals(dt)){
									it.remove();
								}
							}
						}

					}

					
				}else if(item.send && item.s_msg!=null){
					if(type<0){
						it.remove();
					}else{
						if(type==1){ //1 文本私信 -》 日记或文本私信
							if("1".equals(item.s_msg.privatemsgtype)){
								it.remove();
							}else if(item.s_msg.diaries!=null){
								DiaryType dt = item.s_msg.diaries.getDiaryType();
								if(DiaryType.TEXT.equals(dt)){
									it.remove();
								}
							}
						}else if(type==2){ //2 图片私信 -》 日记
							if(item.s_msg.diaries!=null){
								DiaryType dt = item.s_msg.diaries.getDiaryType();
								if(DiaryType.PICTURE.equals(dt)){
									it.remove();
								}
							}
						}else if(type==3){  //3 音频私信  -》 日记或音频私信
							if("2".equals(item.s_msg.privatemsgtype) || "4".equals(item.s_msg.privatemsgtype) ){
								it.remove();
							}else if(item.s_msg.diaries!=null){
								DiaryType dt = item.s_msg.diaries.getDiaryType();
								if(DiaryType.AUDIO.equals(dt)){
									it.remove();
								}
							}
						}else if(type==4){
							if(item.s_msg.diaries!=null){
								DiaryType dt = item.s_msg.diaries.getDiaryType();
								if(DiaryType.VEDIO.equals(dt)){
									it.remove();
								}
							}
						}
					}
				}
			}

		} 
		
	}

	public void addUnreadNum(int num) {
		// TODO Auto-generated method stub
		synchronized (this) {
			unReadNum += num;
		}

	}
	

}
