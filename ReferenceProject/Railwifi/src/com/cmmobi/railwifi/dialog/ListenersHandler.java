package com.cmmobi.railwifi.dialog;

import java.lang.ref.WeakReference;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Handler;
import android.os.Message;

public class ListenersHandler extends Handler {
    private static final int DISMISS = 0x43;
    private static final int CANCEL = 0x44;
    private static final int SHOW = 0x45;
    
    private WeakReference<DialogInterface> mDialog;

    public ListenersHandler(Dialog dialog) {
        mDialog = new WeakReference<DialogInterface>(dialog);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case DISMISS:
                ((OnDismissListener) msg.obj).onDismiss(mDialog.get());
                break;
            case CANCEL:
                ((OnCancelListener) msg.obj).onCancel(mDialog.get());
                break;
            case SHOW:
                ((OnShowListener) msg.obj).onShow(mDialog.get());
                break;
    		case DialogInterface.BUTTON_POSITIVE:
    		case DialogInterface.BUTTON_NEGATIVE:
    		case DialogInterface.BUTTON_NEUTRAL:
    			((DialogInterface.OnClickListener) msg.obj).onClick(mDialog.get(),
    					msg.what);
    			break;
        }
    }
}
