package com.etoc.weflow.fragment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.adapter.RechargeAdapter;
import com.etoc.weflow.net.GsonResponseObject;
import com.etoc.weflow.net.GsonResponseObject.RechargePhoneResp;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.ViewUtils;

public class RechargePhoneFragment extends Fragment {
	private View mView;
	GridView gvPhoneMenu = null;
	RechargeAdapter adapter = null;
	TextView tvCostCoins = null;
	List<RechargePhoneResp> itemList = new ArrayList<GsonResponseObject.RechargePhoneResp>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d("=AAA=","this = " + this);
		if(mView != null){
		    ViewGroup parent = (ViewGroup) mView.getParent();  
		    if (parent != null) {  
		        parent.removeView(mView);  
		    }   
		    return mView;
		}
		
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_recharge_phone, null);
		mView = v;
		initView(v);
		
		return v;
	}
	
	private void initView(View view) {
		gvPhoneMenu = (GridView) view.findViewById(R.id.gv_menu);
		
		int [] moneys = {10,20,30,50,100,200};
		
		if (itemList.size() == 0) {
			for (int i = 0;i < 6;i++) {
				RechargePhoneResp resp = new RechargePhoneResp();
				resp.chargesid = "" + i;
				resp.money = moneys[i] + "元";
				resp.cost = moneys[i] * 100 + "";
				itemList.add(resp);
			}
		}
		
		adapter = new RechargeAdapter(getActivity(), itemList);
		gvPhoneMenu.setAdapter(adapter);
		
		gvPhoneMenu.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		gvPhoneMenu.setOnItemClickListener(new OnItemClickListener() {

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
