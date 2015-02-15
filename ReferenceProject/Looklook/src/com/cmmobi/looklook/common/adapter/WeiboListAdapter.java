package com.cmmobi.looklook.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;

public class WeiboListAdapter extends ArrayAdapter<String> {
	private Context context;
	String[] weibo;
	LayoutInflater inflater;

	public WeiboListAdapter(Context context, int resource,
			int textViewResourceId, String[] objects) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		weibo = context.getResources().getStringArray(R.array.weibo_invite);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final int pos = position;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_friends_add, null);
			holder = new ViewHolder();
			holder.logo = (ImageView) convertView.findViewById(R.id.iv_row_friends_add_weibo);
			holder.weibo = (TextView) convertView.findViewById(R.id.tv_row_friends_add_weibo);
			holder.rl_row = (RelativeLayout) convertView.findViewById(R.id.rl_row_friends_add_weibo);
			convertView.setTag(holder);
		} else {
            holder = (ViewHolder) convertView.getTag();
        }

		String item = getItem(position);
		
		if(item.equals("sina")){
			holder.logo.setImageResource(R.drawable.sina_logo);
			
		}else if(item.equals("renren")){
			holder.logo.setImageResource(R.drawable.renren_logo);
		}
		else if(item.equals("tencent")){
			holder.logo.setImageResource(R.drawable.tencent_logo);
		}else{
			holder.logo.setImageResource(R.drawable.tencent_logo);
		}
		
		holder.weibo.setText(weibo[position]);
		
		//set click event
/*		holder.rl_row.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(weibo[pos].equals("sina")){
					
				}
			}
			
		});*/
		
		return convertView;
	}
	
    static class ViewHolder {
        // CheckBox selectItemCheckBox ;
       TextView weibo;
       ImageView logo;
       RelativeLayout rl_row;
    }


}
