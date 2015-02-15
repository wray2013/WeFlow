package com.cmmobi.looklook.info.profile;

import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;

public class CmmobiAccessToken{
	public String type; //0 looklook 1 sina 2 renren 6 tencent   c:cellphone   m:mail 
	public String id; //looklook的userid，cellphone的号码  mail的地址
	public String  expiresIn; //过期时间,时间段，  对looklook，手机和mail没有意义
	public long mExpiresTime; //过期时间, 时间点
	public String accessToken;  //对looklook，手机和mail则是密码
	public String clientid; //appKey
	
	public CmmobiAccessToken(int curWeiboIndex, OAuthV2 oa){
		if(curWeiboIndex==SHARE_TO.SINA.ordinal()){
			this.type = "1";
		} else if(curWeiboIndex==SHARE_TO.RENREN.ordinal()){
			this.type = "2";
		} else if(curWeiboIndex==SHARE_TO.TENC.ordinal()){
			this.type = "6";
		} 
		
		this.id = oa.getOpenid();
		this.expiresIn = oa.getExpiresIn();
		this.mExpiresTime = oa.getLongExpiresTime();
		this.accessToken = oa.getAccessToken();
		this.clientid = oa.getClientId();
	}

}