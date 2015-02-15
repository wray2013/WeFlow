package com.cmmobi.looklook.fragment;

import com.cmmobi.looklook.common.gson.WeiboResponse.BasicInformation;
import com.cmmobi.looklook.common.gson.WeiboResponse.EmotionalState;
import com.cmmobi.looklook.common.gson.WeiboResponse.Image;
import com.cmmobi.looklook.common.gson.WeiboResponse.Like;
import com.cmmobi.looklook.common.gson.WeiboResponse.School;
import com.cmmobi.looklook.common.gson.WeiboResponse.Work;

public class WrapRenrenUser {

	public long id; //用户ID
	public String name; //用户名
	public Image[] avatar;
	public int star;
	public BasicInformation basicInformation;
	public School[] education;
	public Work[] work;
	public Like[] like;
	public EmotionalState emotionalState;

	public String sortKey;
}
