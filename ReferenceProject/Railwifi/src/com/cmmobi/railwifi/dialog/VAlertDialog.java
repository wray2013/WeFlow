package com.cmmobi.railwifi.dialog;

import com.cmmobi.railwifi.MainApplication;
import com.cmmobi.railwifi.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * 自定义alertdialog
 * @author guoyang
 * @date 2014年8月7日
 */
public class VAlertDialog extends Dialog implements View.OnClickListener {

	private VAlertDialog(Context context) {
		this(context,R.style.CmmobiDialog);
	}
	private VAlertDialog(Context context,int style) {
		super(context,style);
		
		initView();
	}

	private Button btnConfim;
	private Button btnCancel;
	private TextView tvTitle;
	private TextView tvMessage;
	
	
	private void initView(){
		
		View view = LayoutInflater.from(MainApplication.getAppInstance()).inflate(R.layout.xdialog, null);
		btnConfim=(Button) view.findViewById(R.id.btn_ok);
		btnCancel=(Button) view.findViewById(R.id.btn_cancel);
		
		btnConfim.setOnClickListener(this);
		
		tvTitle = (TextView)view.findViewById(R.id.tv_title);
		tvMessage = (TextView)view.findViewById(R.id.tv_content);
		
		setContentView(view);
	}
	
	
	private DialogInterface.OnClickListener confimClickListener;
	private DialogInterface.OnClickListener cancelClickListener;
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_ok:
			
			this.dismiss();
			
			if(confimClickListener != null){
				confimClickListener.onClick(this, 0);
			}
			
			break;
		default:
			break;
		}
	}
	
	public static class Builder {
		
		private VAlertDialog vdialog;
		
        public Builder(Context context) {
        	vdialog=new VAlertDialog(context);
        }

        public Builder(Context context, int theme) {
        	vdialog=new VAlertDialog(context,theme);
        }
        
        
        /**
         * Set the title using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(int titleId) {
            vdialog.tvTitle.setText(titleId);
            return this;
        }
        
        /**
         * Set the title displayed in the {@link Dialog}.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(CharSequence title) {
        	vdialog.tvTitle.setText(title);
            return this;
        }
        
        
        /**
         * Set the message to display using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(int messageId) {
            vdialog.tvMessage.setText(messageId);
            return this;
        }
        
        /**
         * Set the message to display.
          *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(CharSequence message) {
        	vdialog.tvMessage.setText(message);
            return this;
        }
        
        
        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         * @param textId The resource id of the text to display in the positive button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setConfirmButton(int textId, final OnClickListener listener) {
            vdialog.btnConfim.setVisibility(View.VISIBLE);
            vdialog.btnConfim.setText(textId);
            vdialog.confimClickListener = listener;
            return this;
        }
//        
        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         * @param text The text to display in the positive button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setConfirmButton(CharSequence text, final OnClickListener listener) {
        	vdialog.btnConfim.setVisibility(View.VISIBLE);
            vdialog.btnConfim.setText(text);
            vdialog.confimClickListener = listener;
            return this;
        }
//        
//        /**
//         * Set a listener to be invoked when the negative button of the dialog is pressed.
//         * @param textId The resource id of the text to display in the negative button
//         * @param listener The {@link DialogInterface.OnClickListener} to use.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public Builder setNegativeButton(int textId, final OnClickListener listener) {
//            P.mNegativeButtonText = P.mContext.getText(textId);
//            P.mNegativeButtonListener = listener;
//            return this;
//        }
//        
//        /**
//         * Set a listener to be invoked when the negative button of the dialog is pressed.
//         * @param text The text to display in the negative button
//         * @param listener The {@link DialogInterface.OnClickListener} to use.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public Builder setNegativeButton(CharSequence text, final OnClickListener listener) {
//            P.mNegativeButtonText = text;
//            P.mNegativeButtonListener = listener;
//            return this;
//        }
//        
        /**
         * Sets whether the dialog is cancelable or not.  Default is true.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCancelable(boolean cancelable) {
        	vdialog.setCancelable(cancelable);
        	if(cancelable){
        		vdialog.setCanceledOnTouchOutside(true);
        	}
            return this;
        }
        
        /**
         * Sets the callback that will be called if the dialog is canceled.
         * @see #setCancelable(boolean)
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            vdialog.setOnCancelListener(onCancelListener);
            return this;
        }


        /**
         * Creates a {@link AlertDialog} with the arguments supplied to this builder. It does not
         * {@link Dialog#show()} the dialog. This allows the user to do any extra processing
         * before displaying the dialog. Use {@link #show()} if you don't have any other processing
         * to do and want this to be created and displayed.
         */
        public VAlertDialog create() {
            
//          if(vdialog.btnCancel.getVisibility()==View.GONE){
//           	vdialog.btnConfim.setBackgroundResource(R.drawable.);
//            	vdialog.btnCancel.setVisibility(View.GONE);
//			}
            
            return vdialog;
        }
//
        /**
         * Creates a {@link AlertDialog} with the arguments supplied to this builder and
         * {@link Dialog#show()}'s the dialog.
         */
        public VAlertDialog show() {
        	VAlertDialog dialog = create();
            dialog.show();
            return dialog;
        }
	}

	
}
