package com.simope.yzvideo.entity;

import java.io.Serializable;



public class Video implements Serializable {
	private static final long serialVersionUID = -4524603068931974655L;
	private int id;
	private String title;
	private String describle;
	private String channel;
	private int vodtype;
	private String randomInt;
	private long playedTime;
	private long totalTime;
	private boolean checked;
	private long lastPlayTime;		
	private String playAddress;	
	private String channel_img;
	private String vod_img;	
	private int isVideo=0;	
	private int state=99;//1为获取地址超时、2为没有播放地址，即解析出错 ；99默认
	private boolean native_video;
	private String [] multiAddress;
	private int updateimage=0;
	private int sortupimage=0;
	
		
	
	
	
	
	public int getSortupimage() {
		return sortupimage;
	}

	public void setSortupimage(int sortupimage) {
		this.sortupimage = sortupimage;
	}

	public int getUpdateimage() {
		return updateimage;
	}

	public void setUpdateimage(int updateimage) {
		this.updateimage = updateimage;
	}

	public boolean isNative_video() {
		return native_video;
	}

	public void setNative_video(boolean native_video) {
		this.native_video = native_video;
	}

	public String[] getMultiAddress() {
		return multiAddress;
	}

	public void setMultiAddress(String[] multiAddress) {
		this.multiAddress = multiAddress;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getIsVideo() {
		return isVideo;
	}

	public void setIsVideo(int isVideo) {
		this.isVideo = isVideo;
	}

	public String getRandomInt() {
		return randomInt;
	}

	public void setRandomInt(String randomInt) {
		this.randomInt = randomInt;
	}


	public String getChannel_img() {
		return channel_img;
	}

	public void setChannel_img(String channel_img) {
		this.channel_img = channel_img;
	}

	public String getVod_img() {
		return vod_img;
	}

	public void setVod_img(String vod_img) {
		this.vod_img = vod_img;
	}

	public long getLastPlayTime() {
		return lastPlayTime;
	}

	public void setLastPlayTime(long lastPlayTime) {
		this.lastPlayTime = lastPlayTime;
	}

	

	public String getPlayAddress() {
		return playAddress;
	}

	public void setPlayAddress(String playAddress) {
		this.playAddress = playAddress;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescrible() {
		return describle;
	}

	public void setDescrible(String describle) {
		this.describle = describle;
	}


	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public int getVodtype() {
		return vodtype;
	}

	public void setVodtype(int vodtype) {
		this.vodtype = vodtype;
	}
	
	
	public long getPlayedTime() {
		return playedTime;
	}

	public void setPlayedTime(long playedTime) {
		this.playedTime = playedTime;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("播放ID:").append(id).append('\n');
		sb.append("已经播放时间").append(playedTime).append('\n');
		sb.append("总时间").append(totalTime).append('\n');
		sb.append("播放title:").append(title).append('\n');
		sb.append("播放介绍:").append(describle).append('\n');
		sb.append("channel频道属于:").append(channel).append('\n');
//		sb.append("vodtype点播类型:").append(vodtype).append('\n');
		sb.append("解析地址:").append(playAddress).append('\n');
		sb.append("图片点播地址:").append(vod_img).append('\n');
		sb.append("图片分类地址:").append(channel_img).append('\n');

		return sb.toString();
	}


}
