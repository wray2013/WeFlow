package com.etoc.weflow.activity.login;

import java.util.ArrayList;
import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.view.SpinnerEditText;
import com.etoc.weflow.view.SpinnerEditText.OnItemClickedListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class StartAccountActivity extends Activity implements OnClickListener {

	private final String TAG = "StartAccountActivity";
	
	private DisplayMetrics dm = new DisplayMetrics();
	
	private SpinnerEditText seAccount;
	private Button btnLogin;
	
	private Activity activity;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dm = getResources().getDisplayMetrics();
		activity = this;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setFinishOnTouchOutside(false);
		
		setContentView(R.layout.activity_startaccount);
		
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(this);
		
		seAccount = (SpinnerEditText) findViewById(R.id.se_account);
		seAccount.initDatas(fakeDatas());
		seAccount.setOnItemClickedListener(new OnItemClickedListener() {
			@Override
			public void OnItemRemoved(final SpinnerEditText s, final int itemIndex, String account) {
				
				if(activity != null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(activity);
					builder.setTitle("提示").setMessage("确定将\"" + account + "\"从本地记录中删除？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							s.removeItem(itemIndex);
							dialog.dismiss();
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
					AlertDialog alertdialog = builder.create();
					alertdialog.setCanceledOnTouchOutside(false);
		    		alertdialog.show();
				}
			}
		});
	}
	
	private List<String> fakeDatas() {
		List<String> fakedata = new ArrayList<String>();
		fakedata.add("wray2013");
		fakedata.add("wangac");
		fakedata.add("广州");
		fakedata.add("深圳");
		fakedata.add("重庆");
		fakedata.add("青岛");
		fakedata.add("石家庄");
		fakedata.add("武汉");
		return fakedata;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_login:
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			break;
		}
	}

}
