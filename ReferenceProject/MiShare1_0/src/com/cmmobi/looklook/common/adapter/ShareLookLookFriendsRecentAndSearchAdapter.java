package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.baidu.platform.comapi.map.o;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.UserObj;
import com.cmmobi.looklook.common.view.ContactsComparator;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class ShareLookLookFriendsRecentAndSearchAdapter extends BaseAdapter{

	private List<WrapUser> wrapUsers = new ArrayList<WrapUser>();
	private LayoutInflater inflater;

	private Context context;
	private ContactsComparator cmp;

	private List<Boolean> list_selected = new ArrayList<Boolean>();
	public List<UserObj>  to_invite = new ArrayList<UserObj>();
	private Handler handler;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	public ShareLookLookFriendsRecentAndSearchAdapter(Context context, Handler handler) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.handler = handler;
		cmp = new ContactsComparator();
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren_touxiang)
		.showImageForEmptyUri(R.drawable.moren_touxiang)
		.showImageOnFail(R.drawable.moren_touxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.build();
	}

	@Override
	public int getCount() {
		if(wrapUsers!=null){
			return wrapUsers.size();
		}else{
			return 0;
		}
	}
	public void setData(List<WrapUser> wrapUsers, List<UserObj> selected) {
		if(wrapUsers == null){
			return;
		}
		this.wrapUsers.clear();
		this.wrapUsers.addAll(wrapUsers);
		this.to_invite.clear();
		this.list_selected.clear();
		
		for(int i=0; i< this.wrapUsers.size(); i++){
			String id = wrapUsers.get(i).userid;
			String telString = wrapUsers.get(i).phonenum;
			boolean flag = false;
			for(UserObj str : selected)
			{
				if((id != null && id.equals(str.userid)) || (telString !=null && telString.equals(str.user_tel)))
				{
					flag = true;
					this.to_invite.add(str);
					break;
				}
			}
			this.list_selected.add(flag);
		}
		
	}

	@Override
	public WrapUser getItem(int position) {
		return wrapUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void updateUI(String userid) {
		
		
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_contacts, null);
			viewHolder = new ViewHolder();
			viewHolder.contactsImageView = (ImageView) convertView
					.findViewById(R.id.contacts_image);
			viewHolder.contactsTextView = (TextView) convertView
					.findViewById(R.id.contacts_text);
			viewHolder.removeImageView = (ImageView) convertView
					.findViewById(R.id.iv_add_cancel);

			viewHolder.nicknameTextView = (TextView) convertView.findViewById(R.id.tv_nickname);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.contactsImageView.setTag(wrapUsers.get(position).userid);
	
		if(list_selected.get(position)){
			viewHolder.removeImageView.setImageResource(R.drawable.xuanzhong_invite_2);
		}else{
			viewHolder.removeImageView.setImageResource(R.drawable.xuanzhong_invite_1);
		}

		if(wrapUsers.get(position).markname != null && !wrapUsers.get(position).markname.equals("")){
			/*viewHolder.contactsTextView
			.setText(wrapUsers.get(position).markname);
			viewHolder.nicknameTextView.setText("昵称:" + wrapUsers.get(position).nickname);*/
        	FriendsExpressionView.replacedExpressions(wrapUsers.get(position).markname, viewHolder.contactsTextView);
        	if(wrapUsers.get(position).nickname!=null && !wrapUsers.get(position).nickname.isEmpty()){
            	FriendsExpressionView.replacedExpressions("昵称:" + wrapUsers.get(position).nickname, viewHolder.nicknameTextView);	
        	}else{
        		viewHolder.nicknameTextView.setText("");
        	}
		}else if(wrapUsers.get(position).nickname != null && !wrapUsers.get(position).nickname.equals("")){
			/*viewHolder.contactsTextView
			.setText(wrapUsers.get(position).nickname);*/
			FriendsExpressionView.replacedExpressions(wrapUsers.get(position).nickname, viewHolder.contactsTextView);
			viewHolder.nicknameTextView.setText("");
        }else if(wrapUsers.get(position).phonename != null && !wrapUsers.get(position).phonename.equals("")){
        	/*viewHolder.contactsTextView
			.setText(wrapUsers.get(position).phonename);*/
        	FriendsExpressionView.replacedExpressions(wrapUsers.get(position).phonename, viewHolder.contactsTextView);
			viewHolder.nicknameTextView.setText(Html.fromHtml("<font color=\"gray\">来自 </font>" + "手机通讯录：" + wrapUsers.get(position).phonename));
        }else if(wrapUsers.get(position).telname != null && !wrapUsers.get(position).telname.equals("")){
        	/*viewHolder.contactsTextView
			.setText(wrapUsers.get(position).phonename);*/
        	FriendsExpressionView.replacedExpressions(wrapUsers.get(position).telname, viewHolder.contactsTextView);
			viewHolder.nicknameTextView.setText("");
        }else if(wrapUsers.get(position).micnum !=null){
        	viewHolder.contactsTextView.setText(wrapUsers.get(position).micnum);
        	viewHolder.nicknameTextView.setText("");
        }else{
        	viewHolder.contactsTextView.setText("");
        	viewHolder.nicknameTextView.setText("");
        }
		
		if(wrapUsers.get(position)!=null && wrapUsers.get(position).headimageurl!=null){	
			imageLoader.displayImageEx(wrapUsers.get(position).headimageurl, viewHolder.contactsImageView, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
		}else{
			viewHolder.contactsImageView.setImageResource(R.drawable.moren_touxiang);
		}

		return convertView;
	}

	public void setItemSelected(View view,int position) {
		ViewHolder viewHolder = (ViewHolder)view.getTag();
		final WrapUser item = wrapUsers.get(position);
		if (viewHolder == null || item==null || (item.nickname==null && item.markname == null && item.phonename == null)) {
			return;
		}
		if(list_selected.get(position)){
			list_selected.set(position, false); //checked -> normal
			viewHolder.removeImageView.setImageResource(R.drawable.xuanzhong_invite_1);
		}else{
			if(to_invite.size() < 8)
			{
				list_selected.set(position, true); // normal -> checked
				viewHolder.removeImageView.setImageResource(R.drawable.xuanzhong_invite_2);
			}
			else
			{
				new Xdialog.Builder(context)
				.setMessage(context.getString(R.string.share_error_user_max))
				.setNegativeButton(android.R.string.ok, null)
				.create().show();
			}
		}
		
		to_invite.clear();
		for(int i=0; i< list_selected.size(); i++){
			if(list_selected.get(i)){
				UserObj obj = new UserObj();
				obj.userid = wrapUsers.get(i).userid;
				if(wrapUsers.get(i).markname != null && !wrapUsers.get(i).markname.isEmpty()){
					obj.user_telname = wrapUsers.get(i).markname;
					obj.headimageurl = wrapUsers.get(i).headimageurl;
					obj.mic_source = "1";
				}else if(wrapUsers.get(i).nickname != null && !wrapUsers.get(i).nickname.isEmpty()){
					obj.user_telname = wrapUsers.get(i).nickname;
					obj.headimageurl = wrapUsers.get(i).headimageurl;
					obj.mic_source = "1";
				}else if(wrapUsers.get(i).phonename != null && !wrapUsers.get(i).phonename.isEmpty()){
					obj.user_telname = wrapUsers.get(i).phonename;
					obj.mic_source = "2";
					obj.user_tel = wrapUsers.get(i).phonenum;
				}
				
				to_invite.add(obj);
			}
		}

		/*Message msg = handler.obtainMessage(ShareLookLookFriendsActivity.HANDLER_FLAG_CONTACTS_CHECK_CHANGED);
		msg.sendToTarget();*/
	}
	
	class ViewHolder {
		ImageView contactsImageView;
		TextView contactsTextView;
		ImageView removeImageView;
		TextView nicknameTextView;
	}
}
