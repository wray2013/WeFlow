package com.cmmobi.looklook.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiscoverMainActivity;




public class VideoListFragment extends Fragment {
	private final static String TAG = "VideoListFragment";
	
	DiscoverMainActivity mActivity;

	// UI components



	public VideoListFragment() {
	}

	// Called once the Fragment has been created in order for it to
	// create its user interface.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, "NearByFragment - onCreateView");
		// Create, or inflate the Fragment's UI, and return it.
		// If this Fragment has no UI then return null.
		View view = inflater.inflate(R.layout.fragment_discover_list, container, false);



		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e(TAG, "NearByFragment - onActivityCreated");
/*		mNext.setOnClickListener(new OnClickListener() {});*/
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.e(TAG, "NearByFragment - onAttach");

		try {
			mActivity = (DiscoverMainActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}
	
    @Override
	public void onPause() {

        super.onPause();

    }
    
    @Override
	public void onResume() {

        super.onResume();

    }
    
    
    @Override
	public void onDestroyView() {

        super.onDestroyView();
    }

}
