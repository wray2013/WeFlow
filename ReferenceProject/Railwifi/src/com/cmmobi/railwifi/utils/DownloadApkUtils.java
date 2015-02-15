package com.cmmobi.railwifi.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

import com.cmmobi.railwifi.dialog.DialogUtils;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;

public class DownloadApkUtils implements Callback {
	private Context context;
	private String sourceId;
	private Handler handler;
//	private XProcessDialog dialog;
	
	public DownloadApkUtils(Context context,String id) {
		this.context = context;
		this.sourceId = id;
		handler = new Handler(this);
	}
	
	public void download() {

		Requester.requestThirdPage(handler, sourceId);

	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_THIRD_PAGE:

			if (msg.obj != null) {
				final GsonResponseObject.commonContent content = (GsonResponseObject.commonContent) msg.obj;
				if ("0".equals(content.status)) {
					
					DialogUtils.SendDownloadDialog(context, content.title, content.content, content.source_url);
					
				} else {
					
				}
			} else {
				
			}
			break;
		}
		return false;
	}
	

}
