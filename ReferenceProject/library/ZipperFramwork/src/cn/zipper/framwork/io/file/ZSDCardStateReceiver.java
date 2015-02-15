package cn.zipper.framwork.io.file;

import android.content.Intent;
import cn.zipper.framwork.core.ZBroadcastReceiver;

public abstract class ZSDCardStateReceiver extends ZBroadcastReceiver {
	
	public ZSDCardStateReceiver() {
		addAction(Intent.ACTION_MEDIA_MOUNTED);
		addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		addAction(Intent.ACTION_MEDIA_REMOVED);
		addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		getIntentFilter().addDataScheme("file");
	}
}
