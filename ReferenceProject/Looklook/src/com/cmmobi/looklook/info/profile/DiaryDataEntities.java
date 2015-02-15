package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.HashMap;

import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiaryids;
import com.cmmobi.looklook.common.gson.GsonResponse2.diarycommentlistItem;
import com.cmmobi.looklook.common.gson.GsonResponse2.taglistItem;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-7-8
 */
public class DiaryDataEntities {

	/**
	 * 存储该用户所有增量缓存数据
	 */
	public ArrayList<MyDiary> diaryList=new ArrayList<MyDiary>();
	/**
	 * 动态-分享 缓存每个用户最新一页分享数据
	 * key-userid
	 * value-该用户分享缓存数据
	 */
	public HashMap<String, ArrayList<MyDiary>> shareDiaryMap = new HashMap<String, ArrayList<MyDiary>>();
	/**
	 *  动态-收藏
	 *  缓存最新一页收藏日记数据
	 */
	public ArrayList<MyDiary> collectDiaryList=new ArrayList<MyDiary>();
	/**
	 *  动态-赞(转发) 缓存每个用户最新一页赞数据
	 *  key-userid 
	 *  value-该用户本赞日记数据
	 */
	public HashMap<String, ArrayList<MyDiary>> praiseDiaryMap = new HashMap<String, ArrayList<MyDiary>>();
	/**
	 *  动态-评论
	 *  缓存用户一页评论数据
	 */
	public ArrayList<diarycommentlistItem> commentDiaryList=new ArrayList<diarycommentlistItem>();
	
	/**
	 * 保存其他用户空间背景、心情、头像、粉丝数、关注数、昵称、签名
	 */
	public HashMap<String, OtherUserInfo> otherUserInfos=new HashMap<String, OtherUserInfo>();

	public ArrayList<MyDiaryids> praisedDiariesIDList = new ArrayList<MyDiaryids>();
	public ArrayList<MyDiaryids> collectDiariesIDList = new ArrayList<MyDiaryids>();

	// 标签列表
	public ArrayList<taglistItem> tagsList = new ArrayList<taglistItem>();
	// 保存未删除的新浪分享id
	public ArrayList<String> removeSinaIdList = new ArrayList<String>();
	// 保存未删除的腾讯分享id
	public ArrayList<String> removeTencentIdList = new ArrayList<String>();
	// 保存未删除的人人分享id
	public ArrayList<String> removeRenrenIdList = new ArrayList<String>();
	
	//本地日记最新时间
	public String mydiaryFirsttime="";
	//本地日记最旧时间
	public String mydiaryLasttime="";
}
