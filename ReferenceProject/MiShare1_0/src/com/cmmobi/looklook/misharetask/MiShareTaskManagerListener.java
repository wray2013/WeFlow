package com.cmmobi.looklook.misharetask;

public interface MiShareTaskManagerListener {
	public void OnMiShareTaskCompleted(MiShareTask task);
	public void OnMiShareTaskRemoved(MiShareTask task);
	public void OnMiShareTaskStateChange(MiShareTask task, int state);
}
