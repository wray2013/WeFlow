package com.etoc.weflow.fragment;

import java.util.ArrayList;
import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.adapter.RechargeAdapter;
import com.etoc.weflow.net.GsonResponseObject;
import com.etoc.weflow.net.GsonResponseObject.RechargePhoneResp;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.ViewUtils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RechargeQQFragment extends Fragment{

	private View mView;
	GridView gvQQMenu = null;
	RechargeAdapter adapter = null;
	TextView tvCostCoins = null;
	List<RechargePhoneResp> itemList = new ArrayList<GsonResponseObject.RechargePhoneResp>();
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(mView != null){
		    ViewGroup parent = (ViewGroup) mView.getParent();  
		    if (parent != null) {  
		        parent.removeView(mView);  
		    }   
		    return mView;
		}
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_recharge_qq, null);
		initView(v);
		mView = v;
		return v;
	}
	
	private void initView(View view) {
		gvQQMenu = (GridView) view.findViewById(R.id.gv_menu);
		
		int [] moneys = {1,5,10,20,50,100};
		
		if (itemList.size() == 0) {
			for (int i = 0;i < 6;i++) {
				RechargePhoneResp resp = new RechargePhoneResp();
				resp.chargesid = "" + i;
				resp.money = moneys[i] + "Q币";
				resp.cost = moneys[i] * 100 + "";
				itemList.add(resp);
			}
		}
		
		adapter = new RechargeAdapter(getActivity(), itemList);
		gvQQMenu.setAdapter(adapter);
		
		gvQQMenu.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		gvQQMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				// TODO Auto-generated method stub
				adapter.setSelect(position);
				adapter.notifyDataSetChanged();
				tvCostCoins.setText(adapter.getSelectCost());
			}
		});
		
		tvCostCoins = (TextView) view.findViewById(R.id.tv_cost_coins);
		ViewUtils.setMarginRight(tvCostCoins, 16);
		tvCostCoins.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 32));
		tvCostCoins.setText(adapter.getSelectCost());
	}
}
