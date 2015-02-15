package com.cmmobi.railwifi.parallel;

import java.io.File;
import java.text.DecimalFormat;

import com.cmmobi.railwifi.event.ParallelEvent;

public class DiscScanTask extends IYTask{
	private File cacheFold;

	public DiscScanTask(long event_id, File cacheFold) {
		super(ParallelEvent.CACHE_SCAN_DU, event_id);
		this.cacheFold = cacheFold;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(!canRun()){
			return;
		}
		
		beginRun();
		
		long du_total = duDir(cacheFold);
		
		StringBuilder du_str = new StringBuilder();
		DecimalFormat df = new DecimalFormat("0.00");
		
		if(du_total<1024){
			du_str.append(du_total);
			du_str.append("B");
		}else if(du_total<1024*1024){
			du_str.append(df.format(1.0f*du_total/1024));
			du_str.append("K");
		}else if(du_total<1024*1024*1024){
			du_str.append(df.format(1.0f*du_total/1024/1024));
			du_str.append("M");
		}else if(du_total<1024*1024*1024*1024){
			du_str.append(df.format(1.0f*du_total/1024/1024/1024));
			du_str.append("G");
		}
		
		super.processTask(du_str.toString());
		
		endRun();
	}
	
	private long duDir(File dir) {
		File[] fs = dir.listFiles();
		long total_du = 0;
		
		if(fs==null || fs.length<=0){
			return 0;
		}
		
		for (int i = 0; i < fs.length; i++) {
			if (fs[i].isDirectory()) {
				try {
					total_du += duDir(fs[i]);

				} catch (Exception e) {
				}

			} else {
				total_du += fs[i].length();
			}
		}

		return total_du;
	}


}
