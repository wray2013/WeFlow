package com.cmmobi.looklook.info.profile;

import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-6-9
 */
public class DiaryWrapper {

	public int status=1;//1-未上传 2-上传中 3-上传暂停 4-已上传
	public MyDiary myDiary;
}
