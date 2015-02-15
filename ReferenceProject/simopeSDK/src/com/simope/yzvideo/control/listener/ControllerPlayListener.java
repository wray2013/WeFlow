package com.simope.yzvideo.control.listener;

public interface ControllerPlayListener {
	void canclePlayView(long currentTime,long totalTime);
	
	void showHideAboutYzDialog(boolean isShow);
	
	void showBuffText(long newposition);
	
	boolean notControllPlay();
	
	void setActTime(long current,long totaltime);
	
	void showDlnaDial();
	
	void sendPlayTimeOutMessage();
	
	void changerChannel(String which);
	
	void pauseShowAD();
	
	void startAfterAD();
}
