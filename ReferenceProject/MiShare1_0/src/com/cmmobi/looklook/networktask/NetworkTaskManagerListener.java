package com.cmmobi.looklook.networktask;

public interface NetworkTaskManagerListener {
	public void OnTaskRemoved(INetworkTask task);
	public void OnTaskStateChange(INetworkTask task, int state);
	public void notifyPrecentChange(INetworkTask task, long t, long c);
}
