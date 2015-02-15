package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZLog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;

/**
 * 标签页面
 * 
 * @author Administrator
 * 
 */
public class TagActivity extends ZActivity {

	private GridView list;
	private Tagadapter ta;
	private List<Map<String, String>> data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_tag);

		list = (GridView) findViewById(R.id.list);

		handler = getHandler();
		//Requester.submitUA(handler);
		
		
		
		

	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	private class Tagadapter extends BaseAdapter {

		private Context context;
		private LayoutInflater inflater;
		private List<Map<String, String>> data;

		public Tagadapter(Context context, List<Map<String, String>> data) {
			this.data = data;
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.activity_activity_tag_item, null);
				holder = new ViewHolder();
				holder.img01 = (Button)convertView.findViewById(R.id.img01);
				if (data.get(position).get("name") == null) {
					System.out.println("null");
				}
				holder.img01.setText(data.get(position).get("name"));
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			return convertView;
		}

		class ViewHolder {
			Button img01;
		}

	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {

/*		case Requester.RESPONSE_TYPE_UA:
			ActiveAccount acct = ActiveAccount.getInstance(this);
			acct.snstype = "0";
			acct.username = "222222@163.com";
			acct.password = "123456";
			Requester.login(this, handler, acct);
			break;

		case Requester.RESPONSE_TYPE_LOGIN:
			AppState.setLoginResponse(msg.obj);

			Requester.requestTagList(handler, null);

			break;*/

		case Requester2.RESPONSE_TYPE_TAG_DIARY_LIST:
			System.out.println("tag---------tag");
			GsonResponse2.taglistResponse tag = (GsonResponse2.taglistResponse) msg.obj;
			ZLog.printObject(tag);
			GsonResponse2.taglistItem[] item = tag.tags;
			data = new ArrayList<Map<String, String>>();
			for (int i = 0; i < item.length; i++) {
				Map<String, String> map = new HashMap<String, String>();

				String name = item[i].name;
				String id = item[i].id;
				String check = item[i].checked;
				map.put("id", id);
				map.put("name", name);
				map.put("check", check);
				data.add(map);

			}

			ta = new Tagadapter(this, data);
			list.setAdapter(ta);

			break;
		}

		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
