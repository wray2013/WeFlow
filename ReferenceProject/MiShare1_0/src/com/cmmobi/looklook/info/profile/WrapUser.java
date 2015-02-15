package com.cmmobi.looklook.info.profile;

import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.OtherZoneDiaryIds;
import com.google.gson.Gson;
import com.hp.hpl.sparta.xpath.ThisNodeTest;

public class WrapUser {

	public String userid; // 好友ID
	public String headimageurl; // "http://…jpg", 头像URL，可为空
	public String nickname; // 昵称
	public String markname;
	public String sex; // 0男 1女 2未知
	public String signature; // "jdfdf"个人签名（base64编码）
	
	public String diarycount; // 日记数
	
	
	public String isattention;
	
	public String haschange; //用户是否有更新 1有 0没有
	public OtherZoneDiaryIds[] diaryids; //
	public MyDiary[] diaries;
	public String isnew; // 是否是新增的 1是 0不是
	public String isfriend; // 是否是好友，0 不是  1是
	public String sortKey;

	public String requestmsg; //验证信息
	public String update_time; //更新时间
	
	public String t_change;  //更新的时间
	
	public String micnum; //微享号
	
	public String phonename; //通讯录中的名称
	public String phonenum; //通讯录中的手机号

	
	
	public String telname; //手机好友名
	public String source; //1微享用户 2手机用户    
	public String request_status;  //请求状态 1添加 2等待验证 3接受 4已添加

	public Boolean isRecent = false; //是否是最近联系人
	
	public String telephone;   //好友手机号码

	
	
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o == null) {
			 return false;
		}
		WrapUser user = (WrapUser)o;
		if (user.userid == null && this.userid == null) {
			if(user.phonename.equals(this.phonename)&&user.phonenum.equals(this.phonenum)){
				return true;
			}else{
				return false;
			}
		}else if(user.userid == null || this.userid == null){
			return false;
		}else{
			return user.userid.equals(this.userid);
		}
	}
	
	
}
