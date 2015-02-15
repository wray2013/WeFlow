package com.cmmobi.looklook.common.utils;

public class DiaryEditNote {
	public int mediaType;// 1、视频 2、音频 3、图片 4、便签
	public String mediaPath;
	public int effectIndex;
	public String soundtrackPath;
	public double percent;
	public boolean isMontaged;// 是否经过裁剪
	public boolean isEffect;// 是否特效处理
	public boolean isAddSoundTrack;// 是否添加配乐
}
