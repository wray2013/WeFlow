package com.etoc.weflow.dialog;

import java.lang.ref.WeakReference;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ButtonHandler extends Handler {

	private static final String TAG = "ButtonHandler";
	private WeakReference<DialogInterface> mDialog;

	public ButtonHandler(DialogInterface dialog) {
		mDialog = new WeakReference<DialogInterface>(dialog);
	}

	@Override
	public void handleMessage(Message msg) {
		Log.v(TAG, "msg.wat:" + msg.what);
		switch (msg.what) {

		case DialogInterface.BUTTON_POSITIVE:
		case DialogInterface.BUTTON_NEGATIVE:
		case DialogInterface.BUTTON_NEUTRAL:
			((DialogInterface.OnClickListener) msg.obj).onClick(mDialog.get(),
					msg.what);
			break;
		}
	}
}
