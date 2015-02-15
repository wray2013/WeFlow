package com.cmmobi.looklook.activity.login;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.TitleRootActivity;
import com.cmmobi.looklook.common.adapter.CountrySortAdapter;
import com.cmmobi.looklook.common.utils.CountryBean;
import com.cmmobi.looklook.common.view.QuickBarView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.sns.utils.PinYinUtil;

public class CountryListActivity extends TitleRootActivity implements OnItemClickListener{

	
	QuickBarView quickBarView;
	ListView listview;
	CountrySortAdapter adapter;
	private CountryBean currCountry;
	private ActiveAccount activeAccount;
	private ArrayList<CountryBean> frequentCountry;
	private LinearLayout loadingView = null;
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_country_list;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("国家和地区");
		hideRightButton();
		
		listview = (ListView)findViewById(R.id.listview_country);
		quickBarView = (QuickBarView) findViewById(R.id.quick_bar);

		quickBarView.setListView(listview);
		loadingView = (LinearLayout) findViewById(R.id.fullscreen_loading_indicator);
		loadingView.setVisibility(View.VISIBLE);
		
		activeAccount = ActiveAccount.getInstance(MainApplication.getInstance());
		frequentCountry = AccountInfo.getInstance(activeAccount.getUID()).frequentCountry;

		handler = new Handler(this);
		adapter = new CountrySortAdapter(this);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				final ArrayList<CountryBean> countryList = getCountryData();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						adapter.setData(countryList);
						adapter.notifyDataSetChanged();
						loadingView.setVisibility(View.GONE);
					}
				});
			}
		}).start();
//		adapter.setData(getCountryData());
		
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(this);	
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CountryBean bean = (CountryBean) parent.getItemAtPosition(position);
		adapter.setDefaultCountry(bean);
		currCountry = bean;
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResultData();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			if (currCountry != null ){
				setResultData();
				currCountry.sortKey = "★";
				if (!frequentCountry.contains(currCountry)) {
					frequentCountry.add(0,currCountry);
				} else{
					frequentCountry.remove(currCountry);
					frequentCountry.add(0,currCountry);
				}
			}
				
			break;

		default:
			break;
		}
		super.onClick(v);
	}

	private void setResultData() {
		Intent intent = new Intent();
		if(currCountry != null){
			intent.putExtra("countryName", currCountry.countryName);
			intent.putExtra("countryNo", currCountry.countryNo);
		}
		setResult(RESULT_OK, intent);
	}
	
	
	public ArrayList<CountryBean> getCountryData(){
		ArrayList<CountryBean> arr = null;
		InputStream input = null;
		try {
			input = getAssets().open("countrycode.xml");
			arr = parse(input);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (arr != null) {
			if (frequentCountry != null && frequentCountry.size() > 0) {
				arr.addAll(frequentCountry);
			}
			Collections.sort(arr, new CountryComparator());
		}
		return arr;
	}
	
	public class CountryComparator implements Comparator<CountryBean> {

		@Override
		public int compare(CountryBean lhs, CountryBean rhs) {
			if (lhs.sortKey == null && rhs.sortKey != null) {
				return 1;
			}else if(lhs.sortKey != null && rhs.sortKey == null){
				return -1;
			}else if(lhs.sortKey == null && rhs.sortKey == null){
				return 0;
			}
			
			if ("★".equals(lhs.sortKey) && "★".equals(rhs.sortKey)) {
				return 0;
			} else if ("★".equals(lhs.sortKey) && !"★".equals(rhs.sortKey)) {
				return -1;
			} else if ("★".equals(lhs.sortKey) && "★".equals(rhs.sortKey)) {
				return 1;
			}
			return lhs.sortKey.compareTo(rhs.sortKey);
		}

	}
	
	protected ArrayList<CountryBean> parse(InputStream inputStream) throws XmlPullParserException, IOException{
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		int eventType = parser.getEventType();
		ArrayList<CountryBean> arr = new ArrayList<CountryBean>();
		String namespace = null;
		while(eventType != XmlPullParser.END_DOCUMENT){
			switch(eventType){
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if(parser.getName().equals("countrycode")){
					String name_zh = parser.getAttributeValue(namespace, "name_zh");
					String countryCode = "+" + parser.getAttributeValue(namespace, "code");
					String firstChar = PinYinUtil.getPinYin(name_zh);
					CountryBean item = new CountryBean();
					item.countryName = name_zh;
					item.countryNo = countryCode;
					item.sortKey = firstChar;
					arr.add(item);
				}
				break;
			case XmlPullParser.END_TAG:
				break;
			}
			eventType = parser.next();
		}
		return arr;
	}
	
}
