package com.cmmobi.railwifi.activity;

import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.widget.TextView;
import cn.trinea.android.common.util.FileUtils;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;

public class RailTravelProtocolActivity extends TitleRootActivity {

	private TextView tvProtocol;
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_rail_travel_protocol;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		hideRightButton();
		setTitleText("火车网在线支付用户服务协议");
		initViews();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				final String str = FileUtils.getFromAssets(RailTravelProtocolActivity.this, "protocol.txt","utf-8");
				RailTravelProtocolActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						tvProtocol.setText(str);
					}
				});
			}
		}).start();
	}
	
	private void initViews() {
		tvProtocol = (TextView) findViewById(R.id.tv_protocol);
		ViewUtils.setMarginLeft(tvProtocol, 24);
		ViewUtils.setMarginRight(tvProtocol, 24);
		ViewUtils.setMarginTop(tvProtocol, 12);
		ViewUtils.setMarginBottom(tvProtocol, 12);
		tvProtocol.setTextSize(DisplayUtil.textGetSizeSp(this, 30));
//		tvProtocol.setText(Html.fromHtml("<h2>一、总则</h2><ul><li>1．1　用户应当同意本协议的条款并按照页面上的提示完成全部的注册程序。用户在进行注册程序过程中点击\"同意\"按钮即表示用户与百度公司达成协议，完全接受本协议项下的全部条款。<br>"
//                    +"</li><li>1．2　用户注册成功后，百度将给予每个用户一个用户帐号及相应的密码，该用户帐号和密码由用户负责保管；用户应当对以其用户帐号进行的所有活动和事件负法律责任。<br>"
//                    +"</li><li>1．3　用户一经注册百度帐号，除非子频道要求单独开通权限，用户有权利用该账号使用百度各个频道的单项服务，当用户使用百度各单项服务时，用户的使用行为视为其对该单项服务的服务条款以及百度在该单项服务中发出的各类公告的同意。<br>"
//                    +"</li><li>1．4　百度会员服务协议以及各个频道单项服务条款和公告可由百度公司随时更新，且无需另行通知。您在使用相关服务时,应关注并遵守其所适用的相关条款。<br>"
//                    +"</li></ul><p>您在使用百度提供的各项服务之前，应仔细阅读本服务协议。如您不同意本服务协议及/或随时对其的修改，您可以主动取消百度提供的服务；您一旦使用百度服务，即视为您已了解并完全同意本服务协议各项内容，包括百度对服务协议随时所做的任何修改，并成为百度用户。 </p>"));
	}

}
