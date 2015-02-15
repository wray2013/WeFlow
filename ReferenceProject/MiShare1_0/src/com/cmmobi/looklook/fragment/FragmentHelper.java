package com.cmmobi.looklook.fragment;


import android.support.v4.app.FragmentActivity;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-11-7
 */
public class FragmentHelper {

	private static FragmentHelper helper;
	private FragmentActivity fragmentActivity;

	private FragmentHelper() {
	}

	public static FragmentHelper getInstance(FragmentActivity fragmentActivity) {
		if (null == helper)
			helper = new FragmentHelper();
		helper.fragmentActivity = fragmentActivity;
		return helper;
	}

	// 获取ZoneBaseFragment
	public ZoneBaseFragment getZoneBaseFragment() {
		ZoneBaseFragment fragment = null;
		if (fragmentActivity != null) {
			fragment = (ZoneBaseFragment) fragmentActivity
					.getSupportFragmentManager().findFragmentByTag(
							ZoneBaseFragment.class.getName());
		}
		if (null == fragment)
			fragment = new ZoneBaseFragment();
		return fragment;
	}
	
	// 获取NetworkTaskFragment
	public NetworkTaskFragment getNetworkTaskFragment() {
		NetworkTaskFragment fragment = null;
		if (fragmentActivity != null) {
			fragment = (NetworkTaskFragment) fragmentActivity
					.getSupportFragmentManager().findFragmentByTag(
							NetworkTaskFragment.class.getName());
		}
		if (null == fragment)
			fragment = new NetworkTaskFragment();
		return fragment;
	}
	
	private static MyZoneFragment myZoneFragment;
	// 获取MyZoneFragment
	public MyZoneFragment getMyZoneFragment() {
//		MyZoneFragment fragment = null;
//		if (fragmentActivity != null) {
//			fragment = (MyZoneFragment) fragmentActivity
//					.getSupportFragmentManager().findFragmentByTag(
//							MyZoneFragment.class.getName());
//		}
		if (null == myZoneFragment)
			myZoneFragment = new MyZoneFragment();
		return myZoneFragment;
	}

	// 获取SafeboxFragment
	public SafeboxMeFragment getSafeboxMeFragment() {
		SafeboxMeFragment fragment = null;
		if (fragmentActivity != null) {
			fragment = (SafeboxMeFragment) fragmentActivity
					.getSupportFragmentManager().findFragmentByTag(
							SafeboxMeFragment.class.getName());
		}
		if (null == fragment)
			fragment = new SafeboxMeFragment();
		return fragment;
	}
	
	// 获取SafeboxContentFragment
	public SafeboxContentFragment getSafeboxContentFragment() {
		SafeboxContentFragment fragment = null;
		if (fragmentActivity != null) {
			fragment = (SafeboxContentFragment) fragmentActivity
					.getSupportFragmentManager().findFragmentByTag(
							SafeboxContentFragment.class.getName());
		}
		if (null == fragment)
			fragment = new SafeboxContentFragment();
		return fragment;
	}
	
	// 获取SafeboxVShareFragment
	public SafeboxVShareFragment getSafeboxVShareFragment() {
		SafeboxVShareFragment fragment = null;
		if (fragmentActivity != null) {
			fragment = (SafeboxVShareFragment) fragmentActivity
					.getSupportFragmentManager().findFragmentByTag(
							SafeboxVShareFragment.class.getName());
		}
		if (null == fragment)
			fragment = new SafeboxVShareFragment();
		return fragment;
	}

	// 获取FriendsFragment
	public FriendsFragment getFriendsFragment() {
		FriendsFragment fragment = null;
		if (fragmentActivity != null) {
			fragment = (FriendsFragment) fragmentActivity
					.getSupportFragmentManager().findFragmentByTag(
							FriendsFragment.class.getName());
		}
		if (null == fragment)
			fragment = new FriendsFragment();
		return fragment;
	}

	private static VShareFragment vShareFragment;
	// 获取VShareFragment
	public VShareFragment getVShareFragment() {
//		VShareFragment fragment = null;
//		if (fragmentActivity != null) {
//			fragment = (VShareFragment) fragmentActivity
//					.getSupportFragmentManager().findFragmentByTag(
//							VShareFragment.class.getName());
//		}
		if (null == vShareFragment)
			vShareFragment = new VShareFragment();
		return vShareFragment;
	}

	// 获取MyZoneSubscribeFragment
	public MyZoneSubscribeFragment getSubscribeFragment() {
		MyZoneSubscribeFragment fragment = null;
		if (fragmentActivity != null) {
			fragment = (MyZoneSubscribeFragment) fragmentActivity
					.getSupportFragmentManager().findFragmentByTag(
							MyZoneSubscribeFragment.class.getName());
		}
		if (null == fragment)
			fragment = new MyZoneSubscribeFragment();
		return fragment;
	}

	/*// 获取ActivityListFragment
	public ActivityListFragment getActivityListFragment() {
		ActivityListFragment fragment = null;
		if (fragmentActivity != null) {
			fragment = (ActivityListFragment) fragmentActivity
					.getSupportFragmentManager().findFragmentByTag(
							ActivityListFragment.class.getName());
		}
		if (null == fragment)
			fragment = new ActivityListFragment();
		return fragment;
	}*/

	// 获取SettingFragment
	public SettingFragment getSettingFragment() {
		SettingFragment fragment = null;
		if (fragmentActivity != null) {
			fragment = (SettingFragment) fragmentActivity
					.getSupportFragmentManager().findFragmentByTag(
							SettingFragment.class.getName());
		}
		if (null == fragment)
			fragment = new SettingFragment();
		return fragment;
	}

	// 获取FriendNewsFragment 好友动态
	public FriendNewsFragment getFriendNewsFragment() {
		FriendNewsFragment fragment = null;
		if (fragmentActivity != null) {
			fragment = (FriendNewsFragment) fragmentActivity
					.getSupportFragmentManager().findFragmentByTag(
							FriendNewsFragment.class.getName());
		}
		if (null == fragment)
			fragment = new FriendNewsFragment();
		return fragment;
	}
	
	// 获取CollectionFragment(收藏)
	/*public CollectionsFragment getCollectionsFragment() {
		CollectionsFragment fragment = null;
		if (fragmentActivity != null) {
			fragment = (CollectionsFragment) fragmentActivity
					.getSupportFragmentManager().findFragmentByTag(
							CollectionsFragment.class.getName());
		}
		if (null == fragment)
			fragment = new CollectionsFragment();
		return fragment;
	}
*/
	// 获取CommentFragment(评论)
/*	public CommentsFragment getCommentsFragment() {
		CommentsFragment fragment = null;
		if (fragmentActivity != null) {
			fragment = (CommentsFragment) fragmentActivity
					.getSupportFragmentManager().findFragmentByTag(
							CommentsFragment.class.getName());
		}
		if (null == fragment)
			fragment = new CommentsFragment();
		return fragment;
	}*/
	
	// 获取FriendsContactsFragment
	public FriendsContactsFragment getFriendsContactsFragment(int tab) {
		FriendsContactsFragment fragment = null;
		if (fragmentActivity != null) {
			fragment = (FriendsContactsFragment) fragmentActivity
					.getSupportFragmentManager().findFragmentByTag(
							FriendsContactsFragment.class.getName());
		}
		if (null == fragment)
			fragment = new FriendsContactsFragment(tab);
		return fragment;
	}
	
	// 获取FriendAddFragment
	public FriendsMessageFragment getFriendsMessageFragment() {
		FriendsMessageFragment fragment = null;
		if (fragmentActivity != null) {
			fragment = (FriendsMessageFragment) fragmentActivity
					.getSupportFragmentManager().findFragmentByTag(
							FriendsMessageFragment.class.getName());
		}
		if (null == fragment)
			fragment = new FriendsMessageFragment();
		return fragment;
	}
	
}
