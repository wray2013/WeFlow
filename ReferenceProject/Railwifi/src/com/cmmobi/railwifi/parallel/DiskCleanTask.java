package com.cmmobi.railwifi.parallel;

import java.io.File;

import com.cmmobi.railwifi.event.ParallelEvent;

public class DiskCleanTask extends IYTask{
	private File cacheFold;

	public DiskCleanTask(long event_id, File cacheFold) {
		super(ParallelEvent.CACHE_CLEAR, event_id);
		this.cacheFold = cacheFold;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		if(!canRun()){
			return;
		}
		
		beginRun();
		
		cleanUpDir(cacheFold);
		
		super.processTask("OK");
		
		endRun();
	}
	
	private void cleanUpDir(File dir) {
		File[] fs = dir.listFiles();
		
		if(fs==null || fs.length<=0){
			return;
		}
		
		for (int i = 0; i < fs.length; i++) {
			if (fs[i].isDirectory()) {
				try {
					cleanUpDir(fs[i]);

				} catch (Exception e) {
				}

			} else {
				fs[i].delete();
			}
		}

		return;
	}


}
