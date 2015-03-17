package com.cmmobi.railwifi.activity;

import java.util.ArrayList;
import java.util.List;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.utils.AddressCodeParser;
import com.cmmobi.railwifi.utils.CharacterParser;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class AddressSelectorActivity extends TitleRootActivity {

	private ListView lvAddr;
	private ImageButton ibtnBack;
//	private ArrayAdapter<String> adapter = null;
	private AddressListAdapter adapter = null;
	private AddressCodeParser parser = null;
	
	private List<String> Province = new ArrayList<String>();//省
	private List<String> City     = new ArrayList<String>();//市
	private List<String> Area     = new ArrayList<String>();//区
	private String[] results = new String[]{"", "", ""};
	
	private String JSON = "";
	
	private static final int LAYER_BACK     = -1;
	private static final int LAYER_PROVINCE = 0;
	private static final int LAYER_CITY     = 1;
	private static final int LAYER_AREA     = 2;
	private static final int LAYER_RESULT   = 3;
	
	private int selectorLayer = LAYER_PROVINCE;
	private String currentName;
	
	public boolean isCityOnly = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		isCityOnly = getIntent().getBooleanExtra("isCityOnly", false);
		
		setTitleText("地址选择");
		hideRightButton();
		
		initView();
		
	}
	
	private void initView() {
		
		parser = new AddressCodeParser();
		JSON = parser.parseJsonFile(this, "addressjson.txt");
		
		ibtnBack = getLeftButton();
		ibtnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectorLayer--;
				makeData(currentName, false);
			}
		});
		
		lvAddr = (ListView) findViewById(R.id.lv_addr);
		lvAddr.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Object object = parent.getAdapter().getItem(position);
				String name = (String) object;
				if(name != null && !name.equals("")) {
					// 只需要提取城市(包括直辖市)
					if(isCityOnly) {
						if (name.equals("北京") || name.equals("天津")
								|| name.equals("上海") || name.equals("深圳")
								|| name.equals("重庆") || name.equals("香港")
								|| name.equals("澳门")) {
							results[0] = name; //省名
							results[1] = name;
							results[2] = "";
							giveResultBack(results);
							finish();
							return;
						} else {
							if(selectorLayer == LAYER_CITY) {
								results[1] = name;
								results[2] = "";
								giveResultBack(results);
								finish();
								return;
							}
						}
					}
					
					currentName = name;
					if(selectorLayer < LAYER_RESULT) {
						selectorLayer ++;
					}
					makeData(name, true);
				}
			}
		});
		lvAddr.setDividerHeight(DisplayUtil.getSize(this, 2));
		ViewUtils.setMarginTop(lvAddr, 1);
		ViewUtils.setMarginLeft(lvAddr, 12);
		ViewUtils.setMarginRight(lvAddr, 12);
//		adapter = new ArrayAdapter<String>(this, R.layout.activity_address_selector_item);
//		adapter.addAll(makeFakeData());
		
		adapter = new AddressListAdapter(this);
		selectorLayer = LAYER_PROVINCE;
		makeData("", false);
		
		lvAddr.setAdapter(adapter);
		
	}
	
	private void makeData(String name, boolean refresh) {
		List<String> data = new ArrayList<String>();//parser.parseJsonProvByJson(this, JSON, false);
		data.clear();
		switch(selectorLayer) {
		case LAYER_BACK:
			giveResultBack(null);
			finish();
			break;
		case LAYER_PROVINCE:
			Province.clear();
			Province.addAll(parser.parseJsonProvByJson(this, JSON, false));
			data.addAll(Province);
			break;
		case LAYER_CITY:
			if(name != null && !name.equals("")) {
				if(refresh) {
					City.clear();
					City.addAll(parser.parseCitiesByProv(JSON, name, false));
					results[0] = name; //省名
					results[1] = "";
					results[2] = "";
				}
			}
			data.addAll(City);
			break;
		case LAYER_AREA:
			if(name != null && !name.equals("")) {
				if(refresh) {
					Area.clear();
					Area.addAll(parser.parseAreasByProvCity(JSON, results[0], name, false));
					results[1] = name; //市名
					results[2] = "";
				}
			}
			if(Area.size() <= 0) { //没有区
//				Toast.makeText(this, "result = " + results[0] + results[1] + results[2], Toast.LENGTH_LONG).show();
				selectorLayer = LAYER_CITY;
				giveResultBack(results);
				return;
			} else {
				data.addAll(Area);
			}
			break;
		case LAYER_RESULT:
			if(name != null && !name.equals("")) {
				results[2] = name; //区名
//				Toast.makeText(this, "result = " + results[0] + results[1] + results[2], Toast.LENGTH_LONG).show();
				selectorLayer = LAYER_AREA;
				giveResultBack(results);
				return;
			}
			break;
		}
		if(adapter != null) {
			adapter.setData(data);
		}
	}
	
	private void giveResultBack(String[] res) {
		Intent data=new Intent();
		data.putExtra("address", res);
		setResult(RESULT_OK, data);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
		selectorLayer--;
		makeData(currentName, false);
		
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_address_selector;
	}


	private class AddressListAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private CharacterParser par;
		private List<String> list = new ArrayList<String>();
		
		public AddressListAdapter(Context ctx) {
			inflater = LayoutInflater.from(ctx);
			par = CharacterParser.getInstance();
		}
		
		public void setData(List<String> l) {
			list.clear();
			list.addAll(l);
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			Object object = getItem(position);
			String name = (String) object;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.activity_address_selector_item, null);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_item);
				initAdapterView(holder);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
//			String spell = par.getSpelling(name);
			holder.tvName.setText(name);
			
			return convertView;
		}
		
		private void initAdapterView(ViewHolder h) {
			// TODO Auto-generated method stub
			ViewUtils.setHeight(h.tvName, 122);
			ViewUtils.setMarginLeft(h.tvName, 30);
			ViewUtils.setTextSize(h.tvName, 36);
		}

		public class ViewHolder {
			TextView tvName;
		}
		
	}

}
