package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZViewFinder;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.prompt.Prompt;

public class FriendAddInviteActivity extends ZActivity {
	private ArrayList<String> list_friend_to_invite;
	String snstype;
	EditText content;
	TextView tv_char_left;
	private int WEIBO_MESSAGE_LEN_MAX = 140;
	TextView iv_ok;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_add_invite);
        
        Bundle bundle = getIntent().getExtras();
        snstype = bundle.getString("weibo_type");
        list_friend_to_invite = bundle.getStringArrayList("invite_list");
        
        
        ZViewFinder finder = getZViewFinder();
        iv_ok = finder.findTextView(R.id.iv_activity_friends_invite_ok);
        
        finder.setOnClickListener(R.id.iv_activity_friends_invite_ok, this);
        finder.setOnClickListener(R.id.iv_activity_friends_invite_back, this);
        
        findViewById(R.id.iv_activity_friends_invite_back).setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FriendAddInviteActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				FriendAddInviteActivity.this.finish();
				return false;
			}
		});
        
        tv_char_left = finder.findTextView(R.id.ed_activity_friends_invite_text_left);
        content = finder.findEditText(R.id.ed_activity_friends_invite_content); 
        
        StringBuilder sb = new StringBuilder();
        if(list_friend_to_invite.size()>0){
            
            for(String tmp: list_friend_to_invite){
            	sb.append("@");
            	sb.append(tmp);
            	sb.append(" ");
            }
            sb.append(getResources().getString(R.string.invite_dialog_prefix));
            if("sina".equals(snstype)){
            	//sb.append("@looklook-懂你的生活记录 ）");
            }else if("renren".equals(snstype)){
            	//sb.append("looklook-懂你的生活记录）");
            }else if("tencent".equals(snstype)){
            	//sb.append("@looklook6197－懂你的生活记录）");
            }
            
            int number;
            if(sb.length()>WEIBO_MESSAGE_LEN_MAX){
            	 content.setText(sb.toString().substring(0, 140));
            	 number = WEIBO_MESSAGE_LEN_MAX;
            }else{
            	 content.setText(sb.toString());
            	 number = sb.length();
            }
			
			content.setSelection(number);
			tv_char_left.setText("" + number + "/"+WEIBO_MESSAGE_LEN_MAX);
            iv_ok.setEnabled(true);
        }else{
        	//empty
        	content.setText("");
        	iv_ok.setEnabled(false);
        }
        
        
        
		content.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int number_left = /*WEIBO_MESSAGE_LEN_MAX - */s.length();
				tv_char_left.setText("" + number_left + "/"+WEIBO_MESSAGE_LEN_MAX);
			/*	if (temp.length() > WEIBO_MESSAGE_LEN_MAX) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					content.setText(s);
					content.setSelection(tempSelection);// 设置光标在最后
				}*/
				
				if(s.length()<=0){
					iv_ok.setEnabled(false);
				}else{
					iv_ok.setEnabled(true);
				}
			}
		});


	}

	@Override
	public void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub

		switch(msg.what){
			case WeiboRequester.SINA_INTERFACE_PUBLISH_WEIBO:
			case WeiboRequester.RENREN_INTERFACE_PUBLISH_WEIBO:
			case WeiboRequester.TENCENT_INTERFACE_PUBLISH_WEIBO:
				String weiboid = (String)msg.obj;
				if(weiboid!=null){
					//ZToast.showShort("微博发布ok");
//					Prompt.Alert(this, "微博发布成功, weiID:" + weiboid);
					Prompt.Dialog(this, false, "提示", "邀请成功", null);
				}else{
					//ZToast.showShort("微博发布失败");
//					Prompt.Alert(this, "微博发布失败");
					Prompt.Dialog(this, false, "提示", "邀请失败", null);
				}
				iv_ok.setEnabled(true);
				break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_activity_friends_invite_ok:
			if("sina".equals(snstype)){
				WeiboRequester.publishSinaWeibo(this, handler, content.getText().toString(), "http://t3.qpic.cn/mblogpic/a0582683f18a940923c0/2000", false);
				
			}else if("renren".equals(snstype)){
				WeiboRequester.publishRenrenWeibo(this, handler, content.getText().toString(), "http://t3.qpic.cn/mblogpic/a0582683f18a940923c0/2000", false);
				
			}else if("tencent".equals(snstype)){
				WeiboRequester.publishTencentWeibo(this, handler, content.getText().toString(), "http://t3.qpic.cn/mblogpic/a0582683f18a940923c0/2000", false);
			}
			
			iv_ok.setEnabled(false);
			break;
		case R.id.iv_activity_friends_invite_back:
			finish();
			break;
		}

	}

}
